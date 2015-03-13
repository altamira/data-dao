/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.purchase;

import br.com.altamira.data.dao.common.MaterialBaseDao;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.purchase.Steel;

import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "purchase.SteelDao")
public class SteelDao extends MaterialBaseDao<Steel> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Steel entity) {
        // Lazy load of items
        entity.setComponent(null);
    }    
        
    @Override
    public void resolveDependencies(Steel entity, MultivaluedMap<String, String> parameters) {

        super.resolveDependencies(entity, parameters);

        // ALTAMIRA-24
        entity.getWidth().setUnit(entityManager.find(Unit.class, entity.getWidth().getUnit().getId()));
        entity.getLength().setUnit(entityManager.find(Unit.class, entity.getLength().getUnit().getId()));
        entity.getThickness().setUnit(entityManager.find(Unit.class, entity.getThickness().getUnit().getId()));
        entity.getWeight().setUnit(entityManager.find(Unit.class, entity.getWeight().getUnit().getId()));
    }
}
