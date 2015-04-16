/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.planning.BOM;
import br.com.altamira.data.model.shipping.planning.History;
import br.com.altamira.data.model.shipping.planning.User;
import javax.inject.Inject;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.shipping.HistoryDao")
public class HistoryDao extends BaseDao<History> {

    @Inject
    private UserDao userDao;

    @Override
    public void resolveDependencies(History entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {

        String token = parameters.get("token").get(0);
        User user = userDao.getUserByToken(token);
        entity.setCreatedBy(user);
        
        entity.setBOM(entityManager.find(BOM.class, entity.getBOM().getId()));
    }

    @Override
    public void updateDependencies(History entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {

        // ALTAMIRA-138: update BOM.Status with the same value as History.Status on create/update of History
        BOM bom = entity.getBOM();
        bom.setStatus(entity.getStatus());

        entityManager.persist(entity);
        entityManager.flush();
    }

}
