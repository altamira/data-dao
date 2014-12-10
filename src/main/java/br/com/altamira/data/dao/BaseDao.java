/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao;

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

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
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 * @param <T>
 */
@Stateless
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
     * @param entity
     */
    public void lazyLoad(T entity) {

    }

    /**
     *
     * @param id
     * @return
     */
    public CriteriaQuery<T> fetchJoin(@Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id) {
        return null;
    }
    
    /**
     *
     * @param entity
     * @param parameters
     */
    public void resolveDependencies(T entity, MultivaluedMap<String, String> parameters) {

    }

    /**
     *
     * @param parameters
     * @return
     */
    public CriteriaQuery<T> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = cb.createQuery(getTypeClass());

        Root<T> entity = criteriaQuery.from(getTypeClass());

        criteriaQuery.select(entity);
        
        criteriaQuery.orderBy(cb.desc(entity.get("lastModified")));

        return criteriaQuery;
    }

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    @Override
    public List<T> list(
            @NotNull(message = PARAMETER_VALIDATION) MultivaluedMap<String, String> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 0, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaQuery<T> criteriaQuery = this.getCriteriaQuery(parameters);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize == 0 ? Integer.MAX_VALUE : pageSize)
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
                return getTypeClass().newInstance();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        CriteriaQuery<T> criteriaQuery = this.fetchJoin(id);
        
        T entity;
        
        if (criteriaQuery == null) {
            entity = entityManager.find(getTypeClass(), id);
            this.lazyLoad(entity);
        } else {
            entity = entityManager.createQuery(criteriaQuery).getSingleResult();
        }

        return entity;
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public T create(
            @NotNull(message = ENTITY_VALIDATION) T entity,
            /*@NotNull(message = PARAMETER_VALIDATION)*/ MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException {

        if (entity.getId() != null && entity.getId() > 0) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }

        if (parameters.get("parentId") != null && !parameters.get("parentId").isEmpty()) {
            Object parent = entityManager.find(
                    entity.getParentType(),
                    Long.parseLong(parameters.get("parentId").get(0)));
            
            entity.setParent((br.com.altamira.data.model.Entity) parent);
        }

        entity.setId(null);

        validate(entity);

        this.resolveDependencies(entity, parameters);

        entityManager.persist(entity);
        entityManager.flush();

        entity = entityManager.find(getTypeClass(), entity.getId());

        this.lazyLoad(entity);

        // Reload to update child references
        return entity;
    }

    /**
     *
     * @param entity
     * @param parameters
     * @return
     */
    @Override
    public T update(
            @NotNull(message = ENTITY_VALIDATION) T entity,
            MultivaluedMap<String, String> parameters)
            throws ConstraintViolationException, IllegalArgumentException {

        if (entity.getId() == null || entity.getId() == 0l) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }

        if (parameters != null &&
                parameters.get("parentId") != null) {
            Object parent = entityManager.find(
                    entity.getParentType(),
                    Long.parseLong(parameters.get("parentId").get(0)));

            entity.setParent((br.com.altamira.data.model.Entity) parent);
        }

        validate(entity);

        this.resolveDependencies(entity, parameters);

        entity = entityManager.merge(entity);
        entityManager.flush();

        entity = entityManager.find(getTypeClass(), entity.getId());

        this.lazyLoad(entity);

        // Reload to update child references
        return entity;
    }

    /**
     *
     * @param id
     */
    @Override
    public void remove(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, IllegalArgumentException {

        T entity = entityManager.find(getTypeClass(), id);

        if (entity.getId() == null || entity.getId() <= 0) {
            throw new IllegalArgumentException(ID_NOT_NULL_VALIDATION);
        }

        entity = entityManager.contains(entity) ? entity : entityManager.merge(entity);
        entityManager.remove(entity);
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

    /**
     *
     * @return
     */
    protected Class<T> getTypeClass() {
        Class<T> clazz = (Class<T>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
        return clazz;
    }
}
