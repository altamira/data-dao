/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.process;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.process.Resource;
import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 * @param <T>
 */
@Stateless
public abstract class ResourceDao<T extends Resource> extends BaseDao<T> {

    @Override
    public void lazyLoad(Resource entity) {
        entity.getMaterial();
    }

    @Override
    public void resolveDependencies(T entity, MultivaluedMap<String, String> parameters) {
        entity.setMaterial(entityManager.find(Material.class, entity.getMaterial().getId()));
    }
}
