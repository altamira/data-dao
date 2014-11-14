/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.process;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacturing.process.Sketch;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless
public class SketchDao extends BaseDao<Sketch> {

    public SketchDao() {
        this.type = Sketch.class;
    }
}
