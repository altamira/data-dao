package br.com.altamira.data.dao.common;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MultivaluedMap;

import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.common.Material_;
import br.com.altamira.data.model.manufacture.process.Process;

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
    
    public Material updateProcessId(String processId,MultivaluedMap<String, String> parameters){
    	
    	Process process = entityManager.find(Process.class, Long.parseLong(processId));
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	
    	CriteriaUpdate<Material> updateQuery = cb.createCriteriaUpdate(Material.class);
    	Root<Material> root = updateQuery.from(Material.class);
    	updateQuery.set(root.get(Material_.process), process);
    	updateQuery.where(cb.equal(root.get(Material_.id), Long.parseLong(parameters.get("id").get(0))));
    	
    	entityManager.createQuery(updateQuery).executeUpdate();
    	
    	entityManager.flush();
    	
    	return entityManager.find(Material.class, Long.parseLong(parameters.get("id").get(0)));
    }
}
