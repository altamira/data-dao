/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.bom;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Color;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.bom.BOM;
import br.com.altamira.data.model.manufacture.bom.Item;
import br.com.altamira.data.model.manufacture.bom.Item_;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 * @author Alessandro
 */
@Stateless(name = "manufacture.bom.ItemDao")
public class ItemDao extends BaseDao<Item> {

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
                    component.setDelivery(null);
                }
            });
        }
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(Item entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        entity.setBOM(entityManager.find(BOM.class,
                Long.parseLong(parameters.get("parentId").get(0))));

        // Resolve dependencies
        entity.getComponent().stream().forEach((part) -> {
            part.setItem(entity);
            part.setColor(entityManager.find(Color.class, part.getColor().getId()));
            if (part.getMaterial() != null) {
                part.setMaterial(entityManager.find(Material.class, part.getMaterial().getId()));
            }
        });
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Item> getCriteriaQuery(
            @NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<Item> entity = criteriaQuery.from(Item.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get(Item_.bom),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }

}
