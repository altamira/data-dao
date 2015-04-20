/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.planning.ComponentDao")
public class ComponentDao extends BaseDao<Component> {

    /**
     *
     * @param parameters
     * @return
     */
	@Override
	public CriteriaQuery<Component> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();

		CriteriaQuery<Component> criteriaQuery = cb.createQuery(Component.class);
		Root<Order> order = criteriaQuery.from(Order.class);

		ListJoin<Order, Produce> produce = order.join(Order_.produce);
		Join<Produce, Component> component = produce.join(Produce_.component);
		Join<Component, Item> item = component.join(Component_.item);
		Join<Item, BOM> bom = item.join(Item_.bom);

		criteriaQuery.select(component).distinct(true);

		criteriaQuery.where(cb.equal(order.get(Order_.id), parameters.get("id").get(0)),
				cb.equal(bom.get(BOM_.id), parameters.get("id").get(1)),
				cb.equal(item.get(Item_.id), parameters.get("id").get(2)));

		return criteriaQuery;
	}
    
    @Override
    public List<Component> list(@NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
    							@Min(value = 0, message = START_PAGE_VALIDATION) int startPage, 
    							@Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize) 
    							throws ConstraintViolationException {
    	
    	List<Component> list = super.list(parameters, startPage, pageSize);
    	
    	// lazyload materials
    	list.stream().forEach((component) -> {
    		component.getMaterial().getId();
    	});
    	
    	return list;
    }
    
    @Override
    public Component create(
            @NotNull(message = ENTITY_VALIDATION) Component entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Create Shipping Execution Component is not permitted.");
    }

    @Override
    public Component update(
            @NotNull(message = ENTITY_VALIDATION) Component entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Update Shipping Execution Component is not permitted.");
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

        throw new UnsupportedOperationException("Delete Shipping Execution Component is not permitted.");
    }

    /**
     *
     * @param entities
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    @Override
    public void removeAll(
            @NotNull List<Component> entities)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Delete Shipping Execution Component is not permitted.");
    }

}
