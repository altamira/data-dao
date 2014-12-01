package br.com.altamira.data.dao.purchasing.request;

import br.com.altamira.data.model.purchase.Request;
import br.com.altamira.data.dao.BaseDao;

import javax.ejb.Stateless;


/**
 *
 * @author alessandro.holanda
 */
@Stateless
public class RequestDao extends BaseDao<Request> {

//	@Inject
//	private EntityManager entityManager;
//
//    /**
//     *
//     * @param startPosition
//     * @param maxResult
//     * @return
//     */
//    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) 
//	public List<Request> list(int startPosition, int maxResult) {
//
//		TypedQuery<Request> findAllQuery = entityManager.createNamedQuery("Request.list", Request.class);
//
//		findAllQuery.setFirstResult(startPosition);
//		findAllQuery.setMaxResults(maxResult);
//
//		return findAllQuery.getResultList();
//	}
//	
//    /**
//     *
//     * @param requestId
//     * @param startPosition
//     * @param maxResult
//     * @return
//     */
//    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) 
//	public List<RequestItem> listItems(Long requestId, int startPosition, int maxResult) {
//
//		TypedQuery<RequestItem> findAllQuery = entityManager.createNamedQuery("Request.items", RequestItem.class);
//		findAllQuery.setParameter("requestId", requestId);
//		
//		findAllQuery.setFirstResult(startPosition);
//		findAllQuery.setMaxResults(maxResult);
//
//		return findAllQuery.getResultList();
//	}
//	
//    /**
//     *
//     * @param id
//     * @return
//     */
//    public Request find(long id) {
//        Request entity;
//
//        if (id == 0) {
//        	entity = current();
//        } else {
//			TypedQuery<Request> findByIdQuery = entityManager.createNamedQuery("Request.findById", Request.class);
//	        findByIdQuery.setParameter("id", id);
//	        try {
//	            entity = findByIdQuery.getSingleResult();
//	        } catch (NoResultException nre) {
//	            return null;
//	        }
//        }
//        
//        // Lazy load of items
//        if (entity.getItem() != null) {
//        	entity.getItem().size();
//        }
//
//        return entity;
//	}
//	
//    /**
//     *
//     * @param entity
//     * @return
//     */
//    public Request create(Request entity) {
//		if (entity == null) {
//			throw new IllegalArgumentException("Entity can't be null.");
//		}
//		
//		if (entity.getId() != null && entity.getId() > 0) {
//			throw new IllegalArgumentException("To create this entity, id must be null or zero.");
//		}
//		
//		entity.setId(null);
//
//		entityManager.persist(entity);
//		entityManager.flush();
//		
//		// Reload to update child references
//		
//		return entityManager.find(Request.class, entity.getId());
//	}
//	
//    /**
//     *
//     * @param entity
//     * @return
//     */
//    public Request update(Request entity) {
//		if (entity == null) {
//			throw new IllegalArgumentException("Entity can't be null.");
//		}
//		if (entity.getId() == null || entity.getId() == 0l) {
//			throw new IllegalArgumentException("Entity id can't be null or zero.");
//		}
//		
//		entityManager.merge(entity);
//
//		// Reload to update child references
//		
//		return entityManager.find(Request.class, entity.getId());
//	}
//	
//    /**
//     *
//     * @param entity
//     * @return
//     */
//    /*public Request remove(Request entity) {
//		if (entity == null) {
//			throw new IllegalArgumentException("Entity can't be null.");
//		}
//		
//		if (entity.getId() == null || entity.getId() == 0l) {
//			throw new IllegalArgumentException("Entity id can't be null or zero.");
//		}
//		
//		return remove(entity.getId());
//	}*/
//	
//    /**
//     *
//     * @param id
//     * @return
//     */
//    /*public Request remove(long id) {
//		if (id == 0) {
//			throw new IllegalArgumentException("Entity id can't be zero.");
//		}
//		
//		//entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
//
//		Request entity = entityManager.find(Request.class, id);
//        
//		if (entity == null) {
//			throw new IllegalArgumentException("Entity not found.");
//		}
//		
//	    entityManager.remove(entity);
//	    entityManager.flush();
//		
//		return entity;
//	}*/
//	
//    /**
//     *
//     * @return
//     */
//    public Request current() {
//        List<Request> requests;
//
//        Request entity;
//        
//        requests = (List<Request>) entityManager
//                .createNamedQuery("Request.current", Request.class)
//                .getResultList();
//
//        if (requests.isEmpty()) {
//
//            entity = create(new Request(new Date(), "system"));
//
//        } else {
//        	entity = requests.get(0);
//        	
//        	// Lazy load of items
//        	if (entity.getItem() != null) {
//        		entity.getItem().size();
//        	}
//        }
//
//        return entity;
//    }
//
//    /*public byte[] getRequestReportJasperFile() throws SQLException {
//        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT JASPER_FILE FROM REQUEST_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM REQUEST_REPORT)")
//                .getSingleResult();
//
//        return tempBlob.getBytes(1, (int) tempBlob.length());
//    }
//
//    public byte[] getRequestReportAltamiraImage() throws SQLException {
//        Blob tempBlob = (Blob) entityManager.createNativeQuery("SELECT ALTAMIRA_LOGO FROM REQUEST_REPORT WHERE REPORT = (SELECT MAX(REPORT) FROM REQUEST_REPORT)")
//                .getSingleResult();
//
//        return tempBlob.getBytes(1, (int) tempBlob.length());
//    }*/
//
//    /**
//     *
//     * @param requestId
//     * @return
//     */
//    
//
//    public List<Object[]> selectRequestReportDataById(long requestId) {
//        StringBuffer selectSql = new StringBuffer().append(" SELECT M.ID, ")
//                                                   .append("        M.LAMINATION, ")
//                                                   .append("        M.TREATMENT, ")
//                                                   .append("        M.THICKNESS, ")
//                                                   .append("        M.WIDTH, ")
//                                                   .append("        M.LENGTH, ")
//                                                   .append("        RT.WEIGHT, ")
//                                                   .append("        RT.ARRIVAL_DATE ")
//                                                   .append(" FROM REQUEST R, REQUEST_ITEM RT, MATERIAL M ")
//                                                   .append(" WHERE R.ID = RT.REQUEST ")
//                                                   .append(" AND RT.MATERIAL = M.ID ")
//                                                   .append(" AND R.ID = :request_id ");
//
//        @SuppressWarnings("unchecked")
//		List<Object[]> list = entityManager.createNativeQuery(selectSql.toString())
//                                           .setParameter("request_id", requestId)
//                                           .getResultList();
//
//        return list;
//    }
//
//    /*public boolean insertGeneratedRequestReport(JasperPrint print) {
//        byte[] bArray = null;
//
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ObjectOutputStream oos = new ObjectOutputStream(baos);
//
//            oos.writeObject(print);
//            oos.close();
//            baos.close();
//
//            bArray = baos.toByteArray();
//        } catch (Exception e) {
//            System.out.println("Error converting JasperPrint object to byte[] array");
//            e.printStackTrace();
//            return false;
//        }
//
//        try {
//            RequestReportLog log = new RequestReportLog();
//            log.setReportInstance(bArray);
//
//            entityManager.persist(log);
//        } catch (Exception e) {
//            System.out.println("Error while inserting generated report in database.");
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }*/
}
