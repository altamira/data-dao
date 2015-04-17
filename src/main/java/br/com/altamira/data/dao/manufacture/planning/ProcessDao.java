/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.planning;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacture.planning.BOM;
import br.com.altamira.data.model.manufacture.planning.BOM_;
import br.com.altamira.data.model.manufacture.planning.Component;
import br.com.altamira.data.model.manufacture.planning.Component_;
import br.com.altamira.data.model.manufacture.planning.Item;
import br.com.altamira.data.model.manufacture.planning.Item_;
import br.com.altamira.data.model.manufacture.planning.Material;
import br.com.altamira.data.model.manufacture.planning.Material_;
import br.com.altamira.data.model.manufacture.planning.Operation;
import br.com.altamira.data.model.manufacture.planning.Order;
import br.com.altamira.data.model.manufacture.planning.Order_;
import br.com.altamira.data.model.manufacture.planning.Process;
import br.com.altamira.data.model.manufacture.planning.Process_;
import br.com.altamira.data.model.manufacture.planning.Produce;
import br.com.altamira.data.model.manufacture.planning.Produce_;
import br.com.altamira.data.model.manufacture.process.Operation_;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.SetJoin;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.planning.ProcessDao")
public class ProcessDao extends BaseDao<br.com.altamira.data.model.manufacture.planning.Process> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(br.com.altamira.data.model.manufacture.planning.Process entity) {
        // Lazy load of items
        if (entity.getOperation() != null) {
            entity.setOperation(null);
        }
    }
    
    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<br.com.altamira.data.model.manufacture.planning.Process> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALTAMIRA-157: Manufacture Planning - list Operations
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_PROCESS.ID, MN_PROCESS.NAME
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID;
        
        CriteriaQuery<br.com.altamira.data.model.manufacture.planning.Process> criteriaQuery = cb.createQuery(br.com.altamira.data.model.manufacture.planning.Process.class);

        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<br.com.altamira.data.model.manufacture.planning.Process> process = criteriaQuery.from(br.com.altamira.data.model.manufacture.planning.Process.class);

        criteriaQuery.select(process).distinct(true);

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)));

        criteriaQuery.orderBy(cb.asc(process.get(Process_.id)));
                
        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<BOM> getBOMQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALTAMIRA-158: Manufacture Planning - list Operation's BOM
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_BOM.ID, MN_BOM.BOM_NUMBER, MN_BOM.CUSTOMER, MN_BOM.DELIVERY
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //WHERE
        //  MN_PROCESS.ID = 10003 
        //ORDER BY
        //  MN_BOM.DELIVERY;
        
        CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);

        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);

        criteriaQuery.select(cb.construct(BOM.class,
                bom.get(BOM_.id),
                bom.get(BOM_.type),
                bom.get(BOM_.number),
                bom.get(BOM_.customer),
                bom.get(BOM_.created),
                bom.get(BOM_.delivery))).distinct(true);

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(process.get(Process_.id), parameters.get("id").get(0)));

        criteriaQuery.orderBy(cb.asc(bom.get(BOM_.delivery)));
                
        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<BOM> listBOM(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        return entityManager.createQuery(this.getBOMQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();
    }

    //ALTAMIRA-159 : Manufacture Planning - list BOM's Item
    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<Item> getBOMItemQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALTAMIRA-159: Manufacture Planning - list BOM's Item
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_BOM_ITEM.ID, MN_BOM_ITEM.ITEM, MN_BOM_ITEM.DESCRIPTION
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //WHERE
        //  MN_PROCESS.ID = 10003
        //  AND MN_BOM.ID = 62492
        //ORDER BY
        //  MN_BOM_ITEM.ITEM;
        
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        
        item.fetch(Item_.component);

        criteriaQuery.select(item);

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(process.get(Process_.id), parameters.get("id").get(0)),
                cb.equal(bom.get(BOM_.id), parameters.get("id").get(1)));

        criteriaQuery.orderBy(cb.asc(item.get(Item_.item)));
        
        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<Item> listBOMItem(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        List<Item> itemList = entityManager.createQuery(this.getBOMItemQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();
        
        ArrayList<Item> list = new ArrayList<Item>(new LinkedHashSet<Item>(itemList));
        
        // lazyload materials
    	list.stream().forEach((item) -> {
    		item.getComponent().forEach((component) -> {
    			component.getMaterial().getId();
    		});
    	});

        return list;
    }

    //ALTAMIRA-160 : Manufacture Planning - list ITEM's Component
    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<Component> getBOMItemComponentQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALTAMIRA-160: Manufacture Planning - list ITEM's Component
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_BOM_ITEM_CMP.ID, MN_BOM_ITEM_CMP.CODE, MN_BOM_ITEM_CMP.DESCRIPTION
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //WHERE
        //  MN_PROCESS.ID = 10003 
        //  AND MN_BOM.ID = 62492
        //  AND MN_BOM_ITEM.ID = 62508;
  
        CriteriaQuery<Component> criteriaQuery = cb.createQuery(Component.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = criteriaQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
        Root<Operation> operation = criteriaQuery.from(Operation.class);

        criteriaQuery.select(component).distinct(true);

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)),
                cb.equal(process.get(Process_.id), parameters.get("id").get(0)),
                cb.equal(bom.get(BOM_.id), parameters.get("id").get(1)),
                cb.equal(item.get(Item_.id), parameters.get("id").get(2)));
        
        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<Component> listBOMItemComponent(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

    	List<Component> list = entityManager.createQuery(this.getBOMItemComponentQuery(parameters))
    			.setFirstResult(startPage * pageSize)
    			.setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
    			.getResultList();

    	// lazyload materials
    	list.stream().forEach((component) -> {
    		component.getMaterial().getId();
    	});

    	return list;
    }

    //ALTAMIRA-161 : Manufacture Planning - Operation summary
    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<Operation> ListSummaryQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALTAMIRA-161: Manufacture Planning - Operation summary
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_OPERATION.ID, MIN(MN_OPERATION.DESCRIPTION), MN_ORDER_ITEM_CMP.START_DATE, SUM(MN_ORDER_ITEM_CMP.QUANTITY)
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN MN_ORDER_ITEM_CMP ON MN_BOM_ITEM_CMP.ID = MN_ORDER_ITEM_CMP.COMPONENT
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //GROUP BY
        //  MN_OPERATION.ID, MN_ORDER_ITEM_CMP.START_DATE;  
        
        CriteriaQuery<Operation> criteriaQuery = cb.createQuery(Operation.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<Produce> produce = criteriaQuery.from(Produce.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = criteriaQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
        Root<Operation> operation = criteriaQuery.from(Operation.class);

        criteriaQuery.select(cb.construct(Operation.class,
                operation.get(Operation_.id),
                cb.min(operation.get("name")),
                produce.get(Produce_.startDate),
                cb.sum(produce.get("quantity").get("value")),
                cb.min(produce.get("quantity").get("unit"))));

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Item_.id), produce.get(Produce_.component)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)));

        criteriaQuery.groupBy(operation.get(Operation_.id), produce.get(Produce_.startDate));

        criteriaQuery.orderBy(cb.asc(operation.get(Operation_.id)));
        
        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<Operation> listSummary(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        return entityManager.createQuery(this.ListSummaryQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();

    }

    public CriteriaQuery<Component> getOperationReportQuery(Long orderID, Long operationId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Component> criteriaQuery = cb.createQuery(Component.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<Produce> produce = criteriaQuery.from(Produce.class);
        Root<Order> order = criteriaQuery.from(Order.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = criteriaQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
        Root<Operation> operation = criteriaQuery.from(Operation.class);

        criteriaQuery.select(component).distinct(true);

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Item_.id), produce.get(Produce_.component)),
                cb.equal(order.get(Order_.id), produce.get(Produce_.order)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)),
                cb.equal(order.get(Order_.id), orderID),
                cb.equal(operation.get(Operation_.id), operationId));

        return criteriaQuery;
    }

    public List<Component> getOperationDataForReport(Long orderID, Long operationId)
            throws ConstraintViolationException {

        return entityManager.createQuery(this.getOperationReportQuery(orderID, operationId))
                .getResultList();
    }
    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<BOM> getComponentQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALTAMIRA-160: Manufacture Planning - list ITEM's Component
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_BOM_ITEM_CMP.ID, MN_BOM_ITEM_CMP.CODE, MN_BOM_ITEM_CMP.DESCRIPTION
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //WHERE
        //  MN_PROCESS.ID = 10003 
        //  AND MN_BOM.ID = 62492
        //  AND MN_BOM_ITEM.ID = 62508;
  
        CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);
        SetJoin<BOM, Item> item = (SetJoin<BOM, Item>) bom.fetch(BOM_.item);
        SetJoin<Item, Component> component = (SetJoin<Item, Component>) item.fetch(Item_.component);
        
	        Subquery<Long> subQuery = criteriaQuery.subquery(Long.class);
	        Root<Material> material = subQuery.from(Material.class);
	        Root<Process> process = subQuery.from(Process.class);
	        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = subQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
	        Root<Operation> operation = subQuery.from(Operation.class);
	        //Root<Produce> produce = subQuery.from(Produce.class);
	        //Root<Order> order = subQuery.from(Order.class);

	        subQuery.select(component.get(Component_.id)).distinct(true);
	        subQuery.where(cb.equal(component.get(Component_.material), material.get(Material_.id)),
	                cb.equal(material.get(Material_.process), process.get(Process_.id)),
	                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
	                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)),
	                //cb.equal(component.get(Component_.id), produce.get(Produce_.component)),
	                //cb.equal(order.get(Order_.id), produce.get(Produce_.order)),
	                cb.equal(process.get(Process_.id), parameters.get("id").get(0)));
        
        criteriaQuery.select(bom);
        
        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Component_.id),subQuery));
        
        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<BOM> listComponent(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

    	List<BOM> list = entityManager.createQuery(this.getComponentQuery(parameters))
    			.setFirstResult(startPage * pageSize)
    			.setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
    			.getResultList();

    	// lazyload materials
    	list.stream().forEach((bom) -> {
    		bom.getItem().stream().forEach((item) -> {
    			item.getComponent().stream().forEach((component) -> {
    				component.getMaterial().getId();
    			});
    		});
    	});

    	return list;
    }
    
    //ALTAMIRA-170 : Manufacture Planning - API for list/create/update/delete Produce

    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<Produce> getProduceQuery(@NotNull MultivaluedMap<String, String> parameters) {
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    	CriteriaQuery<Produce> criteriaQuery = cb.createQuery(Produce.class);
    	Root<BOM> bom = criteriaQuery.from(BOM.class);
    	SetJoin<BOM, Item> item = bom.join(BOM_.item);
    	SetJoin<Item, Component> component = item.join(Item_.component);
    	Join<Component, Produce> produce = component.join(Component_.produce);
    	Join<Component, Material> material = component.join(Component_.material);
    	Join<Material, Process> process = material.join(Material_.process);
    	Join<Process, br.com.altamira.data.model.manufacture.process.Operation> processOperation = process.join(Process_.operation);
    	Join<br.com.altamira.data.model.manufacture.process.Operation, br.com.altamira.data.model.manufacture.Operation> operation = processOperation.join(Operation_.operation);

    	criteriaQuery.select(produce).distinct(true);

    	criteriaQuery.where(cb.equal(process.get(Process_.id), parameters.get("id").get(0)),
    			cb.equal(bom.get(BOM_.id), parameters.get("id").get(1)),
    			cb.equal(item.get(Item_.id), parameters.get("id").get(2)),
    			cb.equal(component.get(Component_.id), parameters.get("id").get(3)));

    	return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public List<Produce> listProduce(
    		@NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
    		@Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
    		@Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
    				throws ConstraintViolationException {

    	return entityManager.createQuery(this.getProduceQuery(parameters))
    			.setFirstResult(startPage * pageSize)
    			.setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
    			.getResultList();
    }

    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<Produce> getByProduceIdQuery(@NotNull MultivaluedMap<String, String> parameters) {
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();

    	CriteriaQuery<Produce> criteriaQuery = cb.createQuery(Produce.class);
    	Root<BOM> bom = criteriaQuery.from(BOM.class);
    	SetJoin<BOM, Item> item = bom.join(BOM_.item);
    	SetJoin<Item, Component> component = item.join(Item_.component);
    	Join<Component, Produce> produce = component.join(Component_.produce);
    	Join<Component, Material> material = component.join(Component_.material);
    	Join<Material, Process> process = material.join(Material_.process);
    	Join<Process, br.com.altamira.data.model.manufacture.process.Operation> processOperation = process.join(Process_.operation);
    	Join<br.com.altamira.data.model.manufacture.process.Operation, br.com.altamira.data.model.manufacture.Operation> operation = processOperation.join(Operation_.operation);

    	criteriaQuery.select(produce).distinct(true);

    	criteriaQuery.where(cb.equal(process.get(Process_.id), parameters.get("id").get(0)),
    			cb.equal(bom.get(BOM_.id), parameters.get("id").get(1)),
    			cb.equal(item.get(Item_.id), parameters.get("id").get(2)),
    			cb.equal(component.get(Component_.id), parameters.get("id").get(3)),
    			cb.equal(produce.get(Produce_.id), parameters.get("id").get(4)));

    	return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    public Produce getProduceById(
    		@NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters)
    				throws ConstraintViolationException {

    	return entityManager.createQuery(this.getByProduceIdQuery(parameters)).getSingleResult();
    }
}
