/**
 * Servlet for WEB-INF/main.jsp
 */
package com.classchatroom.controller;

import com.classchatroom.model.ChatGroup;
import com.classchatroom.model.InitializeMainScreen;
import com.classchatroom.model.Randomizer;
import com.classchatroom.model.SqlDatabase;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static com.classchatroom.model.OSSAccessor.logError;

/**
 *
 * @author seunggulee
 */
@WebServlet(name = "MainScreenServlet", 
        loadOnStartup = 1,
        urlPatterns = {
//                    "/OpenChat", 
                    "/JoinChat", 
                    "/CreateChat", 
                    "/LogOut", 
                    "/ManageAccount",
                    "/ViewChatDetails",
                    "/LoadMain"})

public class MainScreenServlet extends HttpServlet 
{
    private HttpSession session;
    private SqlDatabase dbObj;
    
    
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
        session = request.getSession(); 
        String userPath = request.getServletPath();
        
        // Log out user
        if(userPath.equals("/LogOut"))
        {
            session.setAttribute("logInMessage", "Successfully logged out. See you later!");
            try {
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                logError(ex);
            }
            
            session.invalidate();
        }
        else if(userPath.equals("/ViewChatDetails"))
        {
            // Connect to database
            String dbName = "classchat";
            dbObj = SqlDatabase.createDefaultInstance();
            
            viewChatDetails(request, response, dbObj);
        }
        else if(userPath.equals("/LoadMain"))
        {
            loadMain(request, response, dbObj);
        }
        else if(userPath.equals("/ManageAccount"))
        {
            try {
                request.getRequestDispatcher("WEB-INF/manage-account.jsp").forward(request, response);
            }
            catch(Exception ex) {
                ex.printStackTrace();logError(ex);
            }
        }
    }
    
    private void loadMain(HttpServletRequest request, HttpServletResponse response, SqlDatabase dbObj)
    {
        try {
            String username = (String)session.getAttribute("username");
            String domain = (String)session.getAttribute("domain");
            String[] email = {username, domain};
            InitializeMainScreen.initialize(email, session, dbObj);
        }
        catch(Exception ex) { ex.printStackTrace(); logError(ex);}

        try {
            request.getRequestDispatcher("WEB-INF/main.jsp").forward(request, response);
        }
        catch(Exception ex) {
            ex.printStackTrace();logError(ex);
        }

        session.setAttribute("manageAccountAlert", "");
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
        // Get session
        session = request.getSession(); 
        String userPath = request.getServletPath();
        
        // Connect to database
        String dbName = "classchat";
        dbObj = SqlDatabase.createDefaultInstance();
        
        if(userPath.equals("/JoinChat"))
        {
            String groupId = request.getParameter("groupId");
            try { joinChat(request, response, dbObj, groupId); }
            catch(Exception ex) { ex.printStackTrace(); logError(ex);}
        }
        else if(userPath.equals("/OpenChat"))
        {
            // To make a request to chat interface, call "/ChatRoomPage" with parameter "groupId"
            openChat(request, response, dbObj);
        }
        else if(userPath.equals("/CreateChat"))
        {
            try { createChat(request, response, dbObj); }
            catch(Exception ex) { ex.printStackTrace(); logError(ex);}
        }
    }
    
    /**
     * Opens chat.
     * @param request
     * @param response
     * @param dbObj 
     */
    private void openChat(HttpServletRequest request, HttpServletResponse response,
            SqlDatabase dbObj)
    {
        //This function is in ChatroomServlet
        // To make a request to chat interface, call "/ChatRoomPage" with parameter "groupId"
    }
    
    
    /**
     * Add user to the group
     * @param request
     * @param response
     * @param dbObj 
     */
    private void joinChat(HttpServletRequest request, HttpServletResponse response,
            SqlDatabase dbObj, String groupIdString) throws SQLException
    {
        int groupId = Integer.parseInt(groupIdString);
        String username = (String)session.getAttribute("username");
        String domain = (String)session.getAttribute("domain");
        String[] email = {username, domain};
        
        // Add user to group
        boolean success = dbObj.addUserToGroup(groupId, username, domain);
        // Returns false if user is already in the group.
        
        try {
            // Alert with JS
            if(success) {
                response.getWriter().println("<script>alert('Group successfully joined. "
                        + "Refreshing the page.');</script>");
            }
            else {
                response.getWriter().println("<script>alert('Sorry, it seems like you are already"
                        + "in the group, or you are not in the same organization as the host.');</script>");
            }
            // Refresh
            loadMain(request, response, dbObj);
//            InitializeMainScreen.initialize(email, session, dbObj);
//            request.getRequestDispatcher("WEB-INF/main.jsp").forward(request, response);
        }
        catch(Exception ex) { ex.printStackTrace(); logError(ex);}
    }
    
    
    /**
     * Creates chat room
     * @param request 
     * @param response
     * @param dbObj 
     */
    private void createChat(HttpServletRequest request, HttpServletResponse response,
            SqlDatabase dbObj) throws SQLException
    {
        // Generate random ID
        int groupId;
        do {
            groupId = Randomizer.randomInt(1000, 2107483647);
        } while(dbObj.groupIdExists(groupId));
        
        // Fetch data
        String schoolDomain = (String)session.getAttribute("domain");
        
        String groupName = request.getParameter("groupName");
        String schoolName = request.getParameter("schoolName");
        String groupDescription = request.getParameter("groupDescription");
        
        String hostUsername = (String)session.getAttribute("username");
        
        String username = (String)session.getAttribute("username");
        String domain = (String)session.getAttribute("domain");
        String[] email = {username, domain};
        
        // Create group
        boolean success1 = dbObj.createGroup(groupId, schoolDomain, groupName,
                schoolName, groupDescription, hostUsername);
        // Join group
        boolean success2 = dbObj.addUserToGroup(groupId, username, domain);

        
        try {
            // Alert with JS
            if(success1 && success2) {
                response.getWriter().println("<script>alert('Group successfully created. "
                        + "Refreshing the page.');</script>");
            }
            else {
                response.getWriter().println("<script>alert('Sorry, an unexpected error "
                        + "has occured on the server side.');</script>");
            }
            // Refresh
            loadMain(request, response, dbObj);
//            InitializeMainScreen.initialize(email, session, dbObj);
//            request.getRequestDispatcher("WEB-INF/main.jsp").forward(request, response);
        }
        catch(Exception ex) { ex.printStackTrace(); logError(ex);}
    }
    
    private void viewChatDetails(HttpServletRequest request, HttpServletResponse response, SqlDatabase dbObj)
    {
        int groupId = Integer.parseInt(request.getParameter("groupId"));
        ChatGroup cg = dbObj.getGroupObject(groupId);
        
        session.setAttribute("groupIdToJoin", groupId);
        session.setAttribute("groupNameToJoin", cg.getGroupName());
        session.setAttribute("groupSchoolToJoin", cg.getSchoolName());
        session.setAttribute("groupDescriptionToJoin", cg.getGroupDescription());
        
        
        try {
            request.getRequestDispatcher("WEB-INF/chat-details.jsp").forward(request, response);
        }
        catch(Exception ex) { ex.printStackTrace(); logError(ex);}
    }
    
    

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    
    
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MainScreenServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MainScreenServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

}
