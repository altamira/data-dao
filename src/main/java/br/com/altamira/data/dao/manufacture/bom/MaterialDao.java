/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.bom;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.common.MaterialBaseDao.CODE_VALIDATION;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "manufacture.bom.MaterialDao")
public class MaterialDao extends BaseDao<br.com.altamira.data.model.manufacture.bom.Material> {
    
    /**
     *
     * @param code
     * @return
     */
    public br.com.altamira.data.model.manufacture.bom.Material find(
            @NotNull @Size(min = 3, message = CODE_VALIDATION) String code)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<br.com.altamira.data.model.manufacture.bom.Material> criteriaQuery = cb.createQuery(getTypeClass());
        Root<br.com.altamira.data.model.manufacture.bom.Material> entity = criteriaQuery.from(getTypeClass());

        criteriaQuery.select(entity);

        criteriaQuery.where(
                cb.equal(cb.lower(entity.get("code")), code.toLowerCase().trim()));

        br.com.altamira.data.model.manufacture.bom.Material material = null;
        
        try {
            material = entityManager.createQuery(criteriaQuery).getSingleResult();
    
            lazyLoad(material);

        } catch (NoResultException e) {
        }

        return material;
    }
}
