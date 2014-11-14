package br.com.altamira.data.dao.manufacturing.process;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.manufacturing.process.Use;

import javax.ejb.Stateless;

/**
 *
 *
 * @author Alessandro
 */
@Stateless
public class UseDao extends BaseDao<Use> {

    public UseDao() {
        this.type = Use.class;
    }
}
