/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.measurement;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Unit;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless
public class UnitDao extends BaseDao<Unit> {

    public UnitDao() {
        this.type = Unit.class;
    }
}
