<%-- 
    Document   : manage-account
    Created on : Nov 22, 2020, 12:13:06 PM
    Author     : seunggulee
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ClassChat</title>
        <link href="css/login.css" type="text/css" rel="stylesheet">
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
    </head>
    <body>
        <a href="LoadMain">Back</a>
        
        
        
        <div class="main-div">
            <br/>
            <img src="images/Logo.png" class="logo-img">
            <br/>
            <h1>Manage Account</h1>

            <p class='warning-text'>${manageAccountAlert}</p>
            <br/>

            <form action="ModifyAccount" method="Post">
                <div class='text-input'>
                    <input name="email" id='login-email' value="${username}@${domain}" readonly>
                </div><br/>
                
                <div class='text-input'>
                    <input name="newName" id='login-name' value="${name}" required>
                    <label for='login-name'>Name</label>
                </div><br/>
                
                <div class='text-input'>
                    <input name="password" type="password" id='login-password'>
                    <label for='login-password'>Current Password</label>
                </div><br/>
                
                <div class='text-input'>
                    <input name="newPassword" type="password" id='login-new-password' required minlength="6">
                    <label for='login-new-password'>New Password (Required; Same password is ok)</label>
                </div>
                
                
                <br/>
                <input class='submit-button' type="submit" value="Change">
            </form>
        
            
        </div>
    </body>
</html>
