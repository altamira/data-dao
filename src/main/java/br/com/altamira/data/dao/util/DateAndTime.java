/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.altamira.data.dao.util;

//import java.util.Date;

/**
 *
 * @author Alessandro
 */
public class DateAndTime {

    // ALTAMIRA-92: replaced by java.sql.Date
    /*public static Date stripTimePortion(Date timestamp) {
        long msInDay = 1000 * 60 * 60 * 24; // Number of milliseconds in a day
        long msPortion = timestamp.getTime() % msInDay;
        return new Date(timestamp.getTime() - msPortion);
    }*/
}
