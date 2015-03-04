/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.execution;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.shipping.execution.Component;
import br.com.altamira.data.model.shipping.execution.Delivered;
import br.com.altamira.data.model.shipping.execution.Delivery;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless
public class DeliveredDao extends BaseDao<Delivered> {
    
    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Delivered entity) {
        // Lazy load of items
        if (entity.getComponent().getMaterial() != null) {
            entity.getComponent().getMaterial().setComponent(null);
        }

    }
    
    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Delivered> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Delivered> criteriaQuery = cb.createQuery(Delivered.class);
        Root<Delivered> entity = criteriaQuery.from(Delivered.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("packingList"),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(Delivered entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {
        // Resolve dependencies

        entity.setComponent(entityManager.find(Component.class, entity.getComponent().getId()));
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void updateDependencies(Delivered entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {
        calculateRemainingAndDelivered(entity.getComponent());
    }

    /**
     *
     * @param entity
     */
    public void calculateRemainingAndDelivered(Component entity) {
        /* ALTAMIRA-22: Shipping Planning - amount remaining calculation */
        // Retrieve the amount of delivered item quantities

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

        entityManager.persist(entity);
        entityManager.flush();

        Set<Delivery> deliverySet = entity.getDelivery();
        TreeSet<Delivery> treeSet = new TreeSet<>(deliverySet);
        Iterator<Delivery> iterator = treeSet.iterator();

        while (delivered.getValue().compareTo(BigDecimal.ZERO) > 0 && iterator.hasNext()) {
            Delivery delivery = iterator.next();

            delivery.setDelivered(delivery.getQuantity().min(delivered));
            delivery.setRemaining(delivery.getQuantity().subtract(delivery.getDelivered()));

            entityManager.persist(delivery);
            entityManager.flush();

            delivered.subtract(delivery.getQuantity().min(delivered));
        }

        entity.setDelivery(treeSet);
    }
}
