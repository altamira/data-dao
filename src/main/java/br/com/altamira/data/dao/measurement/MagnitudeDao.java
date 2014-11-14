/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.measurement;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.measurement.Magnitude;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless
public class MagnitudeDao extends BaseDao<Magnitude> {

    public MagnitudeDao() {
        this.type = Magnitude.class;
    }
}