/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.common;

import br.com.altamira.data.dao.BaseDaoTest;
import br.com.altamira.data.model.common.Component;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 *
 * @author Alessandro
 */
@RunWith(Arquillian.class)
public class ComponentDaoTest extends BaseDaoTest<Component> {
    
    /**
     *
     * @return
     */
//    @Deployment
//    public static WebArchive createDeployment() {
//
//        WebArchive war = BaseDaoTest.createDeployment()
//                .addPackage(Component.class.getPackage())
//                .addPackage(ComponentDao.class.getPackage());
//
//        System.out.print(war.toString(Formatters.VERBOSE));
//
//        return war;
//    }

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
    }
    
    /**
     *
     */
    @After
    @Override
    public void tearDown() {
    }

    
}
