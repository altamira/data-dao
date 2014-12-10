/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.dao.Dao;
import br.com.altamira.data.dao.util.ResourcesFactory;
import br.com.altamira.data.model.BaseEntity;
import br.com.altamira.data.model.common.Component;
import br.com.altamira.data.model.common.Material;
import br.com.altamira.data.model.measurement.Magnitude;
import br.com.altamira.data.model.serialize.JSonViews;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.embeddable.EJBContainer;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.core.MultivaluedMap;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author Alessandro
 */
@RunWith(Arquillian.class)
public class MaterialDaoTest {
    
    /**
     *
     * @return
     */
    @Deployment
    public static Archive<?> createTestArchive() {
        // resolve given dependencies from Maven POM
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").importRuntimeAndTestDependencies()
                .resolve().withTransitivity().asFile();

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "MaterialDaoTest.jar")
                .addPackage(ResourcesFactory.class.getPackage())
                .addPackage(JSonViews.class.getPackage())
                .addPackage(BaseEntity.class.getPackage())
                //.addPackage(BaseEntity_.class.getPackage())
                .addPackage(Magnitude.class.getPackage())
                .addClasses(Dao.class, BaseDao.class)
                //.addAsLibraries(libs)
                .addClasses(Material.class, MaterialBaseDao.class, MaterialDao.class, Component.class)
                .addAsResource("META-INF/test-persistence.xml", "META-INF/persistence.xml");
                //.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        System.out.print(jar.toString(Formatters.VERBOSE));

        return jar;
    }

    @Inject
    Logger log;
    
    /**
     *
     */
    @BeforeClass
    public static void setUpClass() {
    }
    
    /**
     *
     */
    @AfterClass
    public static void tearDownClass() {
    }
    
    /**
     *
     */
    @Before
    public void setUp() {
    }
    
    /**
     *
     */
    @After
    public void tearDown() {
    }

    /**
     * Test of fetchJoin method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testFetchJoin() throws Exception {
        System.out.println("fetchJoin");
        long id = 0L;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao");
        CriteriaQuery<Material> expResult = null;
        CriteriaQuery<Material> result = instance.fetchJoin(id);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of list method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testList() throws Exception {
        System.out.println("list");
        MultivaluedMap<String, String> parameters = null;
        int startPage = 0;
        int pageSize = 0;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        List<Material> expResult = null;
        List<Material> result = instance.list(parameters, startPage, pageSize);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testFind() throws Exception {
        System.out.println("find");
        long id = 0L;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        Material expResult = null;
        Material result = instance.find(id);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testCreate() throws Exception {
        System.out.println("create");
        Material entity = null;
        MultivaluedMap<String, String> parameters = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        Material expResult = null;
        Material result = instance.create(entity, parameters);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testUpdate() throws Exception {
        System.out.println("update");
        Material entity = null;
        MultivaluedMap<String, String> parameters = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        Material expResult = null;
        Material result = instance.update(entity, parameters);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testRemove() throws Exception {
        System.out.println("remove");
        long id = 0L;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        instance.remove(id);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of lazyLoad method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testLazyLoad() throws Exception {
        System.out.println("lazyLoad");
        Material entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        instance.lazyLoad(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of resolveDependencies method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testResolveDependencies() throws Exception {
        System.out.println("resolveDependencies");
        Material entity = null;
        MultivaluedMap<String, String> parameters = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        instance.resolveDependencies(entity, parameters);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCriteriaQuery method, of class MaterialDao.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetCriteriaQuery() throws Exception {
        System.out.println("getCriteriaQuery");
        MultivaluedMap<String, String> parameters = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        MaterialDao instance = (MaterialDao)container.getContext().lookup("java:global/classes/MaterialDao!br.com.altamira.data.dao.Dao");
        CriteriaQuery<Material> expResult = null;
        CriteriaQuery<Material> result = instance.getCriteriaQuery(parameters);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
