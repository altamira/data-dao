package br.com.altamira.data.dao.manufacture.bom;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.manufacture.bom.BOM;

import javax.ejb.Stateless;

import java.util.Date;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Sales bom persistency strategy
 *
 */
@Stateless
public class BOMDao extends BaseDao<BOM> {

    @Override
    public void lazyLoad(BOM entity) {

        // Lazy load of items
        if (entity.getItems() != null) {
            entity.getItems().size();
            entity.getItems().stream().forEach((item) -> {
                item.getParts().size();
                item.getParts().stream().forEach((part) -> {

                });
            });
        }
    }

    @Override
    public void resolveDependencies(BOM entity, MultivaluedMap<String, String> parameters) {
        // Resolve dependencies
        entity.getItems().stream().map((item) -> {
            item.setBOM(entity);
            return item;
        }).forEach((item) -> {
            item.getParts().stream().forEach((part) -> {
                part.setBOMItem(item);
            });
        });
    }

    @Override
    public CriteriaQuery<BOM> getCriteriaQuery(
            @NotNull MultivaluedMap<String, String> parameters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);
        Root<BOM> entity = criteriaQuery.from(BOM.class);

        criteriaQuery.select(cb.construct(BOM.class,
                entity.get("id"),
                entity.get("number"),
                entity.get("customer"),
                entity.get("checked")));

        // filter by checked/unchecked
        if (parameters.get("checked") != null && 
                !parameters.get("checked").isEmpty()) {
            boolean checked = Boolean.parseBoolean(parameters.get("checked").get(0));
            
            criteriaQuery.where(checked ? 
                    cb.isNotNull(entity.get("checked")) :
                    cb.isNull(entity.get("checked")));
        } 
            
        if (parameters.get("search") != null && 
                !parameters.get("search").isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get("number").as(String.class)), searchCriteria),
                    cb.like(cb.lower(entity.get("customer")), searchCriteria)));
        }

        return criteriaQuery;
    }

    /**
     *
     * @param checked
     * @param id
     * @return
     */
    public BOM updateChecked(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id,
            @NotNull boolean checked) {

        BOM bom = entityManager.find(BOM.class, id);

        bom.setChecked(checked ? new Date() : null);

        return super.update(bom, null);

    }

}
