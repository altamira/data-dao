/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.sales;

import br.com.altamira.data.dao.common.MaterialBaseDao;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.sales.Component;

import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "sales.component.ComponentDao")
public class ComponentDao extends MaterialBaseDao<Component> {
	
	@Override
	public void resolveDependencies(Component entity, MultivaluedMap<String, String> parameters) {
		
		super.resolveDependencies(entity, parameters);
		
		// ALTAMIRA-24
		entity.getWidth().setUnit(entityManager.find(Unit.class, entity.getWidth().getUnit().getId()));
		entity.getLength().setUnit(entityManager.find(Unit.class, entity.getLength().getUnit().getId()));
		entity.getHeight().setUnit(entityManager.find(Unit.class, entity.getHeight().getUnit().getId()));
		entity.getDepth().setUnit(entityManager.find(Unit.class, entity.getDepth().getUnit().getId()));
		entity.getArea().setUnit(entityManager.find(Unit.class, entity.getArea().getUnit().getId()));
		entity.getWeight().setUnit(entityManager.find(Unit.class, entity.getWeight().getUnit().getId()));
	}
    
}
