/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

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
@Stateless(name = "common.MaterialAliasDao")
public class MaterialAliasDao extends BaseDao<br.com.altamira.data.model.common.MaterialAlias> {
    
    /**
     *
     * @param code
     * @return
     */
    public br.com.altamira.data.model.common.MaterialAlias find(
            @NotNull @Size(min = 3, message = CODE_VALIDATION) String code)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<br.com.altamira.data.model.common.MaterialAlias> criteriaQuery = cb.createQuery(getTypeClass());
        Root<br.com.altamira.data.model.common.MaterialAlias> entity = criteriaQuery.from(getTypeClass());

        criteriaQuery.select(entity);

        criteriaQuery.where(
                cb.equal(cb.lower(entity.get("code")), code.toLowerCase().trim()));

        br.com.altamira.data.model.common.MaterialAlias material = null;
        
        try {
            material = entityManager.createQuery(criteriaQuery).getSingleResult();
    
            lazyLoad(material);

        } catch (NoResultException e) {
        }

        return material;
    }
}
