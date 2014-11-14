/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.manufacturing.process;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacturing.process.Revision;
import javax.ejb.Stateless;

/**
 *
 * @author Alessandro
 */
@Stateless
public class RevisionDao  extends BaseDao<Revision> {

    public RevisionDao() {
        this.type = Revision.class;
    }
    
}
