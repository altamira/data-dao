package br.com.altamira.data.dao.shipping.execution;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.shipping.execution.BOM;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Sales bom persistency strategy
 *
 */
@Stateless(name = "shipping.execution.BOMDao")
public class BOMDao extends BaseDao<BOM> {

    /**
     *
     * @param parameters
     * @return
     */
    /*@Override
     public CriteriaQuery<BOM> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
     CriteriaBuilder cb = entityManager.getCriteriaBuilder();
     CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);

     Root<BOM> bom = criteriaQuery.from(BOM.class);

     // TODO filter only remaining delivery dates
    
     criteriaQuery.select(bom);

     return criteriaQuery;
     }*/
    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(BOM entity) {
        // Lazy load of items
        if (entity.getItem() != null) {
            entity.getItem().size();
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

    /**
     * Replace the component delivery date with the new ones when the old date
     * match
     *
     * @param delivery first one its the old date to be replaced second ones its
     * the new delivery date
     */
    public void replaceDeliveryDates(long id, List<Date> delivery) {

        if (delivery.isEmpty() || delivery.size() != 2) {
            throw new IllegalArgumentException("Two dates are required: first one is the old date to be replaced, second ones is the new date.");
        }

        // TODO replace from old date to the new ones for matching old date = component delivery date
    }

}
