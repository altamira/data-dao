/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.planning;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.PAGE_SIZE_VALIDATION;
import static br.com.altamira.data.dao.Dao.PARAMETER_VALIDATION;
import static br.com.altamira.data.dao.Dao.START_PAGE_VALIDATION;
import br.com.altamira.data.model.manufacture.planning.BOM;
import br.com.altamira.data.model.manufacture.planning.BOM_;
import br.com.altamira.data.model.manufacture.planning.Component;
import br.com.altamira.data.model.manufacture.planning.Component_;
import br.com.altamira.data.model.manufacture.planning.Item;
import br.com.altamira.data.model.manufacture.planning.Item_;
import br.com.altamira.data.model.manufacture.planning.Material;
import br.com.altamira.data.model.manufacture.planning.Material_;
import br.com.altamira.data.model.manufacture.planning.Order;
import br.com.altamira.data.model.manufacture.planning.Order_;
import br.com.altamira.data.model.manufacture.planning.Process;
import br.com.altamira.data.model.manufacture.planning.Process_;
import br.com.altamira.data.model.manufacture.planning.Produce;
import br.com.altamira.data.model.manufacture.planning.Produce_;
import br.com.altamira.data.model.manufacture.process.Operation_;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.planning.OrderDao")
public class OrderDao extends BaseDao<Order> {

    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<Order> getBOMQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // TODO: 
        // ALTAMIRA-154:
        // ------------------------------------------------------------------------------------
        // SELECT DISTINCT
        //    MN_BOM.ID, 
        //    MN_BOM.BOM_NUMBER, 
        //    MN_BOM.CUSTOMER, 
        //    MN_BOM.DELIVERY
        // FROM 
        //   MN_BOM 
        //     INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //     INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //     INNER JOIN MN_ORDER_ITEM_CMP ON MN_BOM_ITEM_CMP.ID = MN_ORDER_ITEM_CMP.COMPONENT
        //     INNER JOIN MN_ORDER ON MN_ORDER_ITEM_CMP.MN_ORDER = MN_ORDER.ID
        // WHERE
        //   MN_ORDER.ID = 65497;
  
        CriteriaQuery<Order> criteriaQuery = cb.createQuery(Order.class);
        Root<Order> order = criteriaQuery.from(Order.class);
        
        ListJoin<Order, Produce> produce = order.join(Order_.produce, JoinType.LEFT);
        Join<Produce, Component> component = produce.join(Produce_.component, JoinType.LEFT);
        Join<Component, Item> item = component.join(Component_.item, JoinType.LEFT);
        Join<Item, BOM> bom = item.join(Item_.bom, JoinType.LEFT);
        
        criteriaQuery.select(order).distinct(true);

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()
                && !parameters.get("search").get(0).isEmpty()) {
        	String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";
        	
        	criteriaQuery.where(cb.or(
        			cb.like(cb.lower(bom.get(BOM_.number).as(String.class)), searchCriteria),
                    cb.like(cb.lower(bom.get(BOM_.customer)), searchCriteria),
                    cb.like(cb.lower(order.get(Order_.id).as(String.class)), searchCriteria)));
        	
        }
        
        criteriaQuery.orderBy(cb.asc(order.get(Order_.id)));
        
        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<Order> listBOM(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        return entityManager.createQuery(this.getBOMQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();
    }    
    
    @Override
    public List<Order> list(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

    	if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()
                && !parameters.get("search").get(0).isEmpty()) {
    		
    		return this.listBOM(parameters, startPage, pageSize);
    	}
    	else
    	{
    		return super.list(parameters, startPage, pageSize);
    	}
    }
    
    //ALTAMIRA-175 : Manufacture Planning - create list order's operations API

    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<br.com.altamira.data.model.manufacture.Operation> getOperationQuery(@NotNull MultivaluedMap<String, String> parameters) {
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    	CriteriaQuery<br.com.altamira.data.model.manufacture.Operation> criteriaQuery = cb.createQuery(br.com.altamira.data.model.manufacture.Operation.class);
    	
    	Root<Order> order = criteriaQuery.from(Order.class);
    	ListJoin<Order, Produce> produce = order.join(Order_.produce);
    	Join<Produce, Component> component = produce.join(Produce_.component);
    	Join<Component, Material> material = component.join(Component_.material);
    	Join<Material, Process> process = material.join(Material_.process);
    	Join<Process, br.com.altamira.data.model.manufacture.process.Operation> processOperation = process.join(Process_.operation);
    	Join<br.com.altamira.data.model.manufacture.process.Operation, br.com.altamira.data.model.manufacture.Operation> operation = processOperation.join(Operation_.operation);

    	criteriaQuery.select(operation);

    	criteriaQuery.where(cb.equal(order.get(Order_.id), parameters.get("id").get(0)));

    	return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<br.com.altamira.data.model.manufacture.Operation> listOperation(
    		@NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
    		@Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
    		@Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
    				throws ConstraintViolationException {

    	List<br.com.altamira.data.model.manufacture.Operation> operation = entityManager.createQuery(this.getOperationQuery(parameters))
    			.setFirstResult(startPage * pageSize)
    			.setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
    			.getResultList();

    	return new ArrayList<br.com.altamira.data.model.manufacture.Operation>(new LinkedHashSet<br.com.altamira.data.model.manufacture.Operation>(operation));
    }
}
