/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.shipping.execution.Delivered;
import br.com.altamira.data.model.shipping.planning.BOM;
import br.com.altamira.data.model.shipping.planning.Component;
import br.com.altamira.data.model.shipping.planning.Delivery;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
@Stateless(name = "shipping.planning.ComponentDao")
public class ComponentDao extends BaseDao<Component> {

    @Inject
    DeliveryDao deliveryDao;

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Component entity) {
        // Lazy load of items
        if (entity.getMaterial() != null) {
            entity.getMaterial().setComponent(null);
        }

    }
    
    @Override
    public Component create(
            @NotNull(message = ENTITY_VALIDATION) Component entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Create Shipping Planning Component is not permitted.");
    }

    @Override
    public Component update(
            @NotNull(message = ENTITY_VALIDATION) Component entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Update Shipping Planning Component is not permitted.");
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

        throw new UnsupportedOperationException("Delete Shipping Planning Component is not permitted.");
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

        throw new UnsupportedOperationException("Delete Shipping Planning Component is not permitted.");
    }

}
