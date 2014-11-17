/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.bom;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacturing.bom.BOMItem;
import br.com.altamira.data.model.manufacturing.bom.BOMItemPart;
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
public class BOMItemPartDao extends BaseDao<BOMItemPart> {

    @Override
    public void lazyLoad(BOMItemPart entity) {

    }

    @Override
    public void resolveDependencies(BOMItemPart entity, MultivaluedMap<String, String> parameters) {
        // Get reference from parent 
        entity.setBOMItem(entityManager.find(BOMItem.class, 
                Long.parseLong(parameters.get("parentId").get(0))));
    }

    @Override
    public CriteriaQuery<BOMItemPart> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItemPart> criteriaQuery = cb.createQuery(BOMItemPart.class);
        Root<BOMItemPart> entity = criteriaQuery.from(BOMItemPart.class);

        criteriaQuery.select(entity);

        criteriaQuery.where(cb.equal(entity.get("bomItem"), 
                Long.parseLong(parameters.get("parentId").get(0))));

        return criteriaQuery;
    }

    /*@EJB
     private BOMItemDao bomItemDao;*/
    /**
     *
     * @param itemId
     * @param startPage
     * @param pageSize
     * @return
     */
    /*@Override
    public List<BOMItemPart> list(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long itemId,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOMItemPart> q = cb.createQuery(BOMItemPart.class);
        Root<BOMItemPart> entity = q.from(BOMItemPart.class);

        q.select(entity);

        q.where(cb.equal(entity.get("bomItem"), itemId));

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }*/

    /**
     *
     * @param itemId
     * @param entity
     * @return
     */
    /*@Override
    public BOMItemPart create(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long itemId,
            @NotNull(message = ENTITY_VALIDATION) BOMItemPart entity)
            throws ConstraintViolationException {

        // Get reference from parent 
        entity.setBOMItem(bomItemDao.find(itemId));

        return super.create(entity);
    }*/

    /**
     *
     * @param itemId
     * @param entity
     * @return
     */
    /*@Override
    public BOMItemPart update(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long itemId,
            @NotNull(message = ENTITY_VALIDATION) BOMItemPart entity)
            throws ConstraintViolationException, IllegalArgumentException {

        // Get reference from parent 
        entity.setBOMItem(bomItemDao.find(itemId));

        return super.update(entity);
    }*/
}
