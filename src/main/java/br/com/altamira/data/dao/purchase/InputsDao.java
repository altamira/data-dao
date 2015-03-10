/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.purchase;

import br.com.altamira.data.dao.common.MaterialBaseDao;
import br.com.altamira.data.model.purchase.Inputs;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless
public class InputsDao extends MaterialBaseDao<Inputs> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Inputs entity) {
        // Lazy load of items
        entity.setComponent(null);
    }     
}
