package br.com.altamira.data.dao.shipping.planning;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.shipping.planning.Planning;
import br.com.altamira.data.model.shipping.planning.PlanningItem;

import javax.ejb.Stateless;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Sales bom persistency strategy
 *
 */
@Stateless
public class PlanningDao extends BaseDao<Planning> {

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(Planning entity) {
        // Lazy load of items
        if (entity.getItems() != null) {
            entity.getItems().size();
        }
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(Planning entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {
        // Resolve dependencies
        entity.getItems().stream().forEach((PlanningItem item) -> {
            item.setPlanning(entity);
        });
    }

    /**
     *
     * @param parameters
     * @return
     */
    @Override
    public CriteriaQuery<Planning> getCriteriaQuery(
            @NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Planning> criteriaQuery = cb.createQuery(Planning.class);
        Root<Planning> entity = criteriaQuery.from(Planning.class);

        criteriaQuery.select(cb.construct(Planning.class,
                entity.get("id"),
                entity.get("number")));

        // filter by checked/unchecked
        /*if (parameters.get("checked") != null
                && !parameters.get("checked").isEmpty()) {
            boolean checked = Boolean.parseBoolean(parameters.get("checked").get(0));

            criteriaQuery.where(checked
                    ? cb.isNotNull(entity.get("checked"))
                    : cb.isNull(entity.get("checked")));
        }

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get("number").as(String.class)), searchCriteria),
                    cb.like(cb.lower(entity.get("customer")), searchCriteria)));
        }*/

        return criteriaQuery;
    }

}
