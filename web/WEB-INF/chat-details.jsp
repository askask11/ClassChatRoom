<%-- 
    Document   : chat-details
    Created on : Nov 22, 2020, 11:02:15 AM
    Author     : seunggulee
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ClassChat</title>
        <link href="css/main.css" type="text/css" rel="stylesheet">
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
    </head>
    <body>
        <a href="LoadMain">
            <img src="images/Logo_inverted.png" id="logo-img">
        </a>
        
        <span id='logout-text'>
            Signed in with ${username}@${domain}<br/>
            <a href='LogOut'>Log Out</a> <a href='ManageAccount'>| Manage Account</a>
            
        </span>
        
        <h1>Join Chat</h1>
        <br/>
        
        
        <div id="main-container">
            
            <a href="LoadMain">Back</a>
            
            <form class="join-chat-inputs" action='JoinChat' method='Post'>
                Group ID:
                <br/><input id='joinchat-id' value="${groupIdToJoin}" disabled>
                <br/>
                Group Name:
                <br/><input value="${groupNameToJoin}" disabled>
                <br/>
                School:<br/><input value="${groupSchoolToJoin}" disabled>
                <br/>
                Description:<br/><textarea disabled>${groupDescriptionToJoin}</textarea>
                
                <div class="submit-button">
                    <button type='submit'>Join</button>
                </div>
                
                <input name="groupId" value="${groupIdToJoin}" style="display:none">
            </form>
            
        </div>
    </body>
</html>
