/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.bom;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.bom.Component;
import br.com.altamira.data.model.manufacture.bom.Delivery;
import br.com.altamira.data.model.manufacture.bom.Item;
import br.com.altamira.data.model.measurement.Unit;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 * @author Alessandro
 */
@Stateless(name = "manufacture.bom.ComponentDao")
public class ComponentDao extends BaseDao<Component> {

    @Inject
    private DeliveryDao deliveryDao;

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Component entity) {
        // Lazy load of items
        if (entity.getMaterial() != null) {
            entity.getMaterial().setComponent(null);
        }
        entity.setDelivery(null);
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(Component entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        Item item = entityManager.find(Item.class, Long.parseLong(parameters.get("parentId").get(0)));
        entity.setItem(item);
        entity.setMaterial(entityManager.find(Material.class, entity.getMaterial().getId()));
        entity.getQuantity().setUnit(entityManager.find(Unit.class, entity.getQuantity().getUnit().getId()));
        entity.getWidth().setUnit(entityManager.find(Unit.class, entity.getWidth().getUnit().getId()));
        entity.getHeight().setUnit(entityManager.find(Unit.class, entity.getHeight().getUnit().getId()));
        entity.getLength().setUnit(entityManager.find(Unit.class, entity.getLength().getUnit().getId()));
        entity.getWeight().setUnit(entityManager.find(Unit.class, entity.getWeight().getUnit().getId()));

        // set default delivery date
        deliveryDao.removeAll(entity.getDelivery());

        Delivery delivery = new Delivery(entity, item.getBOM().getDelivery(), entity.getQuantity());

        entity.setDelivery(new ArrayList<Delivery>() {
            {
                add(delivery);
            }
        });
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Component> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Component> criteriaQuery = cb.createQuery(Component.class);
        Root<Component> entity = criteriaQuery.from(Component.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("item"),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }

}
