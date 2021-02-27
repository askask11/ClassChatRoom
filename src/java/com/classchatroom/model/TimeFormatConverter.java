/*
 * Author: jianqing
 * Date: Nov 23, 2020
 * Description: This document is created for
 */
package com.classchatroom.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Class to deal with date time issues.
 * @author Jianqing Gao
 */
public class TimeFormatConverter
{
    final static DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
    
    /**
     * Get UTC present time.
     * @return 
     */
    public static String getPresentFormattedDaytime()
    {
        return getPresentFormattedDaytime(0);
    }

    /**
     * get the present date time according to UTC time adjusted.
     * @param utc
     * @return 
     */
    public static String getPresentFormattedDaytime(long utc)
    {
        return LocalDateTime.now(ZoneId.of("UTC" + (utc > 0 ? "+" + utc : (utc == 0 ? "" : utc)))).format(DATETIME_FORMATTER);
    }
    
    /**
     * Get the smart local date time object. Format your String like YYYY-MM-dd HH:mm:ss.
     * @param datetime
     * @return 
     */
    public static LocalDateTime parseLocalDateTime(String datetime)
    {
        String yeardate[] = datetime.split(" ");
        String yyyymmdd[] = yeardate[0].split("-");
        String hhmmss[] = yeardate[1].split(":");
        return LocalDateTime.of(Integer.parseInt(yyyymmdd[0]), Integer.parseInt(yyyymmdd[1]), Integer.parseInt(yyyymmdd[2]), Integer.parseInt(hhmmss[0]), Integer.parseInt(hhmmss[1]), Integer.parseInt(hhmmss[2]));
    }
    
    public static void main(String[] args)
    {//2020-11-23 02:41:06
        String t = getPresentFormattedDaytime();
        System.out.println(t);
    }

}
