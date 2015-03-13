package br.com.altamira.data.dao.manufacture.bom;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.manufacture.bom.BOM;
import br.com.altamira.data.model.manufacture.bom.BOM_;
import br.com.altamira.data.model.manufacture.bom.Delivery;
import br.com.altamira.data.model.manufacture.bom.Item;
import br.com.altamira.data.model.measurement.Measure;
import br.com.altamira.data.model.shipping.execution.Delivered;
import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Sales bom persistency strategy
 *
 */
@Stateless(name = "manufacture.bom.BOMDao")
public class BOMDao extends BaseDao<BOM> {

    @Inject
    private br.com.altamira.data.dao.common.MaterialDao materialDao;

    @Inject
    private br.com.altamira.data.dao.common.MaterialAliasDao materialAliasDao;

    @Inject
    private br.com.altamira.data.dao.manufacture.bom.DeliveryDao deliveryDao;

    /**
     *
     * @param entity
     */
    @Override
    public void lazyLoad(BOM entity) {
        // Lazy load of items
        if (entity.getItem() != null) {
            entity.getItem().size();

            //ALTAMIRA-76: hides ITEM 0 from materials list 
            entity.getItem().removeIf(p -> p.getItem() == 0);

            entity.getItem().stream().forEach((item) -> {
                item.getComponent().size();
                item.getComponent().stream().forEach((component) -> {
                    component.getMaterial().setComponent(null);
                    component.setDelivery(null);
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
        entity.setChecked(false);

        // Resolve dependencies
        entity.getItem().stream().forEach((Item item) -> {
            item.setBOM(entity);
            item.getComponent().stream().forEach((component) -> {
                // get reference from parent
                component.setItem(item);

                // resolve color
                //component.setColor(entityManager.find(Color.class, component.getColor().getId()));

                // resolve material
                Material material = null;

                // resolve material
                if (component.getMaterial().getId() == null
                        || component.getMaterial().getId() == 0) {
                    material = materialDao.find(component.getMaterial().getCode());
                } else {
                    material = materialDao.find(component.getMaterial().getId());
                }

                if (material == null) {
                    br.com.altamira.data.model.common.MaterialAlias alias = materialAliasDao.find(component.getMaterial().getCode());
                    if (alias != null) {
                        material = alias.getMaterial();
                    }
                }

                // if not found, create
                if (material == null) {
                    if (item.getItem() == 0) {
                        switch (component.getMaterial().getCode().substring(0, 3).toUpperCase()) {
                            case "ALP":
                                material = new br.com.altamira.data.model.purchase.Steel(0l, component.getMaterial().getCode(), component.getMaterial().getDescription());
                            case "TPO":
                                material = new br.com.altamira.data.model.purchase.Ink(0l, component.getMaterial().getCode(), component.getMaterial().getDescription());
                            default:
                                material = new br.com.altamira.data.model.purchase.Inputs(0l, component.getMaterial().getCode(), component.getMaterial().getDescription());
                        }
                    } else {
                        switch (component.getMaterial().getCode().substring(0, 3).toUpperCase()) {
                            case "ALP":
                                material = new br.com.altamira.data.model.purchase.Steel(0l, component.getMaterial().getCode(), component.getMaterial().getDescription());
                            case "TPO":
                                material = new br.com.altamira.data.model.purchase.Ink(0l, component.getMaterial().getCode(), component.getMaterial().getDescription());
                            default:
                                material = new br.com.altamira.data.model.sales.Component(0l, component.getMaterial().getCode(), component.getMaterial().getDescription());
                        }
                    }

                    entityManager.persist(material);
                    entityManager.flush();
                }

                if (material == null) {
                    throw new IllegalArgumentException("ITEM " + item.getItem() + ", material " + component.getMaterial().getCode() + " " + component.getMaterial().getDescription() + " não foi encontrado no cadastro de Materiais");
                }

                component.setMaterial(material);

                // resolve measurements
                /*component.getQuantity().setUnit(entityManager.find(Unit.class, component.getQuantity().getUnit().getId()));
                 component.getWidth().setUnit(entityManager.find(Unit.class, component.getWidth().getUnit().getId()));
                 component.getHeight().setUnit(entityManager.find(Unit.class, component.getHeight().getUnit().getId()));
                 component.getLength().setUnit(entityManager.find(Unit.class, component.getLength().getUnit().getId()));
                 component.getWeight().setUnit(entityManager.find(Unit.class, component.getWeight().getUnit().getId()));*/
                component.getDelivered().setUnit(component.getQuantity().getUnit());
                component.getRemaining().setUnit(component.getQuantity().getUnit());

                // reset delivery dates
                if (entity.getId() != null) {
                    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                    CriteriaQuery<BigDecimal> criteria = cb.createQuery(BigDecimal.class);
                    Root<Delivered> root = criteria.from(Delivered.class);
                    Expression<BigDecimal> sum = cb.sum(root.get("quantity").get("value"));
                    criteria.select(sum);
                    criteria.where(cb.equal(root.get("component").get("id"), entity.getId()));

                    Measure delivered = new Measure(entityManager.createQuery(criteria).getSingleResult(), component.getQuantity().getUnit());

                    component.setDelivered(delivered);

                    CriteriaQuery<Delivery> criteriaQuery = cb.createQuery(Delivery.class);
                    Root<Delivery> delivery = criteriaQuery.from(Delivery.class);
                    criteriaQuery.select(delivery);
                    criteriaQuery.where(cb.equal(delivery.get("component").get("id"), entity.getId()));
                    List<Delivery> deliveries = entityManager.createQuery(criteriaQuery).getResultList();

                    // remove delivery dates
                    entity.setDelivery(null);
                    deliveryDao.removeAll(deliveries);

                    entityManager.flush();
                }
                component.setRemaining(component.getQuantity().subtract(component.getDelivered()));

                // ALTAMIRA-92: trunc the time portion of the date to avoid to use non portable 'trunc()' 
                //              function in group by sql clause
                //Date dt = DateAndTime.stripTimePortion(component.getItem().getBOM().getDelivery());
                java.sql.Date dt = new java.sql.Date(component.getItem().getBOM().getDelivery().getTime());

                // set default delivery date
                Delivery delivery = new Delivery(component, dt, component.getQuantity());
                delivery.setDelivered(component.getDelivered());
                delivery.setRemaining(component.getQuantity().subtract(component.getDelivered()));
                component.setDelivery(new ArrayList<Delivery>() {
                    {
                        add(delivery);
                    }
                });

                if (entity.getId() != null) {
                    entityManager.persist(delivery);
                }
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
                entity.get(BOM_.id),
                entity.get(BOM_.number),
                entity.get(BOM_.customer),
                entity.get(BOM_.created),
                entity.get(BOM_.delivery),
                entity.get(BOM_.checked)));

        // filter by checked/unchecked
        if (parameters.get("checked") != null
                && !parameters.get("checked").isEmpty()) {
            boolean checked = Boolean.parseBoolean(parameters.get("checked").get(0));

            criteriaQuery.where(checked
                    ? cb.isNotNull(entity.get(BOM_.checked))
                    : cb.isNull(entity.get(BOM_.checked)));
        }

        if (parameters.get("search") != null
                && !parameters.get("search").isEmpty()
                && !parameters.get("search").get(0).isEmpty()) {
            String searchCriteria = "%" + parameters.get("search").get(0)
                    .toLowerCase().trim() + "%";

            criteriaQuery.where(cb.or(
                    cb.like(cb.lower(entity.get(BOM_.number).as(String.class)), searchCriteria),
                    cb.like(cb.lower(entity.get(BOM_.customer)), searchCriteria)));
        }

        criteriaQuery.orderBy(cb.asc(entity.get(BOM_.delivery)));

        return criteriaQuery;
    }

    /**
     *
     * @param checked
     * @param id
     */
    public void updateChecked(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long id,
            @NotNull boolean checked) {

        BOM entity = entityManager.find(BOM.class, id);

        entity.setChecked(checked);

        entityManager.merge(entity);
        entityManager.flush();
    }

}
