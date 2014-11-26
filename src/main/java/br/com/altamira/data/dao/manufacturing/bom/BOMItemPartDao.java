/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.bom;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacturing.bom.BOMItem;
import br.com.altamira.data.model.manufacturing.bom.BOMItemPart;
import br.com.altamira.data.model.measurement.Unit;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 */
@Stateless
public class BOMItemPartDao extends BaseDao<BOMItemPart> {

    @Override
    public void resolveDependencies(BOMItemPart entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        entity.setBOMItem(entityManager.find(BOMItem.class, 
                Long.parseLong(parameters.get("parentId").get(0))));
        
        entity.getQuantity().setUnit(entityManager.find(Unit.class, entity.getQuantity().getUnit().getId()));
        entity.getWidth().setUnit(entityManager.find(Unit.class, entity.getWidth().getUnit().getId()));
        entity.getHeight().setUnit(entityManager.find(Unit.class, entity.getHeight().getUnit().getId()));
        entity.getLength().setUnit(entityManager.find(Unit.class, entity.getLength().getUnit().getId()));
        entity.getWeight().setUnit(entityManager.find(Unit.class, entity.getWeight().getUnit().getId()));
    }

    @Override
    public CriteriaQuery<BOMItemPart> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItemPart> criteriaQuery = cb.createQuery(BOMItemPart.class);
        Root<BOMItemPart> entity = criteriaQuery.from(BOMItemPart.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("bomItem"), 
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }

}
