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
import br.com.altamira.data.model.manufacture.planning.Order;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
    public CriteriaQuery<BOM> getBOMQuery(@NotNull MultivaluedMap<String, String> parameters) {
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
