package br.com.altamira.data.dao.common;

import javax.ejb.Stateless;

import br.com.altamira.data.model.common.Material;

/**
 *
 * @author Alessandro
 */
@Stateless(name = "common.MaterialDao")
public class MaterialDao extends MaterialBaseDao<Material> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Material entity) {
        // Lazy load of items
        entity.setComponent(null);
    }
}
