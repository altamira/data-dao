package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import static br.com.altamira.data.dao.Dao.PAGE_SIZE_VALIDATION;
import static br.com.altamira.data.dao.Dao.PARAMETER_VALIDATION;
import static br.com.altamira.data.dao.Dao.START_PAGE_VALIDATION;
import br.com.altamira.data.model.shipping.planning.BOM_;
import br.com.altamira.data.model.shipping.planning.BOM;
import br.com.altamira.data.model.shipping.planning.Component;
import br.com.altamira.data.model.shipping.planning.Delivery;
import br.com.altamira.data.model.measurement.Measure_;
import br.com.altamira.data.model.shipping.planning.Delivery_;
import br.com.altamira.data.model.shipping.planning.Component_;
import br.com.altamira.data.model.shipping.planning.Item;
import br.com.altamira.data.model.shipping.planning.Item_;
import br.com.altamira.data.model.shipping.planning.Remaining;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Sales bom persistency strategy
 *
 */
@Stateless(name = "shipping.planning.BOMDao")
public class BOMDao extends BaseDao<BOM> {

    @Inject
    ComponentDao componentDao;

    @Inject
    Logger log;

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<BOM> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);

        SetJoin<BOM, Item> item = bom.join(BOM_.item);
        SetJoin<Item, Component> component = item.join(Item_.component);
        SetJoin<Component, Delivery> delivery = component.join(Component_.delivery);

        /* ALTAMIRA-56, ALTAMIRA-82: remove this to get only the BOM headers list
         Fetch<BOM, Item> fetch = bom.fetch("item");
         SetJoin<BOM, Item> item = (SetJoin<BOM, Item>) fetch;

         Subquery<Long> subQuery = criteriaQuery.subquery(Long.class);
         Root<Component> component = subQuery.from(Component.class);
         subQuery.select(item.get("id"));
         subQuery.where(cb.and(
         cb.equal(component.get("item").get("id"), item.get("id")), 
         cb.gt(item.get("id"), 0)));

         subQuery.groupBy(component.get("item").get("id"));
         subQuery.having( 
         cb.gt( 
         cb.sum(component.get("quantity").get("value")), 
         cb.sum(component.get("delivered").get("value")) ) );
         */
        criteriaQuery.select(cb.construct(BOM.class,
                bom.get(BOM_.id),
                bom.get(BOM_.number),
                bom.get(BOM_.customer),
                bom.get(BOM_.created),
                bom.get(BOM_.delivery))).distinct(true);

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()
                && !parameters.get("search").get(0).isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(bom.get(BOM_.number).as(String.class)), searchCriteria),
                    cb.like(cb.lower(bom.get(BOM_.customer)), searchCriteria)));
        } else {
            //criteriaQuery.where(cb.equal(item.get("id"), subQuery));
            criteriaQuery.where(cb.and(
                    cb.gt(delivery.get(Delivery_.remaining).get(Measure_.value), 0),
                    cb.isNotNull(bom.get(BOM_.checked)),
                    cb.gt(item.get(Item_.item), 0)));
        }
        
        criteriaQuery.orderBy(cb.asc(bom.get(BOM_.delivery)));

        return criteriaQuery;
    }

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(BOM entity) {
        // Lazy load of items
        if (entity.getItem() != null) {
            entity.getItem().size();

            //ALTAMIRA-76: hides ITEM 0 from materials list 
            entity.getItem().removeIf(p -> p.getItem() == 0);

            entity.getItem().stream().forEach((item) -> {
                item.getComponent().size();
                item.getComponent().stream().forEach((component) -> {
                    component.getMaterial().setComponent(null);
                    component.getDelivery().size();
                });
            });
        }
    }

    @Override
    public BOM create(
            @NotNull(message = ENTITY_VALIDATION) BOM entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Create root Shipping Planning is not permitted.");
    }

    @Override
    public BOM update(
            @NotNull(message = ENTITY_VALIDATION) BOM entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Update root Shipping Planning is not permitted.");
    }

    /**
     *
     * @param id
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    @Override
    public void remove(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Delete root Shipping Planning is not permitted.");
    }

    /**
     *
     * @param entities
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    @Override
    public void removeAll(
            @NotNull List<BOM> entities)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Delete root Shipping Planning is not permitted.");
    }

    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<Remaining> getRemainingQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Remaining> criteriaQuery = cb.createQuery(Remaining.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);

        SetJoin<BOM, Item> item = bom.join(BOM_.item);
        SetJoin<Item, Component> component = item.join(Item_.component);
        SetJoin<Component, Delivery> delivery = component.join(Component_.delivery);

        // TODO: to increase performance and provide a better data structure to the client (BOM -> [1 to N] -> Delivery Date),
        //       just read remaining delivery dates > 0 from database 
        //       and do the group by BOM.id and summarize Remaining by Delivery Date using Java 8 streams and lambda expression 
        //       http://jaxenter.com/sql-group-by-aggregations-java-8-114509.html
        criteriaQuery.select(cb.construct(Remaining.class,
                bom.get(BOM_.id),
                delivery.get(Delivery_.delivery),
                cb.sum(delivery.get(Delivery_.remaining).get(Measure_.value))));

        criteriaQuery.where(cb.and(
                cb.gt(delivery.get(Delivery_.remaining).get(Measure_.value), 0),
                cb.isNotNull(bom.get(BOM_.checked))));

        criteriaQuery.groupBy(bom.get(BOM_.id), delivery.get(Delivery_.delivery));

        criteriaQuery.orderBy(cb.asc(bom.get(BOM_.id)), cb.asc(delivery.get(Delivery_.delivery)));

        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<Remaining> listRemaining(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        return entityManager.createQuery(this.getRemainingQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();
    }

    /**
     * Replace the component delivery date with the new ones when the old date
     * match
     *
     * @param id
     * @param dates first one its the old date to be replaced second ones its
     * the new delivery date
     * @return 
     */
    public Map<String, Long> replaceRemainingDeliveryDates(long id, List<Date> dates) {

        if (dates.isEmpty() || dates.size() != 2) {
            throw new IllegalArgumentException("Two dates are required: first one is the old date to be replaced, second ones is the new date in format yyyy-mm-dd");
        }

        java.sql.Date dt0 = new java.sql.Date(dates.get(0).getTime());
        
        java.sql.Date dt1 = new java.sql.Date(dates.get(1).getTime());
        
        /*
         UPDATE 
         MN_BOM_ITEM_CMP_SH
         SET 
         MN_BOM_ITEM_CMP_SH.DELIVERY = '11/03/15' 
         WHERE
         MN_BOM_ITEM_CMP_SH.ID IN 
         (SELECT 
         MN_BOM_ITEM_CMP_SH.ID
         FROM 
         MN_BOM  INNER JOIN MN_BOM_ITEM        ON MN_BOM.ID =          MN_BOM_ITEM.BOM 
         INNER JOIN MN_BOM_ITEM_CMP    ON MN_BOM_ITEM.ID =     MN_BOM_ITEM_CMP.ITEM 
         INNER JOIN MN_BOM_ITEM_CMP_SH ON MN_BOM_ITEM_CMP.ID = MN_BOM_ITEM_CMP_SH.COMPONENT
         WHERE MN_BOM.ID = 60126 AND MN_BOM_ITEM_CMP_SH.DELIVERY = '11/02/15');
         */
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaUpdate<Delivery> updateQuery = cb.createCriteriaUpdate(Delivery.class);
        Root<Delivery> entity = updateQuery.from(Delivery.class);
        updateQuery.set(entity.get(Delivery_.delivery), dt1);

        // subquery
        //CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);
        Subquery<Long> subQuery = updateQuery.subquery(Long.class);
        Root<BOM> bom = subQuery.from(BOM.class);
        SetJoin<BOM, Item> item = bom.join(BOM_.item);
        SetJoin<Item, Component> component = item.join(Item_.component);
        SetJoin<Component, Delivery> delivery = component.join(Component_.delivery);

        subQuery.select(delivery.get(Delivery_.id));

        subQuery.where(cb.and(
                cb.equal(bom.get(BOM_.id), id),
                cb.equal(delivery.get(Delivery_.delivery), dt0), 
                cb.gt(delivery.get(Delivery_.remaining).get(Measure_.value), 0),
                cb.isNotNull(bom.get(BOM_.checked))));

        log.info("Execution Query String");

        Predicate predicate = entity.get(Delivery_.id).in(subQuery);
        updateQuery.where(predicate);
        Query q = entityManager.createQuery(updateQuery);

        Long count = (long) q.executeUpdate(); 
        
        HashMap<String, Long > result = new HashMap<String, Long>(){{
            put("count", count);
        }};
        
        return result;
    }

}
