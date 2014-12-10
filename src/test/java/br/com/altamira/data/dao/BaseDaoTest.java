/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao;

import br.com.altamira.data.dao.util.ResourcesFactory;
import br.com.altamira.data.model.Entity;
import br.com.altamira.data.model.serialize.JSonViews;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 *
 * @author Alessandro
 * @param <T>
 */
@RunWith(Arquillian.class)
public abstract class BaseDaoTest<T extends br.com.altamira.data.model.Entity> {
    
    /**
     *
     * @return
     */
    //@Deployment
    public static JavaArchive createDeployment() {
        // resolve given dependencies from Maven POM
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").importRuntimeAndTestDependencies()
                .resolve().withTransitivity().asFile();

        JavaArchive jar = ShrinkWrap
                //.create(WebArchive.class, "ColorDaoTest" + ".war")
                .create(JavaArchive.class, "ColorDaoTest.jar")
                .addPackage(ResourcesFactory.class.getPackage())
                .addPackage(JSonViews.class.getPackage())
                .addPackage(Entity.class.getPackage())
                .addClasses(Dao.class, BaseDao.class, BaseDao.class)
                //.addAsLibraries(libs)
                //.addAsWebResource("WEB-INF/beans.xml", "beans.xml")
                //.addAsWebInfResource("WEB-INF/beans.xml", "beans.xml")
                //.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                //.addAsWebInfResource("META-INF/jboss-all.xml", "jboss-all.xml")
                //.addAsWebResource("log4j.xml", "log4j.xml")
                .addAsManifestResource("META-INF/test-persistence.xml", "META-INF/persistence.xml");

        System.out.print(jar.toString(Formatters.VERBOSE));

        return jar;
    }
    
    @Inject
    Logger log;

    @Inject
    protected BaseDao<T> dao;
    
    protected T entity;
    
    MultivaluedMap<String, String> parameters = null;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of list method, of class BaseDao.
     */
    //@Test
    public void listTest() {
        System.out.println("list");
        
        int startPage = 0;
        int pageSize = 10;
        
        List<T> list = dao.list(this.parameters, startPage, pageSize);
        
        assertNotNull(list);
        assertFalse(list.isEmpty());
    }

    /**
     * Test of find method, of class BaseDao.
     */
    //@Test
    public void findTest() {
        System.out.println("find");
        
        T found = dao.find(entity.getId());
        
        assertNotNull(found.getId());
        assertEquals(entity.getId(), found.getId());
    }

    /**
     * Test of create method, of class BaseDao.
     */
    //@Test
    public void createTest() {
        System.out.println("create");
        
        T created = dao.create(entity, this.parameters);
        
        assertEquals(entity.getId(), created.getId());
    }

    /**
     * Test of update method, of class BaseDao.
     */
    //@Test
    public void testUpdate() {
        System.out.println("update");
        
        T result = dao.update(entity, this.parameters);
        
        assertEquals(entity.getId(), result.getId());
    }

    /**
     * Test of remove method, of class BaseDao.
     */
    //@Test
    public void testRemove() {
        System.out.println("remove");
        
        dao.remove(entity.getId());
    }

}
