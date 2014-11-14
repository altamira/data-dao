package br.com.altamira.data.dao.sales;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.sales.OrderItem;

import javax.ejb.Stateless;

/**
 *
 * @author alessandro.holanda
 */
@Stateless
public class OrderItemDao extends BaseDao<OrderItem> {

    public OrderItemDao() {
        this.type = OrderItem.class;
    }

}
