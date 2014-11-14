package br.com.altamira.data.dao.sales;

import br.com.altamira.data.dao.BaseDao;

import javax.ejb.Stateless;

import br.com.altamira.data.model.sales.Product;

/**
 *
 * @author alessandro.holanda
 */
@Stateless
public class ProductDao extends BaseDao<Product> {

    public ProductDao() {
        this.type = Product.class;
    }

    public Product find(String code) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
