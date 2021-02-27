/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.classchatroom.model;

/**
 *
 * @author lequanghuy
 */
public class GroupUser
{
    private int groupId;
    private String emailDomain;
    private String emailUsername;
    private int unreadCount;

    public GroupUser()
    {
    }

    public GroupUser(int groupId, String emailDomain, String emailUsername, int unreadCount)
    {
        this.groupId = groupId;
        this.emailDomain = emailDomain;
        this.emailUsername = emailUsername;
        this.unreadCount = unreadCount;
    }
    
    
    public void setEmailDomain(String emailDomain)
    {
        this.emailDomain = emailDomain;
    }

    public void setEmailUsername(String emailUsername)
    {
        this.emailUsername = emailUsername;
    }   

    public void setGroupId(int groupId)
    {
        this.groupId = groupId;
    }

    public void setUnreadCount(int unreadCount)
    {
        this.unreadCount = unreadCount;
    }

    public int getUnreadCount()
    {
        return unreadCount;
    }
    

    public int getGroupId()
    {
        return groupId;
    }

    public String getEmailDomain()
    {
        return emailDomain;
    }

    public String getEmailUsername()
    {
        return emailUsername;
    }
    
    
    
}
