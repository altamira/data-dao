/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.execution;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.execution.PackingList;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.shipping.execution.PackingListDao")
public class PackingListDao extends BaseDao<PackingList> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(PackingList entity) {
        // Lazy load of items
        if (entity.getDelivered() != null) {
            entity.getDelivered().size();
            
            if (entity.getBOM().getItem() != null) {
                entity.getBOM().getItem().size();

                //ALTAMIRA-76: hides ITEM 0 from materials list 
                entity.getBOM().getItem().removeIf(p -> p.getItem() == 0);

                entity.getBOM().getItem().stream().forEach((item) -> {
                    item.getComponent().size();
                    item.getComponent().stream().forEach((component) -> {
                        component.getMaterial().setComponent(null);
                        component.getDelivery().size();
                    });
                });
            }
        }

    }

}
