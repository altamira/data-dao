package br.com.altamira.data.dao.manufacture.process;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import br.com.altamira.data.model.manufacture.process.Process;
import br.com.altamira.data.model.measurement.Unit;
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

                operation.getSketch();

                if (operation.getUse() != null) {
                    operation.getUse().size();
                    operation.getUse().stream().forEach((use) -> {
                        //use.getMaterial().getComponent().size();
                        use.getMaterial().setComponent(null);
                    });
                }

                if (operation.getConsume() != null) {
                    operation.getConsume().size();
                    operation.getConsume().stream().forEach((consume) -> {
                        //consume.getMaterial().getComponent().size();
                        consume.getMaterial().setComponent(null);
                    });
                }

                if (operation.getProduce() != null) {
                    operation.getProduce().size();
                    operation.getProduce().stream().forEach((produce) -> {
                        //produce.getMaterial().getComponent().size();
                        produce.getMaterial().setComponent(null);
                    });
                }
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

            if (operation.getSketch() != null
                    && operation.getSketch().getImage().length > 0) {
                operation.getSketch().setOperation(operation);
            } else {
                operation.setSketch(null);
            }

            operation.getUse().stream().forEach((use) -> {
                use.setOperation(operation);
                use.setMaterial(entityManager.find(Material.class, use.getMaterial().getId()));
                use.getQuantity().setUnit(entityManager.find(Unit.class, use.getQuantity().getUnit().getId()));
            });
            operation.getConsume().stream().forEach((consume) -> {
                consume.setOperation(operation);
                consume.setMaterial(entityManager.find(Material.class, consume.getMaterial().getId()));
                consume.getQuantity().setUnit(entityManager.find(Unit.class, consume.getQuantity().getUnit().getId()));
            });
            operation.getProduce().stream().forEach((produce) -> {
                produce.setOperation(operation);
                produce.setMaterial(entityManager.find(Material.class, produce.getMaterial().getId()));
                produce.getQuantity().setUnit(entityManager.find(Unit.class, produce.getQuantity().getUnit().getId()));
            });

        });
    }

    @Override
    public CriteriaQuery<Process> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Process> criteriaQuery = cb.createQuery(Process.class);
        Root<Process> entity = criteriaQuery.from(Process.class);

        criteriaQuery.select(cb.construct(Process.class,
                entity.get("id"),
                entity.get("code"),
                entity.get("description")));

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get("code")), searchCriteria),
                    cb.like(cb.lower(entity.get("description")), searchCriteria)));
        } else {
            // TODO: list pending processes        	
        }

        criteriaQuery.orderBy(cb.desc(entity.get("lastModified")));

        return criteriaQuery;
    }

}
