package br.com.altamira.data.dao.manufacturing.process;

import br.com.altamira.data.dao.BaseDao;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.altamira.data.model.manufacturing.process.Process;

/**
 *
 * @author alessandro.holanda
 */
@Stateless
public class ProcessDao extends BaseDao<Process> {

    public ProcessDao() {
        this.type = Process.class;
    }

       /**
     *
     * @param startPage
     * @param pageSize
     * @return
     */
    @Override
    public List<Process> list(
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Process> q = cb.createQuery(type);
        Root<Process> entity = q.from(type);

        q.select(cb.construct(type,
                entity.get("id"),
                entity.get("code"),
                entity.get("description")));

        q.orderBy(cb.desc(entity.get("lastModified")));

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

    }
    
    /**
     *
     * @param search
     * @param startPage
     * @param pageSize
     * @return
     */
    @Override
    public List<Process> search(
            @NotNull @Size(min = 2, message = SEARCH_VALIDATION) String search,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Process> q = cb.createQuery(type);
        Root<Process> entity = q.from(type);

        String searchCriteria = "%" + search.toLowerCase().trim() + "%";

        q.select(cb.construct(type,
                entity.get("id"),
                entity.get("code"),
                entity.get("description")));

        q.where(cb.or(
                cb.like(cb.lower(entity.get("code")), searchCriteria),
                cb.like(cb.lower(entity.get("description")), searchCriteria)));

        log.log(Level.INFO, "Searching for {0}...", searchCriteria);

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public Process find(
            @Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException {

        Process process = super.find(id);

        // Lazy load of revision
        process.getRevision().size();

        // Lazy load of operation
        if (process.getOperation() != null) {
            process.getOperation().size();
            process.getOperation().stream().forEach((operation) -> {
                operation.getUse().size();
                operation.getConsume().size();
                operation.getProduce().size();
            });
        }

        return process;
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public Process create(
            @NotNull(message = ENTITY_VALIDATION) Process entity)
            throws ConstraintViolationException {

        // Resolve dependencies
        entity.getRevision().stream().forEach((r) -> {
            r.setProcess(entity);
        });

        entity.getOperation().stream().map((operation) -> {
            operation.setProcess(entity);
            return operation;
        }).forEach((operation) -> {
            
            operation.getUse().stream().forEach((use) -> {
                use.setOperation(operation);
            });
            operation.getConsume().stream().forEach((consume) -> {
                consume.setOperation(operation);
            });
            operation.getProduce().stream().forEach((produce) -> {
                produce.setOperation(operation);
            });
            
        });

        return super.create(entity);
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public Process update(
            @NotNull(message = ENTITY_VALIDATION) Process entity)
            throws ConstraintViolationException, IllegalArgumentException {

        // Resolve dependencies
        entity.getRevision().stream().forEach((r) -> {
            r.setProcess(entity);
        });

        // Resolve dependencies
        entity.getOperation().stream().map((operation) -> {
            operation.setProcess(entity);
            return operation;
        }).forEach((operation) -> {
            
            operation.getUse().stream().forEach((use) -> {
                use.setOperation(operation);
            });
            operation.getConsume().stream().forEach((consume) -> {
                consume.setOperation(operation);
            });
            operation.getProduce().stream().forEach((produce) -> {
                produce.setOperation(operation);
            });
            
        });

        return super.update(entity);
    }
}
