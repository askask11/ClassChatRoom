/*
 * Author: jianqing
 * Date: Nov 23, 2020
 * Description: This document is created for
 */
package com.classchatroom.model;

/**
 *
 * @author jianqing
 */
public class ChatRecord
{
    private String type;
    private String body;
    private String userName;
    private String emailUsername;
    private String emailDomain;
    private String time;
    //private LocalDateTime dateTime;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
    
    public void setType(int type)
    {
        this.type = type==0?"text":"image";
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getEmailUsername()
    {
        return emailUsername;
    }

    public void setEmailUsername(String emailUsername)
    {
        this.emailUsername = emailUsername;
    }

    public String getEmailDomain()
    {
        return emailDomain;
    }

    public void setEmailDomain(String emailDomain)
    {
        this.emailDomain = emailDomain;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }
    
    public void adjustTimeZone(long utc)
    {
       TimeFormatConverter.parseLocalDateTime(time).plusHours(utc).format(TimeFormatConverter.DATETIME_FORMATTER);
    }
    
}
