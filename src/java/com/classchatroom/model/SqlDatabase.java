/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.classchatroom.model;

import cn.hutool.setting.Setting;
import static com.classchatroom.model.OSSAccessor.logError;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author lequanghuy
 */
public class SqlDatabase implements AutoCloseable
{
//    private String dbName;
    private Connection dbCon;
    
    
    public SqlDatabase(){
//        dbName = "";
        dbCon = null;
        
    }
    
    public SqlDatabase(String dbName, String host, String dbUserName, String dbPassword){
        this.setDbCon(dbName, host, dbUserName, dbPassword);
    }
    
    public static SqlDatabase createDefaultInstance(){
        Setting setting = new Setting("db.setting");
        return new SqlDatabase(setting.get("dbName"), setting.get("host"), setting.get("user"), setting.get("pass"));
    }
//    
//    public SqlDatabase(String dbName){
//        setDbName(dbName);
//        setDbCon();
//    }
//    
//    public void setDbName(String dbName)
//    {
//        this.dbName = dbName;
//    }
    
    public void setDbCon(String dbName, String host, String dbUserName, String dbPassword)
    {
        String connectionURL = "jdbc:mysql://" +host+"/"+dbName;
        this.dbCon = null;
        //Find the driver and make connection 
        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Properties properties = new Properties();
            properties.setProperty("user", dbUserName);
            properties.setProperty("password", dbPassword);
            properties.setProperty("useSSL",Boolean.toString(false));
            
            this.dbCon = DriverManager.getConnection(connectionURL, properties);
        }
        catch (ClassNotFoundException cnfe)
        {
            System.out.println("Class for name not found");
            cnfe.printStackTrace(System.err);
            logError(cnfe);
        }catch (SQLException se)
        {
            System.out.println("SQL connection error");
            se.printStackTrace(System.err);logError(se);
        }
    }
    
