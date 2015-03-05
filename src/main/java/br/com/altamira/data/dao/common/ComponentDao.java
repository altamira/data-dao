/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Component;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.common.Component_;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "common.ComponentDao")
public class ComponentDao extends BaseDao<Component> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Component entity) {
        // Lazy load of items
        //entity.getMaterial().getComponent().size();
        entity.getMaterial().setComponent(null);
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(br.com.altamira.data.model.common.Component entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        entity.setParent(entityManager.find(br.com.altamira.data.model.common.Material.class,
                Long.parseLong(parameters.get("parentId").get(0))));

        entity.setMaterial(entityManager.find(br.com.altamira.data.model.common.Material.class,
                entity.getMaterial().getId()));

        entity.getQuantity().setUnit(entityManager.find(Unit.class, entity.getQuantity().getUnit().getId()));
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

        criteriaQuery.where(cb.equal(entity.get(Component_.material),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }
}
