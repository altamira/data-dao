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

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Operation entity) {
        entity.getSketch();

        entity.getUse().size();
        entity.getConsume().size();
        entity.getProduce().size();

        if (entity.getUse() != null) {
            entity.getUse().size();
            entity.getUse().stream().forEach((use) -> {
                //use.getMaterial().getComponent().size();
                use.getMaterial().setComponent(null);
            });
        }

        if (entity.getConsume() != null) {
            entity.getConsume().size();
            entity.getConsume().stream().forEach((consume) -> {
                //consume.getMaterial().getComponent().size();
                consume.getMaterial().setComponent(null);
            });
        }

        if (entity.getProduce() != null) {
            entity.getProduce().size();
            entity.getProduce().stream().forEach((produce) -> {
                //produce.getMaterial().getComponent().size();
                produce.getMaterial().setComponent(null);
            });
        }
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(Operation entity, MultivaluedMap<String, String> parameters) {
        if (entity.getSketch() != null
                && entity.getSketch().getImage().length > 0) {
            entity.getSketch().setOperation(entity);
        } else {
            entity.setSketch(null);
        }

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

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Operation> getCriteriaQuery(@NotNull MultivaluedMap<String, String> parameters) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Operation> criteriaQuery = cb.createQuery(Operation.class
        );
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
