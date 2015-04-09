/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.planning;

import java.math.BigDecimal;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacture.planning.Component;
import br.com.altamira.data.model.manufacture.planning.Component_;
import br.com.altamira.data.model.manufacture.planning.Order;
import br.com.altamira.data.model.manufacture.planning.Produce;
import br.com.altamira.data.model.manufacture.planning.Produce_;
import br.com.altamira.data.model.measurement.Measure_;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.planning.ProduceDao")
public class ProduceDao extends BaseDao<Produce> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Produce entity) {
        // Lazy load of items
        if (entity.getOrder() != null) {
            entity.getOrder().setProduce(null);
        }
    }
    
    @Override
    public void resolveDependencies(Produce entity, MultivaluedMap<String, String> parameters)
            throws IllegalArgumentException {

        //entity.setOrder(entityManager.find(Order.class, entity.getOrder().getId()));
    	entity.setOrder(entityManager.find(Order.class, Long.parseLong(parameters.get("id").get(0))));
        entity.setComponent(entityManager.find(Component.class, Long.parseLong(parameters.get("id").get(3))));
    }
    
    //ALTAMIRA-180: Manufacture Planning - calculate produced and remaining fields
    @Override
    public void updateDependencies(Produce entity, MultivaluedMap<String, String> parameters)
    		throws IllegalArgumentException {
    	
    	calculateProducedAndRemaining(entity.getComponent());
    }
    
    @Override
    public void remove(long id) throws ConstraintViolationException, IllegalArgumentException {

    	Component component = entityManager.find(Produce.class, id).getComponent();
    	super.remove(id);

    	calculateProducedAndRemaining(component);
    }
    
    private void calculateProducedAndRemaining(Component entity) {
    	
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	CriteriaQuery<BigDecimal> criteria = cb.createQuery(BigDecimal.class);
    	Root<Produce> root = criteria.from(Produce.class);
    	criteria.select(cb.sum(root.get(Produce_.quantity).get(Measure_.value)));
    	criteria.where(cb.equal(root.get(Produce_.component).get(Component_.id), entity.getId()));
    	
    	BigDecimal totalProduceQty = entityManager.createQuery(criteria).getSingleResult();
    	entity.getProduced().setValue(totalProduceQty);
    	entity.getRemaining().setValue(entity.getQuantity().getValue().subtract(totalProduceQty));
    	
    	entityManager.persist(entity);
    	entityManager.flush();
    }

}
