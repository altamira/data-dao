/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.planning.Status;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "br.com.altamira.data.dao.shipping.StatusDao")
public class StatusDao extends BaseDao<Status> {
    
}
