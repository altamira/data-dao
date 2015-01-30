/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import java.math.BigDecimal;
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
        
        // Retrieve the set of delivered items
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Delivered> criteria = builder.createQuery(Delivered.class);
    	Root<Delivered> root = criteria.from(Delivered.class);
    	criteria.select(root);
    	criteria.where(builder.equal(root.get("component").get("id"), entity.getId()));
    	List<Delivered> deliveredList = entityManager.createQuery(criteria).getResultList();

    	Unit unitEntity = entity.getQuantity().getUnit();
    	
    	if( deliveredList == null || deliveredList.isEmpty() )
    	{
    		Set<Delivery> deliverySet = entity.getDelivery();
            TreeSet<Delivery> treeSet = new TreeSet<>(deliverySet);
            
            BigDecimal amt_delivered = BigDecimal.ZERO;
            BigDecimal amt_remaining = BigDecimal.ZERO;
            
            Measure delivered = new Measure();
            delivered.setValue(amt_delivered);
            delivered.setUnit(unitEntity);
            
            for( Delivery delivery : treeSet )
            {
            	Measure quantityDelivery = delivery.getQuantity();
            	
            	delivery.setRemaining(quantityDelivery);
            	delivery.setDelivered(delivered);
            	
            	amt_remaining = amt_remaining.add(quantityDelivery.getValue());
            }
            
            Measure remaining = new Measure();
            remaining.setValue(amt_remaining);
            remaining.setUnit(unitEntity);
            
            entity.setDelivered(delivered);
            entity.setRemaining(remaining);
            
    	}
    	else
    	{
    		// Calculate total amount of delivered quantity
        	BigDecimal amt_delivered = BigDecimal.ZERO;
        	for (int i = 0; i < deliveredList.size(); i++) {
        		BigDecimal quantityValuesOFDelivered = deliveredList.get(i).getQuantity().getValue();
        		amt_delivered = amt_delivered.add(quantityValuesOFDelivered);
    		}
        	
        	Measure measureEntity = new Measure();
        	measureEntity.setValue(amt_delivered);
        	measureEntity.setUnit(unitEntity);
        	entity.setDelivered(measureEntity);
        	
        	BigDecimal amt_remaining = BigDecimal.ZERO;
            Set<Delivery> deliverySet = entity.getDelivery();
            TreeSet<Delivery> treeSet = new TreeSet<>(deliverySet);
            
            for( Delivery delivery : treeSet ) 
            {
            	BigDecimal quantityDelivery = delivery.getQuantity().getValue();
            	
            	int result = quantityDelivery.compareTo(amt_delivered);
            	if (result == -1) 
            	{
            		BigDecimal remaining = BigDecimal.ZERO;  
            		BigDecimal delivered = quantityDelivery;
    				amt_delivered = amt_delivered.subtract(quantityDelivery);
    				
    				Measure measure1 = new Measure();
    				measure1.setValue(remaining);
    				measure1.setUnit(unitEntity);
    				delivery.setRemaining(measure1);
    				
    				Measure measure = new Measure();
                	measure.setValue(delivered);
                	measure.setUnit(unitEntity);
    				delivery.setDelivered(measure);
    			}
            	else if (result == 1)
            	{
            		BigDecimal remaining = quantityDelivery.subtract(amt_delivered);
            		BigDecimal delivered = quantityDelivery.subtract(remaining);
            		amt_delivered = BigDecimal.ZERO;
            		
                	Measure measure = new Measure();
                	measure.setValue(remaining);
                	measure.setUnit(unitEntity);
                	delivery.setRemaining(measure);
                	
                	Measure measure1 = new Measure();
                	measure1.setValue(delivered);
                	measure1.setUnit(unitEntity);
                	delivery.setDelivered(measure1);
    			}
            	else if (result == 0) 
            	{
            		BigDecimal delivered = amt_delivered;
            		BigDecimal remaining = quantityDelivery.subtract(delivered);
            		amt_delivered = BigDecimal.ZERO;
            		
                	Measure measure = new Measure();
                	measure.setValue(delivered);
                	measure.setUnit(unitEntity);
    				delivery.setDelivered(measure);
    				
    				Measure measure1 = new Measure();
                	measure1.setValue(remaining);
                	measure1.setUnit(unitEntity);
    				delivery.setRemaining(measure1);
    			}
            	
            	BigDecimal remainingQuantity = delivery.getRemaining().getValue();
                amt_remaining = amt_remaining.add(remainingQuantity);
            }
            
            entity.setDelivery(treeSet);
            
            Measure measureEntity1 = new Measure(); 
        	measureEntity1.setValue(amt_remaining);
        	measureEntity1.setUnit(unitEntity);
        	entity.setRemaining(measureEntity1);
    	}
    }

}
