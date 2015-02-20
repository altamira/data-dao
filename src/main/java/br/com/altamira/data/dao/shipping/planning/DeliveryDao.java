/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.measurement.Unit;
import br.com.altamira.data.model.shipping.execution.Delivered;
import br.com.altamira.data.model.shipping.planning.Component;
import br.com.altamira.data.model.shipping.planning.Delivery;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
@Stateless(name = "shipping.planning.DeliveryDao")
public class DeliveryDao extends BaseDao<Delivery> {

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(Delivery entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {

        /* ALTAMIRA-66: create delivery date not working */
        // units for quantity, delivered and remaining needs to set before persist 
        Unit quantityUnit = entityManager.find(Unit.class, entity.getQuantity().getUnit().getId());
        entity.getQuantity().setUnit(quantityUnit);
        entity.getDelivered().setUnit(quantityUnit);
        entity.getRemaining().setUnit(quantityUnit);

        // ALTAMIRA-92: trunc the time portion of the date to avoid to use non portable 'trunc()' 
        //              function in group by sql clause
        Date dt = entity.getDelivery();
        dt.setTime(0);
        entity.setDelivery(dt);
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void updateDependencies(Delivery entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {
        calculateRemainingAndDelivered(entity.getComponent());
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Delivery> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Delivery> criteriaQuery = cb.createQuery(Delivery.class);
        Root<Delivery> entity = criteriaQuery.from(Delivery.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("component"),
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }

    public Delivery join(List<Delivery> entities) {
        return new Delivery();
    }

    public List<Delivery> divide(Delivery entity, List<Delivery> entities) {
        return new ArrayList<>();
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

        Set<Delivery> deliverySet = entity.getDelivery();
        TreeSet<Delivery> treeSet = new TreeSet<>(deliverySet);
        Iterator<Delivery> iterator = treeSet.iterator();

        while (delivered.getValue().compareTo(BigDecimal.ZERO) > 0 && iterator.hasNext()) {
            Delivery delivery = iterator.next();

            delivery.setDelivered(delivery.getQuantity().min(delivered));
            delivery.setRemaining(delivery.getQuantity().subtract(delivery.getDelivered()));

            entityManager.persist(delivery);

            delivered.subtract(delivery.getQuantity().min(delivered));
        }

        entity.setDelivery(treeSet);
    }
}
