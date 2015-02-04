/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.execution;


import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.shipping.execution.Item;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 * @author Alessandro
 */
@Stateless(name = "shipping.execution.ItemDao")
public class ItemDao extends BaseDao<Item> {

    @Inject 
    ComponentDao componentDao;
    
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
            });
        }
    }
    
    @Override
    public Item create(
            @NotNull(message = ENTITY_VALIDATION) Item entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Create Shipping Execution Item is not permitted.");
    }

    @Override
    public Item update(
            @NotNull(message = ENTITY_VALIDATION) Item entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Update Shipping Execution Item is not permitted.");
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
            @NotNull List<Item> entities)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Delete Shipping Execution Item is not permitted.");
    }

}
