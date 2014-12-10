/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDao;
import br.com.altamira.data.dao.BaseDaoTest;
import br.com.altamira.data.dao.Dao;
import br.com.altamira.data.dao.util.ResourcesFactory;
import br.com.altamira.data.model.Entity;
import br.com.altamira.data.model.common.Color;
import br.com.altamira.data.model.serialize.JSonViews;
import java.io.File;
import java.util.logging.Logger;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Alessandro
 */
@RunWith(Arquillian.class)
public class ColorDaoTest extends BaseDaoTest<br.com.altamira.data.model.common.Color> {

    /**
     *
     * @return
     */
    @Deployment
    public static JavaArchive createDeployment() {
        // resolve given dependencies from Maven POM
        File[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml").importRuntimeAndTestDependencies()
                .resolve().withTransitivity().asFile();

        JavaArchive jar
                = ShrinkWrap
                //                BaseDaoTest.createDeployment()
                //                .create(WebArchive.class, "ColorDaoTest" + ".war")
                .create(JavaArchive.class, "ColorDaoTest.jar")
                .addPackage(ResourcesFactory.class.getPackage())
                .addPackage(JSonViews.class.getPackage())
                .addPackage(Entity.class.getPackage())
                .addClass(BaseDaoTest.class)
                .addClasses(Dao.class, BaseDao.class, BaseDao.class)
                //                .addPackage(ResourcesFactory.class.getPackage())
                //                .addPackage(JSonViews.class.getPackage())
                //                .addPackage(Entity.class.getPackage())
                //                .addClasses(Dao.class, BaseDaoBean.class)
                .addClasses(Color.class, ColorDao.class)
                //                .addAsLibraries(libs)
                //                .addAsWebResource("WEB-INF/beans.xml", "beans.xml")
                //                .addAsWebInfResource("WEB-INF/beans.xml", "beans.xml")
                //                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                //                .addAsWebInfResource("META-INF/jboss-all.xml", "jboss-all.xml")
                //                .addAsWebResource("log4j.xml", "log4j.xml")
                .addAsManifestResource("META-INF/test-persistence.xml", "persistence.xml");

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
    @Override
    public void setUp() {
        this.entity = getColor();
    }

    /**
     *
     */
    @After
    @Override
    public void tearDown() {
    }

    @Produces
    public Color getColor() {
        Color color = new Color();
        color.setCode("XXXXXX");
        color.setName("XXXXXXXXXXX");
        return color;
    }

    /**
     * Test of lazyLoad method, of class ColorDao.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreated() throws Exception {
        Color color = new Color();

        color.setCode("xxxxx");
        color.setName("ZXCZXCZC");

        MultivaluedMap<String, String> map = new MultivaluedHashMap<>();

        Color entity = dao.create(color, map);// <--- Nao esta injetando o DAO

        assertNotNull(entity.getId());

        //assertEquals("Expected color id: " + expected.getId() + " " + expected.getName() + ", but found " + entity.getId() + " " + entity.getName() + ".", expected, entity);
    }

}
