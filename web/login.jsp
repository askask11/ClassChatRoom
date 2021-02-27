<%-- 
    Document   : login
    Created on : Nov 21, 2020, 6:34:01 PM
    Author     : seunggulee
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ClassChat Log In</title>
        <link href="css/login.css" type="text/css" rel="stylesheet">
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
    </head>
    <body>
        <a href="index.jsp">Back</a>
        
        
        
        <div class="main-div">
            <br/>
            <img src="images/Logo.png" class="logo-img">
            <br/>
            <h1>Log In</h1>

            <p class='warning-text'>${logInMessage}</p>
            <br/>

            <form action="LogIn" method="Post">
                <div class='text-input'>
                    <input name="email" id='login-email' required>
                    <label for='login-email'>Email Address</label>
                </div><br/>
                <div class='text-input'>
                    <input name="password" type="password" id='login-password' required>
                    <label for='login-password'>Password</label>
                </div>
                <br/>
                <input class='submit-button' type="submit" value="Log In">
            </form>
        
            
            <p><a href='register.jsp'>Create Account</a></p>
        </div>
    </body>
</html>
