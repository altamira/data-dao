/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.planning.Component;
import javax.ejb.Stateless;

/**
 *
 *
 * @author Alessandro
 */
@Stateless(name = "shipping.planning.ComponentDao")
public class ComponentDao extends BaseDao<Component> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Component entity) {
        // Lazy load of items
        if (entity.getMaterial() != null) {
            entity.getMaterial().setComponent(null);
        }
    }
}
