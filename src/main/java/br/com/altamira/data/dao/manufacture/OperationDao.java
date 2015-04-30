/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacture.Operation;
import br.com.altamira.data.model.manufacture.Operation_;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.manufacture.OperationDao")
public class OperationDao extends BaseDao<Operation> {
	
	// ALTAMIRA-223: Manufacture operation - search parameter not working
	@Override
	public CriteriaQuery<Operation> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
	
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Operation> criteriaQuery = cb.createQuery(Operation.class);
        Root<Operation> entity = criteriaQuery.from(Operation.class);

        criteriaQuery.select(entity);

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()
                && !parameters.get("search").get(0).isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get(Operation_.name).as(String.class)), searchCriteria),
                    cb.like(cb.lower(entity.get(Operation_.description).as(String.class)), searchCriteria)));
        }

        return criteriaQuery;
	}
    
}
