package br.com.altamira.data.dao.manufacture.process;

import br.com.altamira.data.dao.BaseDao;

import javax.ejb.Stateless;
import br.com.altamira.data.model.manufacture.process.Operation;
import javax.ejb.EJB;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 * @author Alessandro
 */
@Stateless
public class OperationDao extends BaseDao<Operation> {
    
    @EJB
    private ProcessDao processDao;
    
    @Override
    public void lazyLoad(Operation entity) {
        entity.getSketch();
        entity.getUse().size();
        entity.getConsume().size();
        entity.getProduce().size();    
    }
    
    @Override
    public void resolveDependencies(Operation entity, MultivaluedMap<String, String> parameters) {
        entity.getUse().stream().forEach((u) -> {
            u.setOperation(entity);
        });
        
        entity.getConsume().stream().forEach((c) -> {
            c.setOperation(entity);
        });
        
        entity.getProduce().stream().forEach((p) -> {
            p.setOperation(entity);
        });
    }

    @Override
    public CriteriaQuery getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Operation> criteriaQuery = cb.createQuery(Operation.class);
        Root<Operation> entity = criteriaQuery.from(Operation.class);

        criteriaQuery.select(cb.construct(Operation.class,
                entity.get("id"),
                entity.get("sequence"),
                entity.get("name")));

        br.com.altamira.data.model.manufacture.process.Process process = entityManager.find(br.com.altamira.data.model.manufacture.process.Process.class, 
                Long.parseLong(parameters.get("parentId").get(0)));
        
        criteriaQuery.where(cb.equal(entity.get("process"), process.getId()));
        
        return criteriaQuery;
    }
    
}
