/*
 * Author: jianqing
 * Date: Nov 22, 2020
 * Description: This document is created for
 */
package com.classchatroom.model;

import cn.hutool.setting.Setting;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* @author Jianqing Gao
 */
public class ChatDatabase extends SqlDatabase
{

    public ChatDatabase(String dbName, String host, String dbUserName, String dbPassword)
    {
        super(dbName, host, dbUserName, dbPassword);
    }

    /**
     * Select the needed cridentials of an account by its token.
     *
     * @param token
     * @return
     * @throws SQLException
     */
    public Map<String, String> selectAccountsByToken(String token) throws SQLException
    {
        Map<String, String> list = new HashMap<>(1);
        PreparedStatement ps = getDbCon().prepareStatement("SELECT * FROM Accounts"
                + " WHERE Token=?");
        ps.setString(1, token);
        ResultSet rs = ps.executeQuery();
        final String[] COLUMNS_TO_SELECT =
        {
            "EmailUsername",
            "EmailDomain",
            "UserName",
            "School",
            "TimeZone",
            "Banned",
            "Token"
        };
        if (rs.next())
        {
            for (String col : COLUMNS_TO_SELECT)
            {
                list.put(col, rs.getString(col));
            }
        }
        return list;
    }

    /**
     * Select if an user is in a group.
     *
     * @param id
     * @param emailName
     * @param emailDomain
     * @return
     * @throws SQLException
     */
    public boolean isUserInGroup(int id, String emailName, String emailDomain) throws SQLException
    {
        String query = "SELECT * FROM GroupUserTable WHERE GroupID=? AND EmailUsername=? AND EmailDomain=?";
        PreparedStatement ps = getDbCon().prepareStatement(query);
        ResultSet rs;
        ps.setInt(1, id);
        ps.setString(2, emailName);
        ps.setString(3, emailDomain);
        rs = ps.executeQuery();
        return rs.next();
    }

    /**
     * Record a chat history
     *
     * @param time
     * @param emailUsername
     * @param domain
     * @param type
     * @param chatroomId
     * @param body
     * @return
     * @throws SQLException
     */
    public int insertIntoChatHistory(String time, String emailUsername, String domain, String type, String chatroomId, String body) throws SQLException
    {
        String query = "INSERT INTO ChatHistory VALUES(?,?,?,?,?,?)";
        PreparedStatement ps = getDbCon().prepareStatement(query);
        ps.setString(1, time);
        ps.setString(2, emailUsername);
        ps.setString(3, domain);
        ps.setInt(4, type.equals("text") ? 0 : 1);//0 text 1 image
        ps.setString(5, chatroomId);
        ps.setString(6, body);
        return ps.executeUpdate();
    }

//    public JsonArray selectFromChatHistory(int groupId, String emailUsername, String emailDomain, int limit) throws SQLException
//    {
//        String sql = "SELECT * FROM ChatHistory WHERE ChatroomID=? AND EmailDomain=? AND EmailUsername=? ORDER BY Time LIMIT "+ limit;
//        PreparedStatement ps = getDbCon().prepareStatement(sql);
//        ResultSet rs;
////        JsonArrayBuilder builder = Json.createArrayBuilder();
////        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
//        int userTimeZone = getUserTimezone(emailUsername, emailDomain);
//        ps.setInt(1, groupId);
//        ps.setString(2, emailDomain);
//        ps.setString(3, sql);
//        rs = ps.executeQuery();
//        while(rs.next())
//        {
//            jsonObjectBuilder.add("type", rs.getInt("Type")==0?"text":"image");
//            jsonObjectBuilder.add("time", TimeFormatConverter.parseLocalDateTime(rs.getString("Time")).plusHours(userTimeZone).format(TimeFormatConverter.DATETIME_FORMATTER));
//            jsonObjectBuilder.add("email", emailUsername);
//        }
//    }
    public ArrayList<ChatRecord> selectFromChatHistory(int groupId, int limit) throws SQLException
    {
        ArrayList<ChatRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM ChatHistory WHERE ChatroomID=? ORDER BY Time DESC LIMIT " + limit;
        PreparedStatement ps = getDbCon().prepareStatement(sql);
        ResultSet rs;
        ChatRecord record;
        ps.setInt(1, groupId);
//        ps.setInt(2, limit);

        rs = ps.executeQuery();
        while (rs.next())
        {
            record = new ChatRecord();
            record.setBody(rs.getString("Body"));
            record.setEmailDomain(rs.getString("EmailDomain"));
            record.setEmailUsername(rs.getString("EmailUsername"));
            record.setType(rs.getInt("Type"));
            record.setTime(rs.getString("Time"));
            list.add(record);
        }
        return list;
    }

