package br.com.altamira.data.dao.sales;

import br.com.altamira.data.dao.BaseDao;

import javax.ejb.Stateless;

import br.com.altamira.data.model.sales.Order;

/**
 * Sales order persistency strategy
 *
 */
@Stateless
public class OrderDao extends BaseDao<Order> {

    public OrderDao() {
        this.type = Order.class;
    }

}
