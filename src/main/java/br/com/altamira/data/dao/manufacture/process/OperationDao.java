package br.com.altamira.data.dao.manufacture.process;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;

import javax.ejb.Stateless;
import br.com.altamira.data.model.manufacture.process.Operation;
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.measurement.Variables;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 * @author Alessandro
 */
@Stateless
public class OperationDao extends BaseDao<Operation> {

    private final Variables variable = new Variables();

    /**
     * @return the variable
     */
    public Variables getVariable() {
        return variable;
    }

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

    public MultivaluedHashMap<String, Material> calcule(Operation operation, Map<String, Measure> measurementParameters, @NotNull MultivaluedMap<String, String> requestParameters) {
        MultivaluedHashMap<String, Material> results = new MultivaluedHashMap<>();

        List<Material> produces = new ArrayList<>();
        List<Material> consumes = new ArrayList<>();
        List<Material> uses = new ArrayList<>();

        measurementParameters.forEach((key, value) -> {
            this.variable.put(key, value.getValue());
        });

        // calcule produces
        operation.getProduce().forEach((produce) -> {
            produce.getMaterial().calcule(this.variable);
        });

        // calcule consumes
        operation.getConsume().forEach((consume) -> {
            consume.getMaterial().calcule(this.variable);

            // calcule quantity
            /*Expression exp = new Expression(consume.getQuantity().getFormula());
             exp.setVariables(variable);

             Measure quantity = new Measure();

             // resolve unknow variables
             exp.getExpressionVariables().forEach((v) -> {
             if (!variable.containsKey(v)) {
             variable.replace(v, solveVariable(operation, measurementParameters, v));
             }
             });
             exp.setVariables(variable);
             quantity.setValue(exp.eval());
             quantity.setUnit(consume.getQuantity().getUnit());*/
            
            consumes.add(consume.getMaterial());
        });

        // calcule uses
        operation.getUse().forEach((use) -> {
            uses.add(use.getMaterial());
        });

        measurementParameters.forEach((key, value) -> {
            this.variable.put(key, value.getValue());
        });

        // recalcule produces
        operation.getProduce().forEach((produce) -> {
            produce.getMaterial().calcule(this.variable);

            // calcule quantity
            /*Expression exp = new Expression(produce.getQuantity().getFormula());
             exp.setVariables(variable);

             Measure quantity = new Measure();

             // resolve unknow variables
             exp.getExpressionVariables().forEach((v) -> {
             if (!variable.containsKey(v)) {
             variable.replace(v, solveVariable(operation, measurementParameters, v));
             }
             });
             exp.setVariables(variable);
             quantity.setValue(exp.eval());
             quantity.setUnit(produce.getQuantity().getUnit());*/
            
            produces.add(produce.getMaterial());
        });

        results.put("produce", produces);
        results.put("consume", consumes);
        results.put("use", uses);

        return results;
    }

}
