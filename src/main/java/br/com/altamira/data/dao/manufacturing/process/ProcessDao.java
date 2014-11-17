package br.com.altamira.data.dao.manufacturing.process;

import br.com.altamira.data.dao.BaseDao;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import br.com.altamira.data.model.manufacturing.process.Process;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author alessandro.holanda
 */
@Stateless
public class ProcessDao extends BaseDao<Process> {

    @Override
    public void lazyLoad(Process entity) {
        // Lazy load of revision
        entity.getRevision().size();

        // Lazy load of operation
        if (entity.getOperation() != null) {
            entity.getOperation().size();
            entity.getOperation().stream().forEach((operation) -> {
                operation.getUse().size();
                operation.getConsume().size();
                operation.getProduce().size();
            });
        }
    }

    @Override
    public void resolveDependencies(Process entity, MultivaluedMap<String, String> parameters) {
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
    }

    @Override
    public CriteriaQuery getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Process> criteriaQuery = cb.createQuery(Process.class);
        Root<Process> entity = criteriaQuery.from(Process.class);

        criteriaQuery.select(cb.construct(Process.class,
                entity.get("id"),
                entity.get("code"),
                entity.get("description")));

        if (parameters.get("search") != null &&
                !parameters.get("search").isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get("code")), searchCriteria),
                    cb.like(cb.lower(entity.get("description")), searchCriteria)));
        }
        
        criteriaQuery.orderBy(cb.desc(entity.get("lastModified")));

        return criteriaQuery;
    }

    /**
     *
     * @param startPage
     * @param pageSize
     * @return
     */
    /*@Override
     public List<Process> list(
     @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
     @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
     throws ConstraintViolationException, NoResultException {

     CriteriaBuilder cb = entityManager.getCriteriaBuilder();
     CriteriaQuery<Process> q = cb.createQuery(Process.class);
     Root<Process> entity = q.from(Process.class);

     q.select(cb.construct(Process.class,
     entity.get("id"),
     entity.get("code"),
     entity.get("description")));

     q.orderBy(cb.desc(entity.get("lastModified")));

     return entityManager.createQuery(q)
     .setFirstResult(startPage * pageSize)
     .setMaxResults(pageSize)
     .getResultList();

     }*/
    /**
     *
     * @param search
     * @param startPage
     * @param pageSize
     * @return
     */
    /*@Override
    public List<Process> search(
            @NotNull @Size(min = 2, message = SEARCH_VALIDATION) String search,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Process> q = cb.createQuery(Process.class);
        Root<Process> entity = q.from(Process.class);

        String searchCriteria = "%" + search.toLowerCase().trim() + "%";

        q.select(cb.construct(Process.class,
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

    }*/

    /**
     *
     * @param id
     * @return
     */
    /*@Override
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
    }*/

    /**
     *
     * @param entity
     * @return
     */
    /*@Override
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
    }*/

    /**
     *
     * @param entity
     * @return
     */
    /*@Override
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
    }*/
}
