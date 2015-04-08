/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacture.planning.Component;
import br.com.altamira.data.model.manufacture.planning.Order;
import br.com.altamira.data.model.manufacture.planning.Produce;

import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.planning.ProduceDao")
public class ProduceDao extends BaseDao<Produce> {
	
	@Override
	public void resolveDependencies(Produce entity, MultivaluedMap<String, String> parameters)
			throws IllegalArgumentException {
		
		entity.setOrder(entityManager.find(Order.class, entity.getOrder().getId()));
		entity.setComponent(entityManager.find(Component.class, Long.parseLong(parameters.get("id").get(3))));
	}

}
