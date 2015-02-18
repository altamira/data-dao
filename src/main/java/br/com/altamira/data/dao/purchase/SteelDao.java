/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.purchase;

import br.com.altamira.data.dao.common.MaterialBaseDao;
import br.com.altamira.data.model.purchase.Steel;

import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "purchase.SteelDao")
public class SteelDao extends MaterialBaseDao<Steel> {
    
}
