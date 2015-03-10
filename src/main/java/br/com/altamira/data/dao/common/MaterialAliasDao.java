/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.common.MaterialBaseDao.CODE_VALIDATION;
import br.com.altamira.data.model.common.MaterialAlias;
import br.com.altamira.data.model.common.MaterialAlias_;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "common.MaterialAliasDao")
public class MaterialAliasDao extends BaseDao<br.com.altamira.data.model.common.MaterialAlias> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(br.com.altamira.data.model.common.MaterialAlias entity) {
        // Lazy load of items
        if (entity.getMaterial() != null) {
            entity.getMaterial().setComponent(null);
        };
    }
    
    /**
     *
     * @param code
     * @return
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public br.com.altamira.data.model.common.MaterialAlias find(
            @NotNull @Size(min = 3, message = CODE_VALIDATION) String code)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<br.com.altamira.data.model.common.MaterialAlias> criteriaQuery = cb.createQuery(getTypeClass());
        Root<br.com.altamira.data.model.common.MaterialAlias> entity = criteriaQuery.from(getTypeClass());

        criteriaQuery.select(entity);

        criteriaQuery.where(
                cb.equal(cb.lower(entity.get(MaterialAlias_.code)), code.toLowerCase().trim()));

        br.com.altamira.data.model.common.MaterialAlias alias;
        
        try {
            alias = entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (EJBException ejbex) {
            if (ejbex.getCause() instanceof NoResultException) {
                return null;
            }
            throw ejbex;
        }

        lazyLoad(alias);

        return alias;
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<MaterialAlias> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaterialAlias> criteriaQuery = cb.createQuery(getTypeClass());
        Root<MaterialAlias> entity = criteriaQuery.from(getTypeClass());

        criteriaQuery.select(entity);

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()) {

            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get(MaterialAlias_.code)), searchCriteria),
                    cb.like(cb.lower(entity.get(MaterialAlias_.description)), searchCriteria)));
        }

        return criteriaQuery;
    }
}
