/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.bom;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.manufacture.bom.Delivery;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "manufacture.bom.DeliveryDao")
public class DeliveryDao extends BaseDao<Delivery> {
    
    @Override
    public Delivery create(
            @NotNull(message = ENTITY_VALIDATION) Delivery entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Create Bill of Material Delivery is not permitted.");
    }

    @Override
    public Delivery update(
            @NotNull(message = ENTITY_VALIDATION) Delivery entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Update Bill of Material Delivery is not permitted.");
    }

    /**
     *
     * @param id
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    @Override
    public void remove(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, IllegalArgumentException {

        throw new UnsupportedOperationException("Delete Bill of Material Delivery is not permitted.");
    }
    
}
