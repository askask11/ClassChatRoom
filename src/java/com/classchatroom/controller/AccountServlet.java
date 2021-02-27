package com.classchatroom.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.classchatroom.model.InitializeMainScreen;
import com.classchatroom.model.JavaMailer;
import com.classchatroom.model.Randomizer;
import com.classchatroom.model.SqlDatabase;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
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
@WebServlet(name = "AccountServlet",
        loadOnStartup = 1,
        urlPatterns = {"/LogIn", 
                        "/Test",
                        "/RegisterStep1",
                        "/RegisterStep2",
                        "/ModifyAccount"})

public class AccountServlet extends HttpServlet 
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
        // Get session
        session = request.getSession(); 
        String userPath = request.getServletPath();
        
        // Connect to database
        String dbName = "classchat";
        dbObj = SqlDatabase.createDefaultInstance();
        
        
        if(userPath.equals(""))
        {
            
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
        // Get session
        session = request.getSession(); 
        String userPath = request.getServletPath();
        
        // Connect to database
        String dbName = "classchat";
        dbObj = SqlDatabase.createDefaultInstance();
        
        
        if(userPath.equals("/LogIn"))
        {
            logIn(request, response, dbObj);
        }
        else if(userPath.equals("/RegisterStep1"))
        {
            registerStep1(request, response, dbObj);
        }
        else if(userPath.equals("/RegisterStep2"))
        {
            registerStep2(request, response, dbObj);
        }
        else if(userPath.equals("/ModifyAccount"))
        {
            manageAccount(request, response, dbObj);
        }
    }
    
    
    /**
     * Try log in.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param dbObj database access object
     */
    private void logIn(HttpServletRequest request, HttpServletResponse response, 
            SqlDatabase dbObj)
    {
        // Get parameters
        String email = request.getParameter("email");
        String[] splittedEmail = email.split("@");
        String password = request.getParameter("password");
        
        // Try log in
        boolean logInSuccess = dbObj.logIn(splittedEmail[0], splittedEmail[1], encryptPassword(password));
        
        if(logInSuccess) // log in success
        {
            // Update token
            boolean updateTokenSuccess = updateToken(splittedEmail, dbObj);
            // Load user info and sets it as string attributes
            initializeInfo(splittedEmail, dbObj);
            
            if(updateTokenSuccess)
            {
                try {
                request.getRequestDispatcher("WEB-INF/main.jsp").forward(request, response);
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    logError(ex);
                }
            }
            else // failed to update token
            {
                session.setAttribute("logInMessage", "Sorry, an unexpected error has occured. "
                        + "(Failed to update token)");
                try {
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    logError(ex);
                }
                session.invalidate();
            }
        }
        else // log in fail
        {
            session.setAttribute("logInMessage", "Email and password is incorrect. Please try again.");
            try {
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                logError(ex);
            }
            session.invalidate();
        }
    }
    
    /**
     * Generate and update user's token on DB
     * @param splittedEmail Email of the user in String[]
     * @param dbObj Database object
     * @return True if action was success, false otherwise
     */
    private boolean updateToken(String[] splittedEmail, SqlDatabase dbObj)
    {
//        // Create random string
//        final int LENGTH = 30;
//        byte[] array = new byte[LENGTH];
//        new Random().nextBytes(array);
//        String generatedToken = new String(array, Charset.forName("UTF-8"));
        
        String generatedToken = Randomizer.generateBash();

        // Update on DB and session
        boolean success = dbObj.updateToken(splittedEmail[0], splittedEmail[1], generatedToken);
        session.setAttribute("userToken", generatedToken);
        
        return success;
    }
    
    /**
     * Loads user data from DB and sets them as session attributes
     * @param splittedEmail User's email address as String[]
     * @param dbObj Database object
     */
    private void initializeInfo(String[] splittedEmail, SqlDatabase dbObj)
    {
        session.setAttribute("name", dbObj.getUserName(splittedEmail[0], splittedEmail[1]));
        session.setAttribute("username", splittedEmail[0]);
        session.setAttribute("domain", splittedEmail[1]);
        
        InitializeMainScreen.initialize(splittedEmail, session, dbObj);
    }
    
    
    
    /**
     * First step of registration; gets parameters, sets them as attributes, 
     * and sends a verification email to user.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param dbObj Database access object
     */
    private void registerStep1(HttpServletRequest request, HttpServletResponse response, 
            SqlDatabase dbObj)
    {
        // Get parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String schoolName = request.getParameter("schoolName");
        int gradYear = Integer.parseInt(request.getParameter("gradYear"));
        String password = request.getParameter("password");
        long timezone = Long.parseLong(request.getParameter("timezone"));
        
        String[] splittedEmail = email.split("@");
        
        // Check if account already exists
        boolean accountExists = dbObj.accountExist(splittedEmail[0], splittedEmail[1]);
        
        if(accountExists) // Account already exists
        {
            session.setAttribute("registerMessage", "The account (" + splittedEmail[0] +
                    "@" + splittedEmail[1]  + ") already exists. Try <a href='login.jsp'>signing in</a> instead.");
            try {
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                logError(ex);
            }
            session.invalidate();
        }
        else // Email
        {
            // Set attributes
            session.setAttribute("name", name);
            session.setAttribute("email", splittedEmail[0] + "@" + splittedEmail[1]);
            session.setAttribute("schoolName", schoolName);
            session.setAttribute("gradYear", gradYear);
            session.setAttribute("encryptedPassword", encryptPassword(password));
            session.setAttribute("timezone", timezone);
            
            // Create verification code
            int verificationCode = 0;
            Random randomObj = new Random();
            while(verificationCode < 100000)
            {
                verificationCode = randomObj.nextInt(1000000);
            }
            session.setAttribute("registrationVerificationCode", verificationCode);
            
            // Send verification email
            new JavaMailer(splittedEmail[0] + "@" + splittedEmail[1],
                    "Your verification code for ClassChat",
                    "<p>Hello " + name + ",</p><br/>"
                    + "<p>Your verification code for ClassChat is:</p>"
                    + "<h3>" + verificationCode + "</h3>"
                    + "<p>Please enter this code to complete your ClassChat registration. "
                    + "Do not share this code to others.</p><br/>"
                    + "<p>(This email address cannot receive messages. "
                    + "Please do not reply to this email.)</p>"
            );
            
            // Open 2nd step page
            try {
                request.getRequestDispatcher("WEB-INF/registration-verify.jsp").forward(request, response);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                logError(ex);
            }
        }
    }
    
    
    /**
     * Second step of registration, asks user to confirm their email.
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param dbObj Database access object
     */
    private void registerStep2(HttpServletRequest request, HttpServletResponse response, 
            SqlDatabase dbObj)
    {
        String verificationCode = request.getParameter("verificationCode");
        
        String correctCode = Integer.toString((int)session.getAttribute("registrationVerificationCode"));
        
        // Verification code match
        if(verificationCode.equals(correctCode))
        {
            System.out.println("Verification code matches");
            
            // Email and PW
            String email = (String)session.getAttribute("email");
            String[] splittedEmail = email.split("@");
            String encryptedPassword = (String)session.getAttribute("encryptedPassword");
            
            // Name and others
            String userName = (String)session.getAttribute("name");
            String schoolName = (String)session.getAttribute("schoolName");
            int gradYear = (int)session.getAttribute("gradYear");
            
            // Motification settings (default)
            int notifSettings = 1;
            String latestNotifSent = "1970.01.01.01.01.01"; // 1970/01/01 01:01:01 AM

            long timezone = (long)session.getAttribute("timezone");
            boolean banned = false;
            String token = ""; // TODO
            
            // Register
            boolean registrationSuccess = dbObj.register(splittedEmail[0], 
                    splittedEmail[1], encryptedPassword, userName, schoolName, 
                    gradYear, notifSettings, latestNotifSent, timezone, banned, token);
            
            // Registration failed
            if(!registrationSuccess)
            {
                session.setAttribute("registerMessage", "Sorry, an unexpected error occured while registering.");
                try {
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                }
                catch(Exception ex) {
                    ex.printStackTrace();
                    logError(ex);
                }
                return;
            }
            
            // Send confirmation email
            new JavaMailer(splittedEmail[0] + "@" + splittedEmail[1],
                    "Welcome to ClassChat!",
                    "<p>Hello " + userName + ",</p><br/>"
                    + "<p>We wanted to welcome you for joining us ClassChat!</p>"
                    + "<p>TODO enter additional welcome message</p>" // TODO enter additional welcome message
                    + "<p>(This email address cannot receive messages. "
                    + "Please do not reply to this email.)</p>"
            );
            
            // Go to Login page
            session.setAttribute("logInMessage", "Registration was a success! Please log in.");
            try {
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                logError(ex);
            }
        }
        else // Mismatch
        {
            System.out.println("Verification code does not match");
            
            session.setAttribute("registerMessage", "The verification code is incorrect. Please try again.");
            try {
                request.getRequestDispatcher("register.jsp").forward(request, response);
            }
            catch(Exception ex) {
                ex.printStackTrace();
                logError(ex);
            }
            
        }
        session.invalidate();
    }
    
    
    /**
     * Encrypts the password with MD5.
     * @param password Password to encrypt
     * @return Encrypted password (32 chars long)
     */
    public String encryptPassword(String password)
    {
        String output = "";
        
        // Encrypt password to MD5 hash
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();

            StringBuilder builder = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                builder.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            output = builder.toString();
            
            System.out.println("Encrypted Password: " + output);
        }
        catch(NoSuchAlgorithmException nsaex)
        {
            nsaex.printStackTrace();
            logError(nsaex);
        }
        
        return output;
    }
    
    
    private void manageAccount(HttpServletRequest request, HttpServletResponse response, SqlDatabase dbObj)
    {
        String[] email = {(String)session.getAttribute("username"), 
            (String)session.getAttribute("domain")};
        String newName = request.getParameter("newName");
        String password = encryptPassword(request.getParameter("password"));
        String newPassword = encryptPassword(request.getParameter("newPassword"));
        
        boolean logInSuccess = dbObj.logIn(email[0], email[1], password);
        
        if(logInSuccess)
        {
            if(!newPassword.equals("")) // User wants password change
            {
//                dbObj.updatePassword(email[0], email[1], newPassword);
            }
            
            // Update name
//            dbObj.updateName(email[0], email[1], newName);
        }
        else // login fail
        {
            session.setAttribute("manageAccountAlert", "The password you have entered is incorrect.");
        }
        
        // Reload page
        try {
            request.getRequestDispatcher("WEB-INF/manage-account.jsp").forward(request, response);
        }
        catch(Exception ex) {
            ex.printStackTrace();
            logError(ex);
        }
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
            out.println("<title>Servlet AccountServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AccountServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
}
