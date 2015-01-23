/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.planning.Delivery;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "shipping.planning.DeliveryDao")
public class DeliveryDao extends BaseDao<Delivery> {

    public Delivery join(List<Delivery> entities) {
        return new Delivery();
    }
    
    public List<Delivery> divide(Delivery entity, List<Delivery> entities) {
        return new ArrayList<>();
    }
}
