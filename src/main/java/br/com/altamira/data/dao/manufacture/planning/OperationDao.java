/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.planning;

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

        // TODO: 
        // ALTAMIRA-154: Select the Operations to generate Producer Orders
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_OPERATION.ID, MN_OPERATION.DESCRIPTION
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    --INNER JOIN MN_ORDER_ITEM_CMP ON MN_BOM_ITEM_CMP.ID = MN_ORDER_ITEM_CMP.COMPONENT
        //    INNER JOIN SL_COMPONENT ON MN_BOM_ITEM_CMP.MATERIAL = SL_COMPONENT.ID
        //    INNER JOIN MN_PROCESS ON SL_COMPONENT.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID;
  
        CriteriaQuery<Operation> criteriaQuery = cb.createQuery(Operation.class);
        
        Root<BOM> bom = criteriaQuery.from(BOM.class);
        Root<Item> item = criteriaQuery.from(Item.class);
        Root<Component> component = criteriaQuery.from(Component.class);
        Root<br.com.altamira.data.model.sales.Component> salesComponent = criteriaQuery.from(br.com.altamira.data.model.sales.Component.class);
        Root<Process> process = criteriaQuery.from(Process.class);
        Root<br.com.altamira.data.model.manufacture.process.Operation> processOperation = criteriaQuery.from(br.com.altamira.data.model.manufacture.process.Operation.class);
        Root<Operation> operation = criteriaQuery.from(Operation.class);
        
        criteriaQuery.select(operation).distinct(true);
        
        criteriaQuery.where(cb.equal(bom.get(BOM_.id), item.get(Item_.bom)),
        		cb.equal(item.get(Item_.id), component.get(Component_.item)),
        		cb.equal(component.get(Component_.material), salesComponent.get(br.com.altamira.data.model.sales.Component_.id)),
        		cb.equal(salesComponent.get(br.com.altamira.data.model.sales.Component_.process), process.get(Process_.id)),
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

        // TODO: 
        // ALTAMIRA-154:
        // ------------------------------------------------------------------------------------
        //SELECT DISTINCT 
        //  MN_BOM.ID, MN_BOM.BOM_NUMBER, MN_BOM.CUSTOMER, MN_BOM.DELIVERY
        //FROM 
        //  MN_BOM 
        //    INNER JOIN MN_BOM_ITEM ON MN_BOM.ID = MN_BOM_ITEM.BOM
        //    INNER JOIN MN_BOM_ITEM_CMP ON MN_BOM_ITEM.ID = MN_BOM_ITEM_CMP.ITEM
        //    INNER JOIN MN_ORDER_ITEM_CMP ON MN_BOM_ITEM_CMP.ID = MN_ORDER_ITEM_CMP.COMPONENT
        //    INNER JOIN SL_COMPONENT ON MN_BOM_ITEM_CMP.MATERIAL = SL_COMPONENT.ID
        //    INNER JOIN MN_PROCESS ON SL_COMPONENT.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_PROCESS_OPERATION ON MN_PROCESS_OPERATION.PROCESS = MN_PROCESS.ID
        //    INNER JOIN MN_OPERATION ON MN_PROCESS_OPERATION.OPERATION = MN_OPERATION.ID
        //WHERE
        //  MN_OPERATION.ID = 10500;
  
        CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);
        Root<BOM> bom = criteriaQuery.from(BOM.class);

        criteriaQuery.select(bom);

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
}
