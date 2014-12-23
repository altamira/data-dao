package br.com.altamira.data.dao.manufacture.bom;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Color;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.bom.BOM;
import br.com.altamira.data.model.manufacture.bom.BOMItem;
import br.com.altamira.data.model.manufacture.bom.BOMItemPart;

import javax.ejb.Stateless;

import java.util.Date;
import javax.inject.Inject;
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

    @Inject
    private br.com.altamira.data.dao.common.MaterialAliasDao materialAliasDao;

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(BOM entity) {

        // Lazy load of items
        if (entity.getItems() != null) {
            entity.getItems().size();
            entity.getItems().stream().forEach((item) -> {
                item.getParts().size();
                item.getParts().stream().forEach((part) -> {
                    part.getMaterial().setComponent(null);
                });
            });
        }
    }

    /**
     *
     * @param entity
     * @param parameters
     */
    @Override
    public void resolveDependencies(BOM entity, MultivaluedMap<String, String> parameters) throws IllegalArgumentException {
        entity.setChecked(null);
        
        // Resolve dependencies
        entity.getItems().stream().forEach((BOMItem item) -> {
            item.setBOM(entity);
            item.getParts().stream().forEach((BOMItemPart part) -> {
                part.setBOMItem(item);
                part.setColor(entityManager.find(Color.class, part.getColor().getId()));
                
                Material material = entityManager.find(Material.class, part.getMaterial().getId());
                
                if (material == null) {
                    br.com.altamira.data.model.common.MaterialAlias alias = materialAliasDao.find(part.getMaterial().getCode());
                    if (alias != null) {
                        material = alias.getMaterial();
                    } else {
                        throw new IllegalArgumentException("ITEM " + item.getItem() + ", material " + part.getMaterial().getCode() + " " + part.getMaterial().getDescription() + " não foi encontrado no cadastro de Materiais");
                    }
                }
                
                part.setMaterial(material);
            });
        });
     }

    /**
     *
     * @param parameters
     * @return
     */
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
        if (parameters.get("checked") != null
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
