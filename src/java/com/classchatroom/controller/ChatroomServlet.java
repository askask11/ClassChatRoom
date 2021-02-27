/*
 * Author: jianqing
 * Date: Nov 19, 2020
 * Description: This document is created for
 */
package com.classchatroom.controller;

import com.classchatroom.model.ChatDatabase;
import com.classchatroom.model.ChatRecord;
import static com.classchatroom.model.OSSAccessor.logError;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author jianqing
 */
@WebServlet(name = "ChatroomServlet", urlPatterns =
{
    "/ChatRoomPage", "/ChatRoomHistory","/ChatHistory"
}, loadOnStartup = 1)
public class ChatroomServlet extends HttpServlet
{

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter())
        {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet HandicappedServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet HandicappedServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        String path = request.getServletPath();
        switch (path)
        {
            case "/index":
                request.getRequestDispatcher(path + ".jsp").forward(request, response);
                break;
            case "/ChatRoomPage":
                //forward
                processUserChatRoom(request, response);
                //request.getRequestDispatcher("/WEB-INF"+path+".jsp").forward(request, response);
                break;
            case "/ChatRoomHistory":
                processChatRoomHistory(request, response);
                break;
            case "/ChatHistory":
                processChatHistory(request, response);
                request.getRequestDispatcher("/WEB-INF" + path + ".jsp").forward(request, response);
                break;
            default:
                processRequest(request, response);
                break;
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

    private void processChatHistory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //String token = request.getParameter("token");
        HttpSession session = request.getSession();
        String domain = (String)session.getAttribute("domain");
        String username = (String)session.getAttribute("username");
        String roomId = request.getParameter("id");
        String date = request.getParameter("date");
        
    }
    
    
    protected void processUserChatRoom(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        //test code!!!!!
        // session.setAttribute("userToken", "12345");
        // session.setAttribute("chatroomId", "11111");

        String token = (String) session.getAttribute("userToken");//pass user token from active session
        String chatroomId = request.getParameter("groupId");
        session.setAttribute("groupName", request.getParameter("groupName"));
        session.setAttribute("chatroomId", chatroomId);
        if (token == null)
        {
            response.sendRedirect(request.getContextPath() + "/ErrorToLogin.jsp");
        } else
        {
            //forward everything else to chat room.
            request.getRequestDispatcher("/WEB-INF" + request.getServletPath() + ".jsp").forward(request, response);
        }

    }

    /**
     * Chatroom history with REST modal
     * @param request
     * @param response
     * @throws IOException 
     */
    protected void processChatRoomHistory(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("userToken");
        String chatId = request.getParameter("id");

        //String amount;
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try (PrintWriter pw = response.getWriter())
        {
            try (ChatDatabase db = ChatDatabase.createDefaultInstance())
            {
                Map<String, String> map = db.selectAccountsByToken(token);
                int id = Integer.parseInt(chatId);
                if (map.size() > 0)
                {
                    String emailDomainAddress = map.get("EmailDomain");
                    String emailUsername = map.get("EmailUsername");
                    String timeZone = map.get("TimeZone");
                    long utc = Long.parseLong(timeZone);
                    //String username = map.get("UserName");
                    int unread = db.getUnreadCount(id, emailUsername, emailDomainAddress);
                    ArrayList<ChatRecord> recordList;
                    //go throgh chat history record list
                    if(unread>0)
                    {
                       recordList = db.selectFromChatHistory(id, unread);
                       
                    }else
                    {
                        recordList = new ArrayList<>(0);
                    }
                     

                    JsonObjectBuilder builder;// = Json.createObjectBuilder();
                    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

                    for (ChatRecord recordListObj : recordList)
                    {
                        builder = Json.createObjectBuilder();
                        builder.add("type", recordListObj.getType());
                        builder.add("body", recordListObj.getBody());
                        builder.add("emailusername", recordListObj.getEmailUsername());
                        builder.add("domain", recordListObj.getEmailDomain());
                        recordListObj.adjustTimeZone(utc);
                        builder.add("time", recordListObj.getTime());
                        // builder.add("", recordListObj.)
                        arrayBuilder.add(builder);
                    }

                    JsonArray array = arrayBuilder.build();

                    db.readAllMessages(id, emailUsername, emailDomainAddress);
                    pw.write(jsonObjectToString(array));
                } else
                {
                    JsonObjectBuilder builder = Json.createObjectBuilder();
                    builder.add("type", "error");
                    builder.add("body", "User Token Is Invalid");
                    pw.write(jsonObjectToString(builder.build()));
                }
            } catch (NumberFormatException nfe)
            {
                nfe.printStackTrace();
                logError(nfe);
                JsonObjectBuilder builder = Json.createObjectBuilder();
                builder.add("type", "error");
                builder.add("body", "User Token Is Invalid");
                pw.write(jsonObjectToString(builder.build()));
            } catch (SQLException sqlee)
            {
                sqlee.printStackTrace();
                logError(sqlee);
                JsonObjectBuilder builder = Json.createObjectBuilder();
                builder.add("type", "error");
                builder.add("body", "Database Error");
                pw.write(jsonObjectToString(builder.build()));
            }
        }
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
     * Convert JSON object to string.
     *
     * @param jsonObject
     * @return
     */
    public String jsonObjectToString(JsonStructure jsonObject)
    {
        StringWriter writer = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(writer))
        {
            jsonWriter.write(jsonObject);
        }
        return writer.toString();
    }
}
