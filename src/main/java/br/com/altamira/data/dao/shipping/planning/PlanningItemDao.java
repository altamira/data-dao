/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.bom.BOMItemPart;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.shipping.planning.Planning;
import br.com.altamira.data.model.shipping.planning.PlanningItem;
import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless
public class PlanningItemDao extends BaseDao<PlanningItem> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(PlanningItem entity) {
        // Lazy load of items
        if (entity.getBomItemPart().getMaterial() != null) {
            entity.getBomItemPart().getMaterial().setComponent(null);
        }
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(PlanningItem entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {
        // Get reference from parent 
        entity.setPlanning(entityManager.find(Planning.class,
                Long.parseLong(parameters.get("parentId").get(0))));
        
        // Resolve dependencies
        entity.setBomItemPart(entityManager.find(BOMItemPart.class, entity.getBomItemPart().getId()));
    }
}