//    public String getDbName()
//    {
//        return this.dbName;
//    }
    
    public Connection getDbCon()
    {
        return this.dbCon;
    }
    
    //method where user register
    //Return boolean if fail(false) or success (true)
    public boolean register(String emailUserName, String EmailDomain, String EncryptedPassword,
            String UserName, String school, int graduationYear, int notifSetting,
            String latestNotifSent, long timeZone, boolean Banned, String token){
        
        
        Statement s = null;
        ResultSet rs = null;
        boolean accountExist;
        boolean success = false;
        //Insert to table query
        String insertAccountQuery = "INSERT INTO Accounts VALUES (?, ?,"
                +"?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
        try(PreparedStatement ps = dbCon.prepareStatement(insertAccountQuery)){
            accountExist = this.accountExist(emailUserName, EmailDomain);
            
            //Check if account exist 
            //If no then insert
            if(accountExist == false){
                ps.setString(1, emailUserName);
                ps.setString(2, EmailDomain);
                ps.setString(3, EncryptedPassword);
                ps.setString(4, UserName);
                ps.setString(5, school);
                ps.setInt(6, graduationYear);
                ps.setInt(7, notifSetting);
                ps.setString(8, latestNotifSent);
                ps.setLong(9, timeZone);
                ps.setBoolean(10, Banned);
                ps.setString(11, token);
                ps.executeUpdate();
                success = true;
                System.out.println(success);
                return success;
            }
            
            //If yes then warn user
            System.out.println(success);
            
        }
        catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);
            logError(e);
        }
        return success;
    }
    
    
    //Check if account already existed
    //Return true if account exist or else false
    public boolean accountExist(String emailUserName, String domain){
        ResultSet rs = null;
        boolean accountExist = false;
        String counter;
        //This query to check if the account is already existed
        String checkAccountQuery = "SELECT COUNT(*) FROM Accounts WHERE emailUserName = ?"
                +" AND EmailDomain = ?";
        try(PreparedStatement ps = dbCon.prepareStatement(checkAccountQuery)){
            ps.setString(1, emailUserName);
            ps.setString(2, domain);
            rs = ps.executeQuery();
            rs.next();
            counter = rs.getString(1);
            
            System.out.println(counter);
            if(counter.equals("1")){
                accountExist = true;
            }
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        
        return accountExist;        
    }
    
    //Sign In method 
    //Return true if signIn success, false if failed
    public boolean logIn(String emailUserName, String domain, String encryptedPassword){
        ResultSet rs = null;
        boolean success = false;
        String counter;
        //Query for log in 
        String logInQuery = "SELECT COUNT(*) FROM Accounts WHERE emailUserName = ?"
                +" AND EmailDomain = ? AND EncryptedPassword = ?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(logInQuery)){
            ps.setString(1, emailUserName);
            ps.setString(2, domain);
            ps.setString(3, encryptedPassword);
            rs = ps.executeQuery();
            rs.next();
            counter = rs.getString(1);
            
            System.out.println(counter);
            //Check if it existed already to log in 
            if(counter.equals("1")){
                success = true;
                return success;
            }
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        return success;
    }
    
    //Return user's token but return "fail" if method fail to find
    public String getUserToken(String emailUserName, String domain){
        ResultSet rs = null;
        String token = "fail";
        String getTokenQuery = "SELECT Token FROM Accounts WHERE emailUserName = ?"
                +" AND EmailDomain = ?";
        
        //Preparestatement to get user token
        try(PreparedStatement ps = dbCon.prepareStatement(getTokenQuery)){
            ps.setString(1, emailUserName);
            ps.setString(2, domain);
            rs = ps.executeQuery();
            rs.next();
            token = rs.getString(1);
            return token;
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        
        return token;
    }
    
    //Get Username of user
    //Return Username if success, or else return "fail"
    public String getUserName(String emailUserName, String domain){
        ResultSet rs = null;
        String userName = "fail";
        String getTokenQuery = "SELECT UserName FROM Accounts WHERE emailUserName = ?"
                +" AND EmailDomain = ?";
        
        //Preparestatement to get user token
        try(PreparedStatement ps = dbCon.prepareStatement(getTokenQuery)){
            ps.setString(1, emailUserName);
            ps.setString(2, domain);
            rs = ps.executeQuery();
            rs.next();
            userName = rs.getString(1);
            return userName;
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        
        return userName;
    }
    
    //Update user token --> change the old one to new one
    //Return true if success, return false if fail 
    public boolean updateToken(String emailUserName, String domain, String token){
        ResultSet rs = null;
        boolean success = false;
        String updateTokenQuery = "UPDATE Accounts SET Token = ? WHERE EmailUsername = ? AND EmailDomain = ? ";
        
        try(PreparedStatement ps = dbCon.prepareStatement(updateTokenQuery)){
            ps.setString(1, token);
            ps.setString(2, emailUserName);
            ps.setString(3, domain);
            ps.executeUpdate();
            success = true;
            return success;
            
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        
        return success;
    }
    
    //Get group ids that user have joined in arraylist
    //Return arraylist of integers of group ids
    public ArrayList<ChatGroup> getUserJoinedGroups(String[] email)
    {
        ResultSet rs = null;
        
        // 1. Finding Group Ids
        ArrayList<Integer> groupIds = new ArrayList<>();
        String getGroupIdQuery = "SELECT GroupID FROM GroupUserTable WHERE "
                + "EmailUsername=? AND EmailDomain=?";
            
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupIdQuery))
        {
            ps.setString(1, email[0]);
            ps.setString(2, email[1]);
            rs = ps.executeQuery();
            
            while(rs.next())
            {
                 groupIds.add(rs.getInt("GroupID"));
            }
        } catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);
            logError(e);
        }
        
        // 2. With Group Ids, add group to arraylist
        ArrayList<ChatGroup> groups = new ArrayList<>();
        
        for(int i=0; i<groupIds.size(); i++) {
            groups.add(getGroupObject(groupIds.get(i), email));
        }
        
        
        // Return
        return groups;
    }
    
    //Get group ids of the school in arraylist
    //Return arraylist of integers of group ids
    public ArrayList<ChatGroup> getGroupsInOrganization(String userName, String domain) throws SQLException
    {
        ResultSet rs = null;
        ResultSet rs2 = null;
        
        // 1. Finding Group Ids
        ArrayList<Integer> newGroup = new ArrayList<>();
        ArrayList<Integer> groupId = new ArrayList<>();
        ArrayList<Integer> groupUserId = new ArrayList<>();
        String getGroupIdQuery = "SELECT GroupID FROM Groups WHERE SchoolDomain = ?";
        String checkGroupQuery = "SELECT GroupID FROM GroupUserTable WHERE EmailUsername=? AND EmailDomain=?";
            
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupIdQuery);PreparedStatement ps2 = dbCon.prepareStatement(checkGroupQuery))
        {
            ps.setString(1, domain);
            rs = ps.executeQuery();
            ps2.setString(1, userName);
            ps2.setString(2, domain);
            rs2 = ps2.executeQuery();
            
            //Check if the user already in the group or not
            while(rs.next())
            {
//                System.out.println(rs.getInt(1));
                groupId.add(rs.getInt(1));
                
            }
            
            while(rs2.next()){
//                System.out.println(rs2.getInt(1));
                groupUserId.add(rs2.getInt(1));
                
            }
            
            for(int i = 0; i < groupId.size(); i++){
                int groupNo = groupId.get(i);
                int counter = 0;
                for(int j = 0; j<groupUserId.size(); j++){
                    int userNo = groupUserId.get(j);
//                    System.out.println(groupNo+" and "+userNo);
                    
                    if(groupNo == userNo){
                        counter++;
//                        System.out.println(counter);
                    }
                }
                //Add the group that user haven't join only
                if(counter == 0){
                    System.out.println(groupId.get(i));
                    newGroup.add(groupId.get(i));
                }
                
            }
            
        }
        
        // 2. With Group Ids, add group to arraylist
        ArrayList<ChatGroup> groups = new ArrayList<>();
        
        for(int i=0; i<newGroup.size(); i++) {
//            System.out.println(getGroupObject(newGroup.get(i)));
            groups.add(getGroupObject(newGroup.get(i)));
        }
        
        
        // Return
        return groups;
    }
     
    
    
    /**
     * Returns ChatGroup object with a group ID
     * @param groupId integer group ID
     * @return ChatGroup object
     */
    public ChatGroup getGroupObject(int groupId, String[] email)
    {
        ResultSet rs = null;
        ChatGroup cg = new ChatGroup();
        
        
        String getGroupIdQuery = "SELECT * FROM Groups WHERE GroupID=?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupIdQuery))
        {
            ps.setInt(1, groupId);
            rs = ps.executeQuery();
            
            while(rs.next())
            {
                cg.setGroupId(groupId);
                cg.setDomain(rs.getString("SchoolDomain"));
                cg.setGroupName(rs.getString("GroupName"));
                cg.setSchoolName(rs.getString("SchoolName"));
                cg.setGroupDescription(rs.getString("GroupDescription"));
                cg.setHostUsername(rs.getString("HostUsername"));
                cg.setUnreadCount(userHasUnreadNotif(groupId,email[0], email[1]));
         
                cg.setMemberCount(countMembers(groupId));
            }
        } catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);
            logError(e);
        }
        
        return cg;
    }
    
    /**
     * Returns ChatGroup object with groupID
     * Overloaded method, email not necessary and hasUnreadNotifications is false
     * @param groupId
     * @return 
     */
    public ChatGroup getGroupObject(int groupId)
    {
        ResultSet rs = null;
        ChatGroup cg = new ChatGroup();
        
        
        String getGroupIdQuery = "SELECT * FROM Groups WHERE GroupID=?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupIdQuery))
        {
            ps.setInt(1, groupId);
            rs = ps.executeQuery();
            
            while(rs.next())
            {
                cg.setGroupId(groupId);
                cg.setDomain(rs.getString("SchoolDomain"));
                cg.setGroupName(rs.getString("GroupName"));
                cg.setSchoolName(rs.getString("SchoolName"));
                cg.setGroupDescription(rs.getString("GroupDescription"));
                cg.setHostUsername(rs.getString("HostUsername"));
                cg.setHasUnreadNotifications(false);
                cg.setMemberCount(countMembers(groupId));
            }
        } catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);
            logError(e);
        }
        
        return cg;
    }
    
    // TODO implement
    public int userHasUnreadNotif(int groupId, String userUsername, String emailDomain) throws SQLException
    {

        PreparedStatement ps = dbCon.prepareStatement("SELECT UnreadCount FROM GroupUserTable WHERE GroupId=? AND EmailUsername=? AND EmailDomain=?");
        ps.setInt(1, groupId);
        ps.setString(2, userUsername);
        ps.setString(3, emailDomain);
        ResultSet rs = ps.executeQuery();
        if(rs.next())
        {
            return rs.getInt("UnreadCount");
        }
        return 0;
    }
    
    /**
     * Returns how many participants there are in the group
     * @param groupId
     * @return 
     */
    public int countMembers(int groupId) throws SQLException
    {
        int memberCount = 0;
        ResultSet rs = null;
        String getGroupIdQuery = "SELECT COUNT(*) FROM Groups WHERE GroupID=?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupIdQuery))
        {
            ps.setInt(1, groupId);
            rs = ps.executeQuery();
            rs.next();
            memberCount = Integer.parseInt( rs.getString(1));
            return memberCount;
        }
        
    }
    
    
     //Get group description  in arraylist
    //Return arraylist of String of group description
    public ArrayList<ChatGroup> getGroupsDescriptionInOrganization(String domain){
        ResultSet rs = null;
        ArrayList<ChatGroup> groups = new ArrayList<>();
        ChatGroup chatObj = new ChatGroup();
        String getGroupDescriptionQuery = "SELECT GroupDescription FROM Groups WHERE SchoolDomain = ?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupDescriptionQuery)){
            ps.setString(1, domain);
            rs = ps.executeQuery();
            while(rs.next()){
                chatObj.setGroupDescription(rs.getString(1));
                chatObj.setDomain(domain);
                System.out.println(rs.getString(1));
                groups.add(chatObj);
            }
            
            return groups;
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        
        return groups;
    }
    
    //Get group name in arraylist
    //Return arraylist of String of group names
    public ArrayList<ChatGroup> getGroupsNameInOrganization(String domain){
        ResultSet rs = null;
        ArrayList<ChatGroup> groups = new ArrayList<>();
        ChatGroup chatObj = new ChatGroup();
        String getGroupNameQuery = "SELECT GroupName FROM Groups WHERE SchoolDomain = ?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupNameQuery)){
            ps.setString(1, domain);
            rs = ps.executeQuery();
            while(rs.next()){
                chatObj.setDomain(domain);
                chatObj.setGroupName(rs.getString(1));
                groups.add(chatObj);
            }
            return groups;
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        
        return groups;
    }
    
    //Check if group Id existed
    //Return true if exist, false if not
    public boolean groupIdExists(int groupId){
        ResultSet rs = null;
        boolean success = false;
        String counter;
        String getGroupIDQuery = "SELECT COUNT(*) FROM Groups WHERE GroupId = ?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(getGroupIDQuery)){
           ps.setInt(1, groupId);
           rs = ps.executeQuery();
           rs.next();
           counter = rs.getString(1);
           if(counter.equals("1")){
               success = true;
               return success;
           }
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        return success;
    }
    
    //Method create new group for organization
    //Return true if create success, false if not
    public boolean createGroup(int groupId, String schoolDomain, String groupName, String schoolName, String groupDescription, String hostUserName){
        boolean success = false;
        String createGroupQuery = "INSERT INTO Groups VALUES (?,?,?,?,?,?)";
        
        try(PreparedStatement ps = dbCon.prepareStatement(createGroupQuery)){
           ps.setInt(1, groupId);
           ps.setString(2, schoolDomain);
           ps.setString(3, groupName);
           ps.setString(4, schoolName);
           ps.setString(5, groupDescription);
           ps.setString(6, hostUserName);
           ps.executeUpdate();
           return success = true;
            
        }catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
        }
        return success;
    }
    
    //Add User to table
    //Return true if success, false if fail
    public boolean addUserToGroup(int groupId, String emailUserName, String domain) throws SQLException{
        boolean success = false;
        String createGroupQuery = "INSERT INTO GroupUserTable VALUES (?,?,?,?)";
        
        //Check if user is in correct domain
        if(checkUserDomain(groupId, domain) == false){
            return success;
        }
        else{
            try(PreparedStatement ps = dbCon.prepareStatement(createGroupQuery)){
               ps.setInt(1, groupId);
               ps.setString(2, emailUserName);
               ps.setString(3, domain);
               ps.setInt(4, 0);
               ps.executeUpdate();
               success = true;
               return success;

            }
            catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
            }
        }
        
        return success;
    }
    
    //Check if user so in the correct organization
    public boolean checkUserDomain(int groupId, String domain){
        boolean success = false;
        ResultSet rs = null;
        String createGroupQuery = "SELECT SchoolDomain FROM Groups WHERE groupID = ?";
        
        try(PreparedStatement ps = dbCon.prepareStatement(createGroupQuery)){
           ps.setInt(1, groupId);
           rs = ps.executeQuery();
           rs.next();
           
           if(rs.getString(1).equals(domain)){
               success = true;
               return success;
           }
           
           return success;
        }
        catch(SQLException e){
            System.out.println("SQL Eror: Not able to get data");
            e.printStackTrace(System.err);logError(e);
            }
        
        return success;
    }
    
    public void updatePassword(String emailUserName, String domain, String newPassword) throws SQLException{
        String updateQuery = "UPDATE Accounts SET EncryptedPassword =? WHERE EmailUsername =? AND EmailDomain = ? ";
        
        try(PreparedStatement ps = dbCon.prepareStatement(updateQuery)){
           ps.setString(1, newPassword);
           ps.setString(2, emailUserName);
           ps.setString(3, domain);
           ps.executeUpdate();            
        }
    }
    
    
    public void updateName(String emailUserName, String domain, String newName) throws SQLException{
        String updateQuery = "UPDATE Accounts SET UserName =? WHERE EmailUsername =? AND EmailDomain = ? ";
        
        try(PreparedStatement ps = dbCon.prepareStatement(updateQuery)){
           ps.setString(1, newName);
           ps.setString(2, emailUserName);
           ps.setString(3, domain);
           ps.executeUpdate();            
        }
    }
    
    
    public static void main(String[] args) throws SQLException{
        SqlDatabase obj;
        obj = SqlDatabase.createDefaultInstance();
        //boolean message;
        //int messageInt;
//        boolean exist = obj.accountExist("hello", "yoyo");
//        System.out.println(exist);
//        Boolean message = obj.register("quanghuy", "test", "sdadajds", "adjajd", "jiojdosjf", 0, 0, "notif", 0, true, "token");
//        Boolean message = obj.logIn("quanghuy", "test", "sdadajds");
//        String message = obj.getUserToken("quanghuy", "test");
//        boolean message = obj.updateToken("quanghuy", "test", "tako");
//        ArrayList<ChatGroup> groupIds;
//        groupIds = obj.getGroupsInOrganization("quanghuy", "test");
//        for(int i =0; i < groupIds.size(); i++){
//            System.out.println(groupIds.get(i).getDomain());
//            System.out.println(groupIds.get(i).getGroupName());
        
//        String message = obj.getUserName("quanghuy", "test");
//        boolean message= obj.groupIdExists(1110);
//        message = obj.createGroup(0, "test", "seung gu lee", "village", "This group is nothing", "NetherYoshi");
//        message = obj.addUserToGroup(11111, "seung gu", "seunggu.com",4);
//        messageInt = obj.countMembers(11111);
//        obj.updateName("quanghuy", "test", "blablalba");
        //message = obj.addUserToGroup(11111, "quangle", "blablabla");
        //System.out.println(message);
        System.out.println("Database to db Connected!");
    }

    @Override
    public void close() throws Exception
    {
    this.dbCon.close();    
        
    }

}
