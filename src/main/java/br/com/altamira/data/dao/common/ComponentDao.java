/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Component;
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
 */
@Stateless
public class ComponentDao extends BaseDao<Component> {

    @Override
    public void lazyLoad(Component entity) {

        // Lazy load of items
        if (entity.getMaterial() != null) {
            entity.getMaterial();
            entity.getQuantity();
        }
    }

    @Override
    public void resolveDependencies(br.com.altamira.data.model.common.Component entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        entity.setParent(entityManager.find(br.com.altamira.data.model.common.Material.class,
                Long.parseLong(parameters.get("parentId").get(0))));

        entity.setMaterial(entityManager.find(br.com.altamira.data.model.common.Material.class,
                entity.getMaterial().getId()));

        entity.getQuantity().setUnit(entityManager.find(Unit.class, entity.getQuantity().getUnit().getId()));
    }

    @Override
    public CriteriaQuery<Component> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Component> criteriaQuery = cb.createQuery(Component.class);
        Root<Component> entity = criteriaQuery.from(Component.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("material"),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }
}
