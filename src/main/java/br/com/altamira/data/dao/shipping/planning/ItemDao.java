/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;


import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.shipping.planning.Item_;
import br.com.altamira.data.model.shipping.planning.Item;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
@Stateless(name = "shipping.planning.ItemDao")
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

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Item> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> entity = criteriaQuery.from(Item.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get(Item_.bom),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }    
    
    @Override
    public Item create(
            @NotNull(message = ENTITY_VALIDATION) Item entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Create Shipping Planning Item is not permitted.");
    }

    @Override
    public Item update(
            @NotNull(message = ENTITY_VALIDATION) Item entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Update Shipping Planning Item is not permitted.");
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

        throw new UnsupportedOperationException("Delete Shipping Planning Item is not permitted.");
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

        throw new UnsupportedOperationException("Delete Shipping Planning Item is not permitted.");
    }
}
