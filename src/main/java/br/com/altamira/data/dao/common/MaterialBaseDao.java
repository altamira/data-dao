/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Unit;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 * @param <T>
 */
@Stateless(name = "common.MaterialDao")
public abstract class MaterialBaseDao<T extends br.com.altamira.data.model.common.Material> extends BaseDao<T> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(T entity) {
        // Lazy load of items
        entity.getComponent().size();
        entity.getComponent().forEach((component) -> {
            //component.getMaterial().getComponent().size();
            component.getMaterial().setComponent(null);
        });
    }

    /*@Override
    public CriteriaQuery<T> fetchJoin(@Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(getTypeClass());
        Root<T> entity = criteriaQuery.from(getTypeClass());
        
        entity.fetch("component");
        
        criteriaQuery.select(entity);
        
        return criteriaQuery;
    }*/

    /**
     *
     * @param entity
     * @param parameters
     */
    

    @Override
    public void resolveDependencies(T entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        if (entity.getComponent() != null) {
            entity.getComponent().stream().forEach((component) -> {
                // Get reference from parent 
                component.setParent(entity);

                component.setMaterial(entityManager.find(br.com.altamira.data.model.common.Material.class,
                        component.getMaterial().getId()));

                component.getQuantity().setUnit(entityManager.find(Unit.class, component.getQuantity().getUnit().getId()));
            });
        }
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<T> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(getTypeClass());
        Root<T> entity = criteriaQuery.from(getTypeClass());

        criteriaQuery.select(cb.construct(getTypeClass(),
                entity.get("id"),
                entity.get("code"),
                entity.get("description")));

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get("code")), searchCriteria),
                    cb.like(cb.lower(entity.get("description")), searchCriteria)));
        }

        criteriaQuery.orderBy(cb.desc(entity.get("lastModified")));

        return criteriaQuery;
    }
}
