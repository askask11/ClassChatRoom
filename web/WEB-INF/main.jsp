<%-- 
    Document   : main
    Created on : Nov 21, 2020, 6:59:17 PM
    Author     : seunggulee
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="core"%>
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
            <a href='LogOut'>Log Out</a> | <a href='ManageAccount'>Manage Account</a>
            
        </span>
        
        
        
        <h1>Welcome, ${name}!</h1>
        <br/>
        
        <!-- Where chat list will show up -->
        <div id='main-container'>
            
            <h2>Your Chats</h2>
            
            <!-- SAMPLE
            <form class='joined-chat-box' action='JoinChat' method='Post'>
                <button class='join-chat-button' type='submit'>
                    <h3>Group Chat A</h3>
                    <div class='new-notification'>N</div>
                    <img src='images/People_white.png' class='people-img'>
                    <span class='participant-count'>35</span>
                </button>
            </form>
            <form class='joined-chat-box' action='JoinChat' method='Post'>
                <button class='join-chat-button' type='submit'>
                    <h3>Group Chat B</h3>
                    <div class='new-notification'>N</div>
                    <img src='images/People_white.png' class='people-img'>
                    <span class='participant-count'>122</span>
                </button>
            </form>
            -->
            
            <core:if test='${not empty joinedGroups}'>
                <core:forEach var='row' items='${joinedGroups}'>
                    <form class='joined-chat-box' action='ChatRoomPage' method='Get'>
                        <button class='join-chat-button' type='submit'>
                            <h3>${row.getGroupName()}</h3>
                            <div class='new-notification-${row.isHasUnreadNotifications()}'>${row.getUnreadCount()}</div>
                            <img src='images/People_white.png' class='people-img'>
                            <span class='participant-count'>${row.getMemberCount()}</span>

                            <input type='hidden' name='groupName' value='${row.getGroupName()}'>
                            <input name='groupId' value="${row.getGroupId()}" style='display:none'>

                        </button>
                    </form>
                </core:forEach>
            </core:if>
            
            
            <h2 class="larger-top-padding">Join New Chats</h2>
            
            
            <div class='joined-chat-box'>
                <button class='join-chat-button' onclick='openModal("join-id-modal")'>
                    <img src='images/Search_white.png' class='front-img'>
                    <h4 class=''>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                        Enter Group ID</h4>
                </button>
            </div>
            
            <core:if test='${not empty organizationGroups}'>
                <core:forEach var='row' items='${organizationGroups}'>
                <form class='join-new-chat-box' action='ViewChatDetails' method='Get'>
                    <button class='join-chat-button' type='submit'>
                        <h4>${row.getGroupName()}</h4>
                        <p>[${row.getSchoolName()}] ${row.getGroupDescription()}</p>
                        <img src='images/People_white.png' class='people-img'>
                        <span class='participant-count'>${row.getMemberCount()}</span>

                        <input name='groupId' value="${row.getGroupId()}" style='display:none'>
                    </button>
                </form>
                </core:forEach>
            </core:if>
            
            
            <!-- SAMPLE
            <form class='join-new-chat-box' action='ViewChatDetails' method='Get'>
                <button class='join-chat-button' type='submit'>
                    <h4>Group Chat C</h4>
                    <p>Group Description. Blah Blah Blah</p>
                    <img src='images/People_white.png' class='people-img'>
                    <span class='participant-count'>122</span>
                </button>
            </form>
            
            <form class='join-new-chat-box' action='ViewChatDetails' method='Get'>
                <button class='join-chat-button' type='submit'>
                    <h4>Group Chat D</h4>
                    <p>Group Description. Blah Blah Blah</p>
                    <img src='images/People_white.png' class='people-img'>
                    <span class='participant-count'>122</span>
                </button>
            </form>
            -->
            
            
            
        </div>
            
        <!--Credits-->
        <p class='small-bottom-text'>
            Created by Team Wooly Boolean for WWPHacks2020<br/>
            
            <a href="https://devpost.com/software/classchat-ojbmcz" target="_blank">Devpost</a> |
            <a href="https://github.com/askask11/ClassChatRoom" target="_blank">Github</a> |
            <a href="credits.html" target="_blank">Credits</a>
        </p>
        
        <!-- Create Group Chat -->
        <button id='create-group-button' onclick='openModal("create-group-modal")' title='New Chat'>+</button>
        
        
        <!-- New Chat Modal -->
        <div class='fullscreen-modal' id='create-group-modal' style='display: none'>
            <span class='close-modal'>
                <a onclick='closeModal("create-group-modal")'>&times;</a>
            </span>
            <br/><br/>
            <div class='modal-container'>
                <h1>Create Chat</h1>
                
                <form action='CreateChat' method='Post'>
                    
                    <input name="groupName" id='cchat-group-name' placeholder='Group Name'
                           required maxlength='30'>

                    <input name="schoolName" id='cchat-school-name' placeholder='School Name'
                           required maxlength='30'>

                    <textarea name="groupDescription" placeholder='Description (Up to 100 characters)'
                           id='cchat-group-description' required maxlength='100'></textarea>
                    
                    
                    <button type='submit'>Create</button>
                </form>
                <br/>
                
                <p class='small-text'>
                    These information cannot be edited once you create the chat, so please be careful.<br/>
                    Do not use any inappropriate language; your account may be banned from our services.
                </p>
            </div>
        </div>
        
        <!-- Join Chat with ID Modal -->
        <div class='fullscreen-modal' id='join-id-modal' style='display: none'>
            <span class='close-modal'>
                <a onclick='closeModal("join-id-modal")'>&times;</a>
            </span>
            <br/><br/>
            <div class='modal-container'>
                <h1>Join Chat</h1>
                
                
                
                <form action='JoinChat' method='Post'>
                    
                    <div class='text-input'>
                        <input name="groupId" id='joinchat-id' required>
                        <label for='joinchat-id'>Group ID</label>
                    </div><br/>
                    
                    <button type='submit'>Join</button>
                </form>
                <br/>
                
                <p class='small-text'>
                    If you do not have a Group ID, you can find groups under "Join New Chats".
                </p>
            </div>
        </div>
    </body>
</html>

<script>
    function openModal(modalId)
    {
        document.getElementById(modalId).style.display = "block";
    }
    function closeModal(modalId)
    {
        document.getElementById(modalId).style.display = "none";
    }
</script>
