/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.sales;

import br.com.altamira.data.dao.common.MaterialBaseDao;
import br.com.altamira.data.model.sales.Component;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "sales.Component")
public class ComponentDao extends MaterialBaseDao<Component> {
    
}
