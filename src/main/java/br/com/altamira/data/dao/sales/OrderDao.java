package br.com.altamira.data.dao.sales;

import br.com.altamira.data.dao.BaseDao;

import javax.ejb.Stateless;

import br.com.altamira.data.model.sales.Order;

/**
 * Sales order persistency strategy
 *
 */
@Stateless(name = "br.com.altamira.data.dao.sales.OrderDao")
public class OrderDao extends BaseDao<Order> {

}
