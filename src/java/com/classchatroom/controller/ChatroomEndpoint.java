/*
 * Author: jianqing
 * Date: Nov 20, 2020
 * Description: This document is created for the Websocket Endpoint of Chat rooms.
 */
package com.classchatroom.controller;

import com.classchatroom.model.ChatDatabase;
import com.classchatroom.model.Randomizer;
import static com.classchatroom.model.TimeFormatConverter.getPresentFormattedDaytime;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import static com.classchatroom.model.OSSAccessor.logError;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This is the websocket endpoint of chat room.
 *
 * @author Jianqing Gao
 */
@ServerEndpoint("/Chat")
public class ChatroomEndpoint
{

    //A collection of online users
    static Set<Session> chatroomUsers = Collections.synchronizedSet(new HashSet<Session>());

    static final String RETURNTYPE_ERROR = "error";
    static final String RETURNTYPE_TEXT = "text";
    static final String RETURNTYPE_IMG = "image";
    static final String RETURNTYPE_CONNECTED = "connected";

    static final String RECEIVETYPE_VERIFY = "verify";
    static final String RECEIVETYPE_TEXT = "text";
    static final String RECEIVETYPE_IMAGE = "image";

    final private boolean BLOCK_CROSSCHAT = false;

    @OnOpen
    public void onOpen(Session userSession)
    {
        chatroomUsers.add(userSession);
    }

    /**
     * This method will be executed each time the server received a message from
     * user.
     *
     * @param message The message text being send. Usually a JSON script.
     * @param userSession The usersession which init this connection.
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String message, Session userSession) throws IOException
    {
        //get if user is verified
        //JsonObjectToString(JsonValue.EMPTY_JSON_OBJECT).
        try
        {
            //parse json message
            JSONParser parser = new JSONParser();
            JSONObject rootJSON = (JSONObject) parser.parse(message);
            String type = (String) rootJSON.get("type");
            String chatroomId = (String) rootJSON.get("chatroomid");
            String body = (String) rootJSON.get("body");

            //verify user conenction
            switch (type)
            {
                case RECEIVETYPE_VERIFY:
                    //do a sql statement run check user token match
                    //TEST CODE!!!!!!
                    try (ChatDatabase db = ChatDatabase.createDefaultInstance())
                    {
                        Map<String, String> map = db.selectAccountsByToken(body);

                        if (map.size() > 0)
                        {
                            //check if user has joined this group. If not, deny access.
                            if (db.isUserInGroup(Integer.parseInt(chatroomId), map.get("EmailUsername"), map.get("EmailDomain")))
                            {
                                String username = map.get("UserName");
                                userSession.getUserProperties().put("username", username);
                                //userSession.getUserProperties().put("EmailUsername", map.get("EmailUsername"));
                                userSession.getUserProperties().put("chatroomid", chatroomId);
                                userSession.getUserProperties().put("emailusername", map.get("EmailUsername"));
                                userSession.getUserProperties().put("domain", map.get("EmailDomain"));
                                userSession.getUserProperties().put("verified", "true");
                                userSession.getUserProperties().put("timezone", map.get("TimeZone"));
                                userSession.getUserProperties().put("color", "#000000");
                                userSession.getUserProperties().put("avatar", "https://xeduocdn.sirv.com/Images/DefaultAvatars/DefaultAvatar" + Randomizer.randomInt(1, 24) + ".png");//random avatar
                                userSession.getBasicRemote().sendText(bulidJsonSuccessMessage(username));
                            } else
                            {
                                //user not in group, error
                                userSession.getBasicRemote().sendText(bulidJsonErrorMessage("It looks like you did not join this group, or is kicked out by the owner. Please try to join this group, group id: " + chatroomId));
                            }
                        } else
                        {
                            //User token does not exist.
                            userSession.getBasicRemote().sendText(bulidJsonErrorMessage("Invalid user token, please login again!"));
                            userSession.close();
                        }
                } catch (SQLException sqle)
                {
                    userSession.getBasicRemote().sendText(bulidJsonErrorMessage("Sorry, there is a internal database error."));
                    logError(sqle);
                } catch (NumberFormatException nfe)
                {
                    userSession.getBasicRemote().sendText(bulidJsonErrorMessage("Please enter a valid join id!"));
                    logError(nfe);
                }

                break;

                case RECEIVETYPE_TEXT:
                case RECEIVETYPE_IMAGE:
                    String verified = (String) userSession.getUserProperties().get("verified");
                    if ("true".equals(verified))
                    {
                        String initatorUsername = (String) userSession.getUserProperties().get("username");
                        String initatorEmailUsername = (String) userSession.getUserProperties().get("emailusername");
                        String initatorDomain = (String) userSession.getUserProperties().get("domain");
                        String initatorColor = (String) userSession.getUserProperties().get("color");
                        String initatorAvatar = (String) userSession.getUserProperties().get("avatar");
                        Iterator<Session> iterator = chatroomUsers.iterator();
                        String currentId;//the chatroom id of the user session that is being iterated.
                        try (ChatDatabase db = ChatDatabase.createDefaultInstance())
                        {
                            while (iterator.hasNext())
                            {
                                //iterate all users that is currently on chat
                                Session user = iterator.next();
                                //get the chatroom id of that user
                                currentId = (String) user.getUserProperties().get("chatroomid");
                                //target id matches
                                if (currentId.equals(chatroomId))
                                {
                                    //adapt timezone to clients (format timezone of remote clients)
                                    String time = getPresentFormattedDaytime(Long.parseLong(user.getUserProperties().get("timezone").toString()));

                                    user.getBasicRemote().sendText(bulidJsonMessage(type, initatorUsername, time, body, initatorColor, initatorAvatar, initatorEmailUsername, initatorDomain));
                                    //then insert it
                                    try//try inside try so that it will continue if one failed.
                                    {
                                        db.insertIntoChatHistory(getPresentFormattedDaytime(), initatorEmailUsername, initatorDomain, type, chatroomId, body);
                                        //get all participants of this chatroom
                                        ArrayList<String[]> participants = getAllParticipantsOfChatroom(chatroomId);
                                        db.updateEveryoneBy1(Integer.parseInt(chatroomId));
                                        db.markSomepeopleAsRead(Integer.parseInt(chatroomId), participants);

                                    } catch (SQLException e)
                                    {
                                        user.getBasicRemote().sendText(bulidJsonErrorMessage("Failed to save chat history :("));
                                        //e.printStackTrace();
                                        logError(e);
                                    }
                                }
                            }//end of WHILE
                        } catch (SQLException sqle)
                        {
                            logError(sqle);
                        }
                    } else
                    {
                        userSession.getBasicRemote().sendText(bulidJsonErrorMessage("User Not Joined Chatroom"));
                    }

                    break;

                default:
                    userSession.getBasicRemote().sendText(bulidJsonErrorMessage("Unsupported message type is " + type));
                    break;
            }
            //String username = (String) userSession.getUserProperties().get("username");

        } catch (ParseException pe)
        {
            userSession.getBasicRemote().sendText(bulidJsonErrorMessage("Invalid parameter type"));
            pe.printStackTrace();
            logError(pe);

        }
    }

    /**
     * Execute when user has closed a connection.
     *
     * @param userSession
     */
    @OnClose
    public void onClose(Session userSession)
    {
        chatroomUsers.remove(userSession);
    }

