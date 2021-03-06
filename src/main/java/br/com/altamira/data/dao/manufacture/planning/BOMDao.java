package br.com.altamira.data.dao.manufacture.planning;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.manufacture.planning.BOM;
import br.com.altamira.data.model.manufacture.planning.BOM_;
import br.com.altamira.data.model.manufacture.planning.Component;
import br.com.altamira.data.model.manufacture.planning.Component_;
import br.com.altamira.data.model.manufacture.planning.Item;
import br.com.altamira.data.model.manufacture.planning.Item_;
import br.com.altamira.data.model.manufacture.planning.Order;
import br.com.altamira.data.model.manufacture.planning.Order_;
import br.com.altamira.data.model.manufacture.planning.Produce;
import br.com.altamira.data.model.manufacture.planning.Produce_;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
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
@Stateless(name = "br.com.altamira.data.dao.manufacture.planning.BOMDao")
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

        SetJoin<BOM, Item> item = bom.join(BOM_.item);
        SetJoin<Item, Component> component = item.join(Item_.component);
        ListJoin<Component, Produce> produce = component.join(Component_.produce);
        Join<Produce, Order> orderProduce = produce.join(Produce_.order);

        criteriaQuery.select(cb.construct(BOM.class, 
        		bom.get(BOM_.id),
        		bom.get(BOM_.type),
        		bom.get(BOM_.number),
        		bom.get(BOM_.customer),
        		bom.get(BOM_.created),
        		bom.get(BOM_.delivery))).distinct(true);
        
        criteriaQuery.where(cb.equal(orderProduce.get(Order_.id), parameters.get("id").get(0)));

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()
                && !parameters.get("search").get(0).isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(bom.get(BOM_.number).as(String.class)), searchCriteria),
                    cb.like(cb.lower(bom.get(BOM_.customer)), searchCriteria)));
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
                    component.getProduce().size();
                });
            });
        }
    }

    /**
     *
     * @param parameters
     * @return
     */
    /*public CriteriaQuery<Remaining> getRemainingQuery(@NotNull MultivaluedMap<String, String> parameters) {
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
    }*/

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    /*public List<Remaining> listRemaining(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        return entityManager.createQuery(this.getRemainingQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();
    }*/

    
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
