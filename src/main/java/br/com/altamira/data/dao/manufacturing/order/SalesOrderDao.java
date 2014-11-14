/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.order;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacturing.order.Order;
import javax.ejb.Stateless;

/**
 *
 * 
 */
@Stateless
public class SalesOrderDao extends BaseDao<Order> {

    public SalesOrderDao() {
        this.type = Order.class;
    }

}
