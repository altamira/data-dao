package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.shipping.execution.Delivered;
import br.com.altamira.data.model.shipping.planning.BOM;
import br.com.altamira.data.model.shipping.planning.Component;
import br.com.altamira.data.model.shipping.planning.Delivery;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

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
                    
                    calculateRemainingAndDeliveredAmounts(component);
                });
            });
        }
    }
    
    private void calculateRemainingAndDeliveredAmounts(Component entity)
    {
    	/* ALTAMIRA-22: Shipping Planning - amount remaining calculation */
        
        // Retrieve the amount of delivered item quantities
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<BigDecimal> criteria = builder.createQuery(BigDecimal.class);
    	Root<Delivered> root = criteria.from(Delivered.class);
    	Expression<BigDecimal> sum = builder.sum(root.get("quantity").get("value"));
    	criteria.select(sum);
    	criteria.where(builder.equal(root.get("component").get("id"), entity.getId()));
    	BigDecimal amt_delivered = entityManager.createQuery(criteria).getSingleResult();
    	amt_delivered = (amt_delivered==null) ? BigDecimal.ZERO : amt_delivered;
    	
    	Unit unitEntity = entity.getQuantity().getUnit();

    	Measure measureEntity = new Measure();
    	measureEntity.setValue(amt_delivered);
    	measureEntity.setUnit(unitEntity);
    	entity.setDelivered(measureEntity);

    	Set<Delivery> deliverySet = entity.getDelivery();
    	TreeSet<Delivery> treeSet = new TreeSet<Delivery>(deliverySet);
    	Iterator<Delivery> iterator = treeSet.iterator();
    	
    	while( amt_delivered.compareTo(BigDecimal.ZERO)>0 && iterator.hasNext() )
    	{
    		Delivery delivery = iterator.next();
    		
    		BigDecimal quantityDelivery = delivery.getQuantity().getValue();
    		Measure measure = new Measure();
    		measure.setValue(quantityDelivery.min(amt_delivered));
    		measure.setUnit(unitEntity);
    		delivery.setDelivered(measure);
    		
    		amt_delivered = amt_delivered.subtract(quantityDelivery.min(amt_delivered));
    	}
    	
    	entity.setDelivery(treeSet);
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
