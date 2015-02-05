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
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.shipping.execution.Delivered;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
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
        entity.getDelivered().setUnit(entity.getQuantity().getUnit());
        entity.getRemaining().setUnit(entity.getQuantity().getUnit());

        if (entity.getId() != null) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> criteria = cb.createQuery(BigDecimal.class);
            Root<Delivered> root = criteria.from(Delivered.class);
            Expression<BigDecimal> sum = cb.sum(root.get("quantity").get("value"));
            criteria.select(sum);
            criteria.where(cb.equal(root.get("component").get("id"), entity.getId()));

            Measure delivered = new Measure(entityManager.createQuery(criteria).getSingleResult(),
                    entity.getQuantity().getUnit());

            entity.setDelivered(delivered);
            entity.setRemaining(entity.getQuantity().subtract(entity.getDelivered()));
            
            CriteriaQuery<Delivery> criteriaQuery = cb.createQuery(Delivery.class);
            Root<Delivery> delivery = criteriaQuery.from(Delivery.class);
            criteriaQuery.select(delivery);
            criteriaQuery.where(cb.equal(delivery.get("component").get("id"), entity.getId()));
            List<Delivery> deliveries = entityManager.createQuery(criteriaQuery).getResultList();

            // remove delivery dates
            entity.setDelivery(null);
            deliveryDao.removeAll(deliveries);

            entityManager.flush();
        }

        // set default delivery date
        Delivery delivery = new Delivery(entity, entity.getItem().getBOM().getDelivery(), entity.getQuantity());
        delivery.setDelivered(entity.getDelivered());
        delivery.setRemaining(entity.getQuantity().subtract(entity.getDelivered()));
        entity.setDelivery(new ArrayList<Delivery>() {
            {
                add(delivery);
            }
        });

        if (entity.getId() != null) {
            entityManager.persist(delivery);
        }
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
