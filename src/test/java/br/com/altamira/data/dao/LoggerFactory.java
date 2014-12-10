/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao;

import com.google.inject.spi.InjectionPoint;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

/**
 *
 * @author Alessandro
 */
@Dependent
public class LoggerFactory {

    /**
     *
     * @param injectionPoint
     * @return
     */
    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        String resourcePath = injectionPoint.getMember().getDeclaringClass().getName();
        return Logger.getLogger(resourcePath);
    }
}
