/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.measurement.Unit;
import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "common.MaterialDao")
public class MaterialDao extends BaseDao<Material> {

    @Override
    public void lazyLoad(Material entity) {

        // Lazy load of items
        if (entity.getComponent() != null) {
            entity.getComponent().size();
        }
    }

    @Override
    public void resolveDependencies(Material entity, MultivaluedMap<String, String> parameters) {
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
}
