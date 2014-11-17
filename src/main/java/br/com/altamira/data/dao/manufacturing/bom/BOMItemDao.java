/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.bom;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacturing.bom.BOM;
import br.com.altamira.data.model.manufacturing.bom.BOMItem;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 */
@Stateless
public class BOMItemDao extends BaseDao<BOMItem> {

    @Override
    public void lazyLoad(BOMItem entity) {
        // Lazy load of items
        if (entity.getParts() != null) {
            entity.getParts().size();
        }
    }

    @Override
    public void resolveDependencies(BOMItem entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        entity.setBOM(entityManager.find(BOM.class, 
                Long.parseLong(parameters.get("parentId").get(0))));

        // Resolve dependencies
        entity.getParts().stream().forEach((part) -> {
            part.setBOMItem(entity);
        });
    }

    @Override
    public CriteriaQuery<BOMItem> getCriteriaQuery(
            @NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItem> criteriaQuery = cb.createQuery(BOMItem.class);
        Root<BOMItem> entity = criteriaQuery.from(BOMItem.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("bom"), 
                Long.parseLong(parameters.get("parentId").get(0))));
        
        return criteriaQuery;
    }

    /**
     *
     * @param id
     * @param startPage
     * @param pageSize
     * @return
     */
    /*@Override
    public List<BOMItem> list(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItem> q = cb.createQuery(BOMItem.class);
        Root<BOMItem> entity = q.from(BOMItem.class);

        q.select(entity);

        q.where(cb.equal(entity.get("bom"), id));

        return entityManager.createQuery(q)
                //.setFirstResult(startPage * pageSize)
                //.setMaxResults(pageSize)
                .getResultList();
    }*/

    /*@Override
    public BOMItem find(
            @Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException {

        BOMItem entity = super.find(id);

        // Lazy load of items
        if (entity.getParts() != null) {
            entity.getParts().size();
        }

        return entity;
    }*/

    /**
     *
     * @param bomId
     * @param entity
     * @return
     */
    /*public BOMItem create(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long bomId,
            @NotNull(message = ENTITY_VALIDATION) BOMItem entity)
            throws ConstraintViolationException {

        // Get reference from parent 
        entity.setBOM(bomDao.find(bomId));

        // Resolve dependencies
        entity.getParts().stream().forEach((part) -> {
            part.setBOMItem(entity);
        });

        return super.create(entity);
    }*/

    /**
     *
     * @param bomId
     * @param entity
     * @return
     */
    /*public BOMItem update(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long bomId,
            @NotNull(message = ENTITY_VALIDATION) BOMItem entity)
            throws ConstraintViolationException, IllegalArgumentException {

        // Get reference from parent 
        entity.setBOM(bomDao.find(bomId));

        // Resolve dependencies
        entity.getParts().stream().forEach((part) -> {
            part.setBOMItem(entity);
        });

        return super.update(entity);
    }*/

}
