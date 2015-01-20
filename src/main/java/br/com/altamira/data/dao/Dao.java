/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 * @param <T>
 */
@Stateless
public interface Dao<T extends br.com.altamira.data.model.Entity> {

    /**
     *
     */
    public static final String START_PAGE_VALIDATION = "Invalid start page number, must be greater than 0.";

    /**
     *
     */
    public static final String PAGE_SIZE_VALIDATION = "Invalid page size, must be greater than 0.";

    /**
     *
     */
    public static final String NUMBER_VALIDATION = "Invalid number, must be greater than zero.";

    /**
     *
     */
    public static final String ENTITY_VALIDATION = "Entity can't be null.";
    
    /**
     *
     */
    public static final String PARAMETER_VALIDATION = "Parameters can't be null.";

    /**
     *
     */
    public static final String ID_NULL_VALIDATION = "Entity id must be null or zero.";

    /**
     *
     */
    public static final String ID_NOT_NULL_VALIDATION = "Entity id can't be null or zero.";

    /**
     *
     */
    public static final String SEARCH_VALIDATION = "Search word can't be null and size must be greater that 2 characters.";

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     * @throws ConstraintViolationException
     */
    public List<T> list(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException;

    /**
     *
     * @param id
     * @return
     * @throws ConstraintViolationException
     * @throws NoResultException
     */
    public T find(
            @Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException;

    /**
     *
     * @param parameters
     * @param entity
     * @return
     * @throws ConstraintViolationException
     */
    public T create(
            @NotNull(message = ENTITY_VALIDATION) T entity,
            /*@NotNull(message = PARAMETER_VALIDATION)*/ MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException;
        
    /**
     *
     * @param parameters
     * @param entity
     * @return
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    public T update(
            @NotNull(message = ENTITY_VALIDATION) T entity,
            /*@NotNull(message = PARAMETER_VALIDATION)*/ MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException;
    
    /**
     *
     * @param id
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    public void remove(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, IllegalArgumentException;
    
    /**
     *
     * @param entities
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    public void removeAll(
            @NotNull List<T> entities)
            throws ConstraintViolationException, IllegalArgumentException;
}