    /**
     * Get all participants that in a chatroom id that currently on session.
     *
     * @param chatroomId
     * @return
     */
    public ArrayList<String[]> getAllParticipantsOfChatroom(String chatroomId)
    {
        ArrayList<String[]> list = new ArrayList<>();
        String[] participantCridential;// = new String[2];
        for (Session user : chatroomUsers)
        {
            String userChatroomId = (String) user.getUserProperties().get("chatroomid");
            if (chatroomId.equals(userChatroomId))
            {
                participantCridential = new String[2];
                participantCridential[0] = (String) user.getUserProperties().get("emailusername");
                participantCridential[1] = (String) user.getUserProperties().get("domain");
                list.add(participantCridential);
            }
        }
        return list;
    }

    /**
     * Bulid a normal JSON message.
     *
     * @param type
     * @param username
     * @param time
     * @param body
     * @param color
     * @param avatar
     * @param emailUsername
     * @param domain
     * @return The textual JSON Message bulid.
     */
    public String bulidJsonMessage(String type, String username, String time, String body, String color, String avatar, String emailUsername, String domain)
    {
        JsonObject jsonObject;
        JsonObjectBuilder b = Json.createObjectBuilder();

        b.add("type", type);
        b.add("username", username);
        b.add("time", time);
        b.add("body", body);
        b.add("color", color);
        b.add("avatar", avatar);
        b.add("emailusername", emailUsername);
        b.add("domain", domain);
        jsonObject = b.build();
        return jsonObjectToString(jsonObject);
    }

    public String bulidJsonErrorMessage(String body)
    {
        JsonObject jsonObject;
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("type", "error");
        b.add("body", body);
        jsonObject = b.build();
        return jsonObjectToString(jsonObject);
    }

    /**
     * A temp method for JSON data
     *
     * @param username
     * @param message
     * @return
     */
    public String bulidJsonData(String username, String message)
    {
        JsonObject jsonObject;
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("message", username + ": " + message);
        jsonObject = b.build();
        return jsonObjectToString(jsonObject);
    }

    public String bulidJsonSuccessMessage(String username)
    {
        JsonObject jsonObject;
        JsonObjectBuilder b = Json.createObjectBuilder();
        b.add("type", RETURNTYPE_CONNECTED);
        b.add("username", username);
        jsonObject = b.build();
        return jsonObjectToString(jsonObject);
    }

    /**
     * Convert JSON object to string.
     *
     * @param jsonObject
     * @return
     */
    public String jsonObjectToString(JsonObject jsonObject)
    {
        StringWriter writer = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(writer))
        {
            jsonWriter.write(jsonObject);
        }
        return writer.toString();
    }

    public static void main(String[] args)
    {
        System.out.println(getPresentFormattedDaytime(8));
    }

}
