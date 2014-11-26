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

}
