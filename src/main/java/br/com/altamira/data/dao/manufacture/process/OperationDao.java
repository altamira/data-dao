package br.com.altamira.data.dao.manufacture.process;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.process.Consume;

import javax.ejb.Stateless;
import br.com.altamira.data.model.manufacture.process.Operation;
import br.com.altamira.data.model.manufacture.process.Operation_;
import br.com.altamira.data.model.manufacture.process.Produce;
import br.com.altamira.data.model.manufacture.process.Use;
import br.com.altamira.data.model.measurement.Expression.UnresolvedTokenException;
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
@Stateless(name = "br.com.altamira.data.dao.manufacture.process.OperationDao")
public class OperationDao extends BaseDao<Operation> {

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
                entity.get(Operation_.id),
                entity.get(Operation_.sequence),
                entity.get(Operation_.name)));

        br.com.altamira.data.model.manufacture.process.Process process = entityManager.find(br.com.altamira.data.model.manufacture.process.Process.class,
                Long.parseLong(parameters.get("parentId").get(0)));

        criteriaQuery.where(cb.equal(entity.get(Operation_.process), process.getId()));

        return criteriaQuery;
    }

    public MultivaluedHashMap<String, Material> calcule(Operation operation, Map<String, Measure> measurementParameters, @NotNull MultivaluedMap<String, String> requestParameters) {
        MultivaluedHashMap<String, Material> results = new MultivaluedHashMap<>();

        Variables variable = new Variables();
        Variables measure = new Variables();

        List<Material> produces = new ArrayList<>();
        List<Material> consumes = new ArrayList<>();
        List<Material> uses = new ArrayList<>();

        // load parameter variables
        for (Map.Entry<String, Measure> entry : measurementParameters.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
            variable.put(entry.getKey(), entry.getValue().getValue());
        }

        // resolve unknow variables
        for (Produce produce : operation.getProduce()) {
            try {
                produce.getMaterial().setVariable(variable);
            } catch (UnresolvedTokenException tokenEx) {

                for (String token : tokenEx.getTokens()) {
                    variable = resolveToken(operation, token, variable);
                };
            }
        };

        // calcule produces
        for (Produce produce : operation.getProduce()) {
            produce.getMaterial().setVariable(variable);
        };

        // calcule consumes
        for (Consume consume : operation.getConsume()) {
            consume.getMaterial().setVariable(variable);

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
        };

        // calcule uses
        for (Use use : operation.getUse()) {
            uses.add(use.getMaterial());
        };

        results.put("produce", produces);
        results.put("consume", consumes);
        results.put("use", uses);

        return results;
    }

    private Variables resolveToken(Operation operation, String token, Variables variable) {
        // resolve unknow variables

        for (Consume consume : operation.getConsume()) {
            try {
                variable = consume.getMaterial().setVariable(variable);
            } catch (UnresolvedTokenException tokenEx) {
                for (String t : tokenEx.getTokens()) {
                    variable = resolveToken(operation, t, variable);
                };
            }
        }

        return variable;
    }
}
