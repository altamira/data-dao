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
import br.com.altamira.data.model.manufacture.planning.Operation;
import br.com.altamira.data.model.manufacture.planning.Operation_;
import br.com.altamira.data.model.manufacture.planning.Order;
import br.com.altamira.data.model.manufacture.planning.Order_;
import br.com.altamira.data.model.manufacture.planning.Produce;
import br.com.altamira.data.model.manufacture.planning.Produce_;
import br.com.altamira.data.model.manufacture.process.Process;
import br.com.altamira.data.model.manufacture.process.Process_;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.planning.OperationDao")
public class OperationDao extends BaseDao<Operation> {

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Operation> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // ALTAMIRA-157: Manufacture Planning - list Operations
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_OPERATION.ID, MN_OPERATION.DESCRIPTION
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID; 
        
        CriteriaQuery<Operation> criteriaQuery = cb.createQuery(Operation.class);

        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = criteriaQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
        Root<Operation> operation = criteriaQuery.from(Operation.class);

        criteriaQuery.select(operation).distinct(true);

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)));

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
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //WHERE
        //  MN_OPERATION.ID = 10500;
        
        CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);

        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = criteriaQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
        Root<Operation> operation = criteriaQuery.from(Operation.class);

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
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)),
                cb.equal(operation.get(Operation_.id), parameters.get("id").get(0)));

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
        //MN_BOM_ITEM.ID, MN_BOM_ITEM.ITEM, MN_BOM_ITEM.DESCRIPTION
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //WHERE
        //  MN_OPERATION.ID = 10500
        //  AND MN_BOM.ID = 61849;
        
        CriteriaQuery<Item> criteriaQuery = cb.createQuery(Item.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.common.Material> material = criteriaQuery.from(br.com.altamira.data.model.common.Material.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = criteriaQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
        Root<Operation> operation = criteriaQuery.from(Operation.class);

        criteriaQuery.select(cb.construct(Item.class,
                item.get(Item_.id),
                item.get(Item_.item),
                item.get(Item_.description)));

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)),
                cb.equal(operation.get(Operation_.id), parameters.get("id").get(0)),
                cb.equal(bom.get(BOM_.id), parameters.get("id").get(1)));

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

        List<Item> item = entityManager.createQuery(this.getBOMItemQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();

        return new ArrayList<Item>(new LinkedHashSet<Item>(item));
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
        //MN_BOM_ITEM_CMP.ID, MN_BOM_ITEM_CMP.CODE, MN_BOM_ITEM_CMP.DESCRIPTION
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //WHERE
        //  MN_OPERATION.ID = 10500
        //  AND MN_BOM.ID = 61849
        //  AND MN_BOM_ITEM.ID = 61877;
        
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
                cb.equal(operation.get(Operation_.id), parameters.get("id").get(0)),
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

        return entityManager.createQuery(this.getBOMItemComponentQuery(parameters))
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
                .getResultList();
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
        //MN_OPERATION.ID, MIN(MN_OPERATION.DESCRIPTION), MN_ORDER_ITEM_CMP.START_DATE, SUM(MN_ORDER_ITEM_CMP.QUANTITY)
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN MN_ORDER_ITEM_CMP ON MN_BOM_ITEM_CMP.ID = MN_ORDER_ITEM_CMP.COMPONENT
        //    INNER JOIN CM_MATERIAL ON MN_BOM_ITEM_CMP.MATERIAL = CM_MATERIAL.ID
        //    INNER JOIN MN_PROCESS ON CM_MATERIAL.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //    INNER JOIN MR_RESOURCE ON MR_RESOURCE.ID = MN_BOM_ITEM_CMP.ID
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
                cb.min(produce.get("quantity").get("unit")),
                cb.sum(produce.get("weight").get("value")),
                cb.min(produce.get("weight").get("unit"))));

        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
                cb.equal(item.get(Item_.id), component.get(Component_.item)),
                cb.equal(component.get(Item_.id), produce.get(Produce_.component)),
                cb.equal(component.get(Component_.material), material.get(br.com.altamira.data.model.common.Material_.id)),
                cb.equal(material.get(br.com.altamira.data.model.common.Material_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.process), process.get(Process_.id)),
                cb.equal(processOperation.get(br.com.altamira.data.model.manufacture.process.Operation_.operation), operation.get(Operation_.id)));

        criteriaQuery.groupBy(operation.get(Operation_.id), produce.get(Produce_.startDate));

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
}
