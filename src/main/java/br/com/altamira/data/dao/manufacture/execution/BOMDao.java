package br.com.altamira.data.dao.manufacture.execution;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import static br.com.altamira.data.dao.Dao.PAGE_SIZE_VALIDATION;
import static br.com.altamira.data.dao.Dao.PARAMETER_VALIDATION;
import static br.com.altamira.data.dao.Dao.START_PAGE_VALIDATION;
import br.com.altamira.data.model.measurement.Measure_;
import br.com.altamira.data.model.manufacture.execution.BOM;
import br.com.altamira.data.model.manufacture.execution.BOM_;
import br.com.altamira.data.model.manufacture.execution.Component;
import br.com.altamira.data.model.manufacture.execution.Component_;
import br.com.altamira.data.model.manufacture.execution.Delivery;
import br.com.altamira.data.model.manufacture.execution.Item;
import br.com.altamira.data.model.manufacture.execution.Item_;
import br.com.altamira.data.model.manufacture.execution.Remaining;
import br.com.altamira.data.model.manufacture.execution.Delivery_;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Sales bom persistency strategy
 *
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.execution.BOMDao")
public class BOMDao extends BaseDao<BOM> {

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
        //Fetch<BOM, PackingList> fetch = bom.fetch(BOM_.packingList, JoinType.LEFT);
        //SetJoin<BOM, PackingList> packingList = (SetJoin<BOM, PackingList>) fetch;

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
                bom.get(BOM_.delivery)/*,
         packingList.get(PackingList_.id)*/)).distinct(true);

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
        //       and do the group by and summarize using Java 8 streams and lambda expression 
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

    @Override
    public BOM create(
            @NotNull(message = ENTITY_VALIDATION) BOM entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Create Shipping Execution is not permitted.");
    }

    @Override
    public BOM update(
            @NotNull(message = ENTITY_VALIDATION) BOM entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Update Shipping Execution is not permitted.");
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

        throw new UnsupportedOperationException("Delete Shipping Execution Item is not permitted.");
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

        throw new UnsupportedOperationException("Delete Shipping Execution is not permitted.");
    }

}
