/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.planning.Item;
import javax.ejb.Stateless;

/**
 *
 *
 * @author Alessandro
 */
@Stateless(name = "shipping.planning.ItemDao")
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
                }
            });
        }
    }

}
