/**
 * This class is used to initialize the main screen for the users.
 * Call the constructor with proper parameters to call this.
 * 
 */

package com.classchatroom.model;

import static com.classchatroom.model.OSSAccessor.logError;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




/**
 *
 * @author seunggulee
 */

public class InitializeMainScreen 
{
    public static void initialize(String[] splittedEmail, HttpSession session, SqlDatabase dbObj)
    {
        ArrayList<ChatGroup> userJoinedGroups = dbObj.getUserJoinedGroups(splittedEmail);
        
        session.setAttribute("joinedGroups", userJoinedGroups);
        
        
        try {
            ArrayList<ChatGroup> groupsInOrganization;
            groupsInOrganization = dbObj.getGroupsInOrganization(splittedEmail[0], splittedEmail[1]);
            session.setAttribute("organizationGroups", groupsInOrganization);
            
            System.out.println("OrganizationGroups length " + groupsInOrganization.size());
        }
        catch(SQLException sqle) { sqle.printStackTrace(); logError(sqle);}
    }
}
