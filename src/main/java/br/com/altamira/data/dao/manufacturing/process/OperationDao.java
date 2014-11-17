package br.com.altamira.data.dao.manufacturing.process;

import br.com.altamira.data.dao.BaseDao;

import javax.ejb.Stateless;
import br.com.altamira.data.model.manufacturing.process.Operation;
import java.util.Map;
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

        br.com.altamira.data.model.manufacturing.process.Process process = entityManager.find(
                br.com.altamira.data.model.manufacturing.process.Process.class, 
                Long.parseLong(parameters.get("parentId").get(0)));
        
        criteriaQuery.where(cb.equal(entity.get("process"), process.getId()));
        
        return criteriaQuery;
    }
    
    /**
     *
     * @param id
     * @param startPage
     * @param pageSize
     * @return
     */
    /*public List<Operation> list(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Operation> q = cb.createQuery(Operation.class);
        Root<Operation> entity = q.from(Operation.class);

        q.select(cb.construct(Operation.class,
                entity.get("id"),
                entity.get("sequence"),
                entity.get("name")));

        q.where(cb.equal(entity.get("process"), id));

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }*/

    /**
     *
     * @return
     */
    /*@Override
    public Operation find(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException {

        Operation entity = super.find(id);

        entity.getSketch();
        entity.getUse().size();
        entity.getConsume().size();
        entity.getProduce().size();

        return entity;
    }*/
    
    /**
     *
     * @param id
     * @param entity
     * @return
     * @throws ConstraintViolationException
     */
    /*public Operation create(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id,
            @NotNull(message = ENTITY_VALIDATION) Operation entity)
            throws ConstraintViolationException {
        
        entity.setProcess(processDao.find(id));
        
        //if (entity.getSketch() != null) {
        //	entity.getSketch().setOperation(entity);
        //}
        
        entity.getUse().stream().forEach((u) -> {
            u.setOperation(entity);
        });
        
        entity.getConsume().stream().forEach((c) -> {
            c.setOperation(entity);
        });
        
        entity.getProduce().stream().forEach((p) -> {
            p.setOperation(entity);
        });
        
        return super.create(entity);
    }*/

    /**
     *
     * @param id
     * @param entity
     * @return
     * @throws ConstraintViolationException
     * @throws IllegalArgumentException
     */
    /*public Operation update(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id,
            @NotNull(message = ENTITY_VALIDATION) Operation entity)
            throws ConstraintViolationException, IllegalArgumentException {
        
        entity.setProcess(processDao.find(id));
        
        //if (entity.getSketch() != null) {
        //	entity.getSketch();
        //}
        
        entity.getUse().stream().forEach((u) -> {
            u.setOperation(entity);
        });
        
        entity.getConsume().stream().forEach((c) -> {
            c.setOperation(entity);
        });
        
        entity.getProduce().stream().forEach((p) -> {
            p.setOperation(entity);
        });
        
        return super.update(entity);
    } */  
}
