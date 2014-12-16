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
import br.com.altamira.data.model.manufacture.bom.BOMItem;
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
@Stateless
public class BOMItemDao extends BaseDao<BOMItem> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(BOMItem entity) {
        // Lazy load of items
        if (entity.getParts() != null) {
            entity.getParts().size();
            entity.getParts().stream().forEach((part) -> {
                if (part.getMaterial() != null) {
                    part.getMaterial().setComponent(null);
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
    public void resolveDependencies(BOMItem entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        entity.setBOM(entityManager.find(BOM.class,
                Long.parseLong(parameters.get("parentId").get(0))));

        // Resolve dependencies
        entity.getParts().stream().forEach((part) -> {
            part.setBOMItem(entity);
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
    public CriteriaQuery<BOMItem> getCriteriaQuery(
            @NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItem> criteriaQuery = cb.createQuery(BOMItem.class);
        Root<BOMItem> entity = criteriaQuery.from(BOMItem.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("bom"),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }

}
