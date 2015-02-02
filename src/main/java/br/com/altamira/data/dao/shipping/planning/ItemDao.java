/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.shipping.execution.Delivered;
import br.com.altamira.data.model.shipping.planning.Component;
import br.com.altamira.data.model.shipping.planning.Delivery;
import br.com.altamira.data.model.shipping.planning.Item;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

/**
 *
 *
 * @author Alessandro
 */
@Stateless(name = "shipping.planning.ItemDao")
public class ItemDao extends BaseDao<Item> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Item entity) {
        // Lazy load of items
        if (entity.getComponent() != null) {
            entity.getComponent().size();
            entity.getComponent().stream().forEach((component) -> {
                if (component.getMaterial() != null) {
                    component.getMaterial().setComponent(null);
                }
                
                calculateRemainingAndDeliveredAmounts(component);
                
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

}
