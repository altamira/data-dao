/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacture.process;

import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.process.Operation;
import br.com.altamira.data.model.manufacture.process.Produce;
import br.com.altamira.data.model.measurement.Expression;
import br.com.altamira.data.model.measurement.Formula;
import br.com.altamira.data.model.measurement.Measure;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 *
 * @author Alessandro
 */
@Stateless
public class ProduceDao extends ResourceDao<Produce> {

    public Map<Material, Map<String, Measure>> calcule(Operation operation, Map<String, Measure> variables, @NotNull MultivaluedMap<String, String> parameters) {
        Map<Material, Map<String, Measure>> bom = new HashMap<>();
        Map<String, BigDecimal> variable = new HashMap<>();
        
        variables.forEach((key, value) -> {
            variable.put(key, value.getValue());
        });
        
        operation.getConsume().forEach((consume) -> {
            Map<String, Measure> quantities = new HashMap<>();
            Formula formula = consume.getQuantity();
            Expression exp = new Expression(formula.getFormula());
            
            exp.setVariables(variable);
            Measure quantity = new Measure();
            quantity.setValue(exp.eval());
            quantity.setUnit(consume.getQuantity().getUnit());
            quantities.put("quantidade", quantity);
            bom.put(consume.getMaterial(), quantities);
        });
        
        return bom;
    }
}
