/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.bom;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ENTITY_VALIDATION;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import static br.com.altamira.data.dao.Dao.PAGE_SIZE_VALIDATION;
import static br.com.altamira.data.dao.Dao.START_PAGE_VALIDATION;
import br.com.altamira.data.model.manufacturing.bom.BOMItemPart;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
public class BOMItemPartDao extends BaseDao<BOMItemPart> {

    @EJB
    private BOMItemDao bomItemDao;
    
    public BOMItemPartDao() {
        this.type = BOMItemPart.class;
    }

    /**
     *
     * @param itemId
     * @param startPage
     * @param pageSize
     * @return
     */
    @Override
    public List<BOMItemPart> list(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long itemId,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItemPart> q = cb.createQuery(type);
        Root<BOMItemPart> entity = q.from(type);

        q.select(entity);

        q.where(cb.equal(entity.get("bomItem"), itemId));

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }

    /**
     *
     * @param itemId
     * @param entity
     * @return
     */
    @Override
    public BOMItemPart create(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long itemId,
            @NotNull(message = ENTITY_VALIDATION) BOMItemPart entity)
            throws ConstraintViolationException {
        
        // Get reference from parent 
        entity.setBOMItem(bomItemDao.find(itemId));

        return super.create(entity);
    }

    /**
     *
     * @param itemId
     * @param entity
     * @return
     */
    @Override
    public BOMItemPart update(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long itemId,
            @NotNull(message = ENTITY_VALIDATION) BOMItemPart entity)
            throws ConstraintViolationException, IllegalArgumentException {

        // Get reference from parent 
        entity.setBOMItem(bomItemDao.find(itemId));

        return super.update(entity);
    }
}
