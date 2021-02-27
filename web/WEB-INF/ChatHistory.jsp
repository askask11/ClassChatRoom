<%-- 
    Document   : ChatHistory
    Created on : Dec 3, 2020, 8:47:28 PM
    Author     : jianqing
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
        <link href="css/chat.css" rel="stylesheet">
        <title>JSP Page</title>
    </head>
    <body>
        <a class="back-text" href="LoadMain">Back</a>

        <span id='logout-text'>
            Signed in with ${username}@${domain}<br/>
            <a href='LogOut'>Log Out</a> <a href='ManageAccount'>| Manage Account</a>
        </span>

        <!-- Render this with javascript prevent xss -->
        <h1 id="group-header">Group History</h1>
        
        <div id="main-container">
            HI<br>
            Another link<br>
        </div>
    </body>
</html>