    public int getUserTimezone(String emailUsername, String emailDomain) throws SQLException
    {
        String sql = "SELECT TimeZone FROM Account WHERE EmailUsername=? AND EmailDomain=?";
        PreparedStatement ps = getDbCon().prepareStatement(sql);
        ResultSet rs;
        ps.setString(1, emailUsername);
        ps.setString(2, emailDomain);
        rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("TimeZone");
        } else
        {
            return 0;
        }
    }

    public int getUnreadCount(int groupId, String ename, String domain) throws SQLException
    {
        String sql = "SELECT UnreadCount FROM GroupUserTable WHERE groupId=? AND EmailUsername=? AND EmailDomain=?";
        PreparedStatement ps = getDbCon().prepareStatement(sql);
        ps.setInt(1, groupId);
        ps.setString(2, ename);
        ps.setString(3, domain);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            return rs.getInt("UnreadCount");
        }
        return 0;
    }

    public void updateEveryoneBy1(int groupId) throws SQLException
    {
        String sql = "UPDATE GroupUserTable SET UnreadCount = UnreadCount + 1 WHERE GroupId=" + groupId;
        PreparedStatement ps = getDbCon().prepareStatement(sql);
        ps.executeUpdate();
    }

    public void markSomepeopleAsRead(int groupId, ArrayList<String[]> list) throws SQLException
    {
        String sql = "UPDATE GroupUserTable SET UnreadCount = UnreadCount - 1 WHERE GroupId=? AND EmailUsername=? AND EmailDomain=?";
        PreparedStatement ps = getDbCon().prepareStatement(sql);
        for (int i = 0; i < list.size(); i++)
        {
            String[] get = list.get(i);
            ps.setInt(1, groupId);
            ps.setString(2, get[0]);
            ps.setString(3, get[1]);
            ps.addBatch();
        }
        ps.executeBatch();
    }

    public void readAllMessages(int groupId, String ename, String domain) throws SQLException
    {
        String sql = "UPDATE GroupUserTable SET UnreadCount =0 WHERE GroupId=? AND EmailUsername=? AND EmailDomain=?";
        PreparedStatement ps = getDbCon().prepareStatement(sql);
        ps.setInt(1, groupId);
        ps.setString(2, ename);
        ps.setString(3, domain);
        ps.executeUpdate();
    }

    /**
     * Creating a default instance of chat database access.
     *
     * @return
     */
    public static ChatDatabase createDefaultInstance()
    {
        Setting setting = new Setting("db.setting");
        return new ChatDatabase(setting.get("dbName"), setting.get("host"), setting.get("user"), setting.get("pass"));
    }

    @Override
    public void close() throws SQLException
    {
        getDbCon().close();
    }

    public static void main(String[] args)
    {
        try (ChatDatabase db = createDefaultInstance())
        {
            //db.insertIntoChatHistory(DEFAULT_DBNAME, DEFAULT_USERNAME, DEFAULT_HOST, DEFAULT_HOST, DEFAULT_HOST, DEFAULT_HOST)
            db.updateEveryoneBy1(11111);
            System.out.println("Yay!");
        } catch (Exception e)
        {
            e.printStackTrace();
            OSSAccessor.logError(e);
        }
    }
}
