/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.bom;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.manufacturing.bom.BOMItem;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * 
 */
@Stateless
public class BOMItemDao extends BaseDao<BOMItem> {

    @EJB
    private BOMDao bomDao;
    
    public BOMItemDao() {
        this.type = BOMItem.class;
    }

    /**
     *
     * @param id
     * @param startPage
     * @param pageSize
     * @return
     */
    @Override
    public List<BOMItem> list(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItem> q = cb.createQuery(type);
        Root<BOMItem> entity = q.from(type);

        q.select(entity);

        q.where(cb.equal(entity.get("bom"), id));

        return entityManager.createQuery(q)
                //.setFirstResult(startPage * pageSize)
                //.setMaxResults(pageSize)
                .getResultList();
    }

    @Override
    public BOMItem find(
            @Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException {

        BOMItem entity = super.find(id);

        // Lazy load of items
        if (entity.getParts() != null) {
            entity.getParts().size();
        }

        return entity;
    }

    /**
     *
     * @param bomId
     * @param entity
     * @return
     */
    public BOMItem create(
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
    }

    /**
     *
     * @param bomId
     * @param entity
     * @return
     */
    public BOMItem update(
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
    }

}
