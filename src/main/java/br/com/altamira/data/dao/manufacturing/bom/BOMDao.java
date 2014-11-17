package br.com.altamira.data.dao.manufacturing.bom;

import br.com.altamira.data.dao.BaseDao;
import static br.com.altamira.data.dao.Dao.ID_NOT_NULL_VALIDATION;
import br.com.altamira.data.model.manufacturing.bom.BOM;

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

        // list unchecked
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
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    /*@Override
    public List<BOM> list(
            @NotNull Map<String, Object> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOM> criteriaQuery = cb.createQuery(BOM.class);
        Root<BOM> entity = criteriaQuery.from(BOM.class);

        criteriaQuery.select(cb.construct(BOM.class,
                entity.get("id"),
                entity.get("number"),
                entity.get("customer"),
                entity.get("checked")));

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }*/

    /**
     *
     * @param startPage
     * @param pageSize
     * @return
     */
    /*public List<BOM> listUnchecked(
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOM> q = cb.createQuery(BOM.class);
        Root<BOM> entity = q.from(BOM.class);

        q.select(cb.construct(BOM.class,
                entity.get("id"),
                entity.get("number"),
                entity.get("customer"),
                entity.get("checked")));

        q.where(cb.isNull(entity.get("checked")));

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();
    }*/

    /*@Override
    public BOM find(
            @Min(value = 0, message = ID_NOT_NULL_VALIDATION) long id)
            throws ConstraintViolationException, NoResultException {

        BOM entity = super.find(id);

        // Lazy load of items
        if (entity.getItems() != null) {
            entity.getItems().size();
            entity.getItems().stream().forEach((item) -> {
                item.getParts().size();
            });
        }

        return entity;
    }*/

    /**
     *
     * @param number
     * @return
     */
    /*public BOM findByNumber(
            @Min(value = 1, message = ID_NOT_NULL_VALIDATION) long number)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOM> q = cb.createQuery(BOM.class);
        Root<BOM> entity = q.from(BOM.class);

        q.select(entity).where(cb.equal(entity.get("number"), number));

        BOM bom = entityManager.createQuery(q).getSingleResult();

        // Lazy load of items
        if (bom.getItems() != null) {
            bom.getItems().size();
            bom.getItems().stream().forEach((item) -> {
                item.getParts().size();
                item.getParts().stream().forEach((part) -> {

                });
            });
        }

        return bom;
    }*/

    /**
     *
     * @param parameters
     * @param startPage
     * @param pageSize
     * @return
     */
    /*@Override
    public List<BOM> search(
            @NotNull Map<String, Object> parameters,
            @Min(value = 0, message = START_PAGE_VALIDATION) int startPage,
            @Min(value = 1, message = PAGE_SIZE_VALIDATION) int pageSize)
            throws ConstraintViolationException, NoResultException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BOM> q = cb.createQuery(BOM.class);
        Root<BOM> entity = q.from(BOM.class);

        String searchCriteria = "%" + parameters.get("search")
                .toString().toLowerCase().trim() + "%";

        q.select(cb.construct(BOM.class,
                entity.get("id"),
                entity.get("number"),
                entity.get("customer"),
                entity.get("checked")));

        q.where(cb.or(
                cb.like(cb.lower(entity.get("number").as(String.class)), searchCriteria),
                cb.like(cb.lower(entity.get("customer")), searchCriteria)));

        log.log(Level.INFO, "Searching for {0}...", searchCriteria);

        return entityManager.createQuery(q)
                .setFirstResult(startPage * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

    }*/

    /**
     *
     * @param entity
     * @return
     */
    /*@Override
    public BOM create(
            @NotNull(message = ENTITY_VALIDATION) BOM entity)
            throws ConstraintViolationException {

        // Resolve dependencies
        entity.getItems().stream().map((item) -> {
            item.setBOM(entity);
            return item;
        }).forEach((item) -> {
            item.getParts().stream().forEach((part) -> {
                part.setBOMItem(item);
                Product product = productDao.findByCode(part.getCode());
                 if (product == null) {
                 product = new Product(
                 part.getCode(),
                 part.getDescription(),
                 part.getColor(),
                 part.getWidth(),
                 part.getHeight(),
                 part.getLength(),
                 part.getWeight());
                 product = productDao.create(product);
                 }
                 part.setProduct(product);
            });
        });

        return super.create(entity);
    }*/

    /**
     *
     * @param entity
     * @return
     */
    /*@Override
    public BOM update(
            @NotNull(message = ENTITY_VALIDATION) BOM entity)
            throws ConstraintViolationException, IllegalArgumentException {

        // Resolve dependencies
        entity.getItems().stream().map((item) -> {
            item.setBOM(entity);
            return item;
        }).forEach((item) -> {
            item.getParts().stream().forEach((part) -> {
                part.setBOMItem(item);
            });
        });

        return super.update(entity);
    }*/

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
