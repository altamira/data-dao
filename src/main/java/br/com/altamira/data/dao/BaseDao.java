/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao;

import static br.com.altamira.data.dao.Dao.PAGE_SIZE_VALIDATION;
import static br.com.altamira.data.dao.Dao.SEARCH_VALIDATION;
import static br.com.altamira.data.dao.Dao.START_PAGE_VALIDATION;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Alessandro
 * @param <T>
 */
public abstract class BaseDao<T extends br.com.altamira.data.model.Entity> implements Dao<T> {

    /**
     *
     */
    @Inject
    protected Logger log;

    /**
     *
     */
    @Inject
    protected EntityManager entityManager;

    /**
     *
     */
    @Inject
    protected Validator validator;

    /**
     *
     */
    protected Class<T> type;
    
    public void lazyLoad(T entity) {
        
    }
    
    public void resolveDependencies(T entity) {
        
    }
    
    public CriteriaQuery<T> getCriteriaQuery(long parentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(type);
        
        Root<T> entity = criteriaQuery.from(type);

        criteriaQuery.select(entity);
        
        criteriaQuery.orderBy(cb.desc(entity.get("lastModified")));
        
        return criteriaQuery;
    }
    
    /**
     *
     * @param startPage
     * @param pageSize
     * @return
     */
    @Override
    public List<T> list(
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaQuery<T> criteriaQuery = this.getCriteriaQuery(0l);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
    
    /**
     *
     * @param parentId
     * @param startPage
     * @param pageSize
     * @return
     */
    @Override
    public List<T> list(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long parentId,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaQuery<T> criteriaQuery = this.getCriteriaQuery(parentId);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }
    
    /**
     *
     * @param search
     * @param startPage
     * @param pageSize
     * @return
     * @throws ConstraintViolationException
     */
    @Override
    public List<T> search(
            @NotNull @Size(min = 2, message = SEARCH_VALIDATION) String search,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> q = cb.createQuery(type);
        Root<T> entity = q.from(type);

        String searchCriteria = "%" + search.toLowerCase().trim() + "%";

        // TODO: get fields from Entity<T>
        /*q.select(cb.construct(type, entity.get("number"),
         entity.get("customer"),
         entity.get("checked")));*/
        // TODO: pass criteria query as parameter
        /*q.where(cb.or(
         cb.like(cb.lower(entity.get("number").as(String.class)), searchCriteria),
         cb.like(cb.lower(entity.get("customer")), searchCriteria)));*/
        log.log(Level.INFO, "Searching for {0}...", searchCriteria);

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public T find(
            @Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException {

        // Return Entity Model
        if (id == 0) {
            try {
                return type.newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        T entity = entityManager.find(type, id);

        this.lazyLoad(entity);
        
        return entity;
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public T create(
            @NotNull(message = ENTITY_VALIDATION) T entity)
            throws ConstraintViolationException {

        if (entity.getId() != null && entity.getId() > 0) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }

        entity.setId(null);

        validate(entity);

        this.resolveDependencies(entity);
        
        entityManager.persist(entity);
        entityManager.flush();

        // Reload to update child references
        return find(entity.getId());
    }

    
    /**
     *
     * @param entity
     * @return
     */
    @Override
    public T create(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long parentId,
            @NotNull(message = ENTITY_VALIDATION) T entity)
            throws ConstraintViolationException {

        if (entity.getId() != null && entity.getId() > 0) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }
        
        Object parent = entityManager.find(
                entity.getParentType(), 
                parentId);
        
        entity.setParent((br.com.altamira.data.model.Entity)parent);
        
        entity.setId(null);

        validate(entity);

        this.resolveDependencies(entity);
        
        entityManager.persist(entity);
        entityManager.flush();

        // Reload to update child references
        return find(entity.getId());
    }
    
    /**
     *
     * @param entity
     * @return
     */
    @Override
    public T update(
            @NotNull(message = ENTITY_VALIDATION) T entity)
            throws ConstraintViolationException, IllegalArgumentException {

        if (entity.getId() == null || entity.getId() == 0l) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }

        validate(entity);

        this.resolveDependencies(entity);
        
        entity = entityManager.merge(entity);
        entityManager.flush();

        // Reload to update child references
        return find(entity.getId());
    }
    
    /**
     *
     * @param entity
     * @return
     */
    @Override
    public T update(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long parentId,
            @NotNull(message = ENTITY_VALIDATION) T entity)
            throws ConstraintViolationException, IllegalArgumentException {

        if (entity.getId() == null || entity.getId() == 0l) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }

        Object parent = entityManager.find(
                entity.getParentType(), 
                parentId);
        
        entity.setParent((br.com.altamira.data.model.Entity)parent);
        
        validate(entity);

        this.resolveDependencies(entity);
        
        entity = entityManager.merge(entity);
        entityManager.flush();

        // Reload to update child references
        return find(entity.getId());
    }
    
    /**
     *
     * @param entity
     */
    @Override
    public void remove(
            @NotNull(message = ENTITY_VALIDATION) T entity)
            throws ConstraintViolationException, IllegalArgumentException {

        if (entity.getId() == null || entity.getId() <= 0) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }

        entity = entityManager.contains(entity) ? entity : entityManager.merge(entity);
        entityManager.remove(entity);
    }

    /**
     *
     * @param id
     */
    @Override
    public void remove(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException {

        remove(find(id));
    }
    
    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions
     * based on the type of error. If the error is standard bean validation
     * errors then it will throw a ConstraintValidationException with the set of
     * the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is
     * registered it throws a regular validation exception so that it can be
     * interpreted separately.
     * </p>
     *
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validate(T entity) throws ConstraintViolationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<T>> violations = validator.validate(entity);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }
    }

    private Class<T> getTypeClass() {
        Class<T> clazz = (Class<T>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
        return clazz;
    }
}
