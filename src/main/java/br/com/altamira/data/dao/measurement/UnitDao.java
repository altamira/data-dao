/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.measurement;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Magnitude;
import br.com.altamira.data.model.measurement.Unit;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Alessandro
 */
@Stateless
public class UnitDao extends BaseDao<Unit> {

    @Override
    public CriteriaQuery getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Unit> criteriaQuery = cb.createQuery(Unit.class);
        Root<Unit> entity = criteriaQuery.from(Unit.class);

        criteriaQuery.select(entity);

        if (parameters.get("magnitude") != null && 
                !parameters.get("magnitude").isEmpty()) {
            String searchCriteria = parameters.get("magnitude").get(0).toLowerCase().trim();

            Join<Unit, Magnitude> magnitude = entity.join("magnitude");
            criteriaQuery.where(cb.equal(cb.lower(magnitude.get("name")), searchCriteria));
        }
        
        return criteriaQuery;
    }
}
