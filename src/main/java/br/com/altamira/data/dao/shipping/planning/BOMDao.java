package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.planning.BOM;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

/**
 * Sales bom persistency strategy
 *
 */
@Stateless(name = "shipping.planning.BOMDao")
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

    /**
     * Replace the component delivery date with the new ones when the old date match
     * @param delivery 
     *              first one its the old date to be replaced
     *              second ones its the new delivery date 
     */
    public void replaceDeliveryDates(long id, List<Date> delivery) {
        
        if (delivery.isEmpty() || delivery.size() != 2) {
            throw new IllegalArgumentException("Two dates are required: first one is the old date to be replaced, second ones is the new date.");
        }
        
        // TODO replace from old date to the new ones for matching old date = component delivery date
    }
    
}
