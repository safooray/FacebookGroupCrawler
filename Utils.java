/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facebookcrawl;

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *
 * @author Ayine
 */
public class Utils
{
    public static GregorianCalendar stringToDate(String ds)
    {
        int year = Integer.valueOf(ds.substring(0, 4));
        int month = Integer.valueOf(ds.substring(5, 7));
        int date = Integer.valueOf(ds.substring(8, 10));
        int hour = Integer.valueOf(ds.substring(11, 13));
        int minute = Integer.valueOf(ds.substring(14, 16));
        int second = Integer.valueOf(ds.substring(17, 19));
        GregorianCalendar gc = new GregorianCalendar();
        TimeZone tz = TimeZone.getTimeZone("GMT");
        gc.setTimeZone(tz);
        gc.set(year, month - 1, date, hour, minute, second);
        return gc;
        //return new (year - 1901, month, minute, hour, minute, second);    
    }
    public static long dateToLong(GregorianCalendar date)
    {
        return date.getTimeInMillis();
    }
    public static long msecToSec(long msec)
    {
        return msec/1000;
    }
}
