/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.execution;


import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.manufacture.execution.Component;
import br.com.altamira.data.model.manufacture.execution.Component_;
import java.util.List;

import javax.ejb.Stateless;
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
@Stateless(name = "br.com.altamira.data.dao.manufacture.execution.ComponentDao")
public class ComponentDao extends BaseDao<Component> {
    
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

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Component> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Component> criteriaQuery = cb.createQuery(Component.class);
        Root<Component> entity = criteriaQuery.from(Component.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get(Component_.item),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
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
