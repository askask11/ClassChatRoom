<%-- 
    Document   : index.jsp
    Created on : Nov 21, 2020, 6:27:36 PM
    Author     : seunggulee
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ClassChat</title>
        <link href="css/index.css" type="text/css" rel="stylesheet">
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
    </head>
    <body>
        <div id="logo-div">
            <img src="images/Logo.png" class="logo-img">
        </div>

        <span id="nav-text">
            <a href="login.jsp">Log In</a>
            &nbsp;|&nbsp;
            <a href="register.jsp">Register</a>
        </span>

        <br/><br/>

        <h1>Welcome to ClassChat!</h1>


        <br/><br/><br/><br/>

        <h2>
            ClassChat is an innovative open-invitation chat service that allows
            anyone to hop in and chat<br/>with other students in their school, seamlessly.
        </h2>

        <h2>
            Anyone is welcome to ask questions about a homework, ask for tutoring,
            or even make new friends!<br/>
            Just have fun and socialize at ClassChat.
        </h2>


        <br/><br/>

        <button id="register-button" onclick="window.location.href = 'register.jsp'">
            Sign Up
        </button>

        <h4>
            By Team "Wooly Boolean"<br/>
            (Seung-Gu Lee, Johnson Gao, Ben Le)
        </h4>

        <div class="credits-text">
            Created by Team "Wooly Boolean" for WWPHacks2020<br/>
            Team Members: Seung-Gu Lee, Johnson(Jianqing) Gao, Ben(Huy) Le<br/><br/>
            <a href="https://devpost.com/software/classchat-ojbmcz" target="_blank">Devpost</a> |
            <a href="https://github.com/askask11/ClassChatRoom" target="_blank">Github</a> |
            <a href="credits.html" target="_blank">Credits</a>
        </div><!-- TODO insert link -->
    </body>
</html>
