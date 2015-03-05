package br.com.altamira.data.dao.sales;

import br.com.altamira.data.dao.common.MaterialBaseDao;
import br.com.altamira.data.model.measurement.Unit;

import javax.ejb.Stateless;

import br.com.altamira.data.model.sales.Product;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author alessandro.holanda
 */
@Stateless
public class ProductDao extends MaterialBaseDao<Product> {

    @Override
    public void resolveDependencies(Product entity, MultivaluedMap<String, String> parameters) {

        super.resolveDependencies(entity, parameters);

        // ALTAMIRA-24
        entity.getWidth().setUnit(entityManager.find(Unit.class, entity.getWidth().getUnit().getId()));
        entity.getLength().setUnit(entityManager.find(Unit.class, entity.getLength().getUnit().getId()));
        entity.getHeight().setUnit(entityManager.find(Unit.class, entity.getHeight().getUnit().getId()));
        entity.getDepth().setUnit(entityManager.find(Unit.class, entity.getDepth().getUnit().getId()));
        entity.getArea().setUnit(entityManager.find(Unit.class, entity.getArea().getUnit().getId()));
        entity.getWeight().setUnit(entityManager.find(Unit.class, entity.getWeight().getUnit().getId()));
    }
}
