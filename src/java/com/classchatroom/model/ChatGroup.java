/**
 * This ChatGroup class stores all the information for the chat group.
 * This class only has constructors and get/set methods.
 * 
 */


package com.classchatroom.model;

/**
 *
 * @author seunggulee
 */

public class ChatGroup 
{
    private int groupId;
    private String domain;
    private String groupName;
    private String schoolName;
    private String groupDescription;
    private String hostUsername;
    
    private int unreadCount;
    private boolean hasUnreadNotifications;
    private int memberCount;
    
    public ChatGroup() {
    }

    public ChatGroup(int groupId, String domain, String groupName, String schoolName, String groupDescription, String hostUsername, boolean hasUnreadNotifications, int getMemberCount) {
        this.groupId = groupId;
        this.domain = domain;
        this.groupName = groupName;
        this.schoolName = schoolName;
        this.groupDescription = groupDescription;
        this.hostUsername = hostUsername;
        this.hasUnreadNotifications = hasUnreadNotifications;
        this.memberCount = getMemberCount;
    }
        
    

    public boolean isHasUnreadNotifications() {
        return hasUnreadNotifications;
    }

    
    
    public void setHasUnreadNotifications(boolean hasUnreadNotifications) {
        this.hasUnreadNotifications = hasUnreadNotifications;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getUnreadCount()
    {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount)
    {
        this.unreadCount = unreadCount;
        hasUnreadNotifications = unreadCount>0;
        
    }

    
    
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getHostUsername() {
        return hostUsername;
    }

    public void setHostUsername(String hostUsername) {
        this.hostUsername = hostUsername;
    }
    
}
