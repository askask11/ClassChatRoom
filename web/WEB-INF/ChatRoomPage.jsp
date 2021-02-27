<%-- 
    Document   : ChatRoomPage
    Created on : Nov 22, 2020, 10:45:38 AM
    Author     : jianqing
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chatroom Page</title>
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
        <link href="css/chat.css" rel="stylesheet">
        <link href="css/popup-nojs.css" rel="stylesheet">
        <script src='js/chat.js'></script>
    </head>
    <body>

        <a class="back-text" href="LoadMain">Back</a>

        <span id='logout-text'>
            Signed in with ${username}@${domain}<br/>
            <a href='LogOut'>Log Out</a> <a href='ManageAccount'>| Manage Account</a>
        </span>

        <!-- Render this with javascript prevent xss -->
        <h1 id="group-header"></h1>


        <!-- Text Area for chat -->
        <div id="main-container">
            <div id="messagesTextArea" class="main-div"  style="height: 500px; overflow: scroll;">

            </div><br>
            <form id="send-form" method="GET" onsubmit="sendTextMessage();" action="about:blank" target="t">

                <div class='text-input'>
                    <input type="text" id="messageText" required disabled />
                    <label for='messageText'>Message</label>
                </div>
                <button  class="centralized submit-button" type="submit">
                    Send
                </button>
                
                
                <a href="#popup1" style="display: inline-block;" >
                    <img src="https://xeduocdn.sirv.com/icons/upimage.png?w=50" style="margin-bottom: -20px; width: 50px;" alt="upload image" srcset="https://xeduocdn.sirv.com/icons/upimage.png?w=50 1x, https://xeduocdn.sirv.com/icons/upimage.png?w=100 2x"/>
                </a>
            </form>

            <div id="popup1" class="overlay" >
                <div class="popup" style="width: 44%; margin: 95px auto; ">
                    <h2>Send An Image</h2>
                    <a class="close" href="#">&times;</a>
                    <div class="content">
                        Upload a file in your computer to send an image,
                        Please drag image into this imput or click on "upload file"<br>
                        <form method="POST" enctype="multipart/form-data" id="form1" onsubmit="sendImage();" target="t2" action="about:blank">
                            <input type="file" name="file" id="file" accept=".jpg,.jpeg,.gif,.png,.svg,.bmp" required style="margin-top: 15px; margin-bottom: 17px;"><br>
                            <button type="submit" class="submit-button" style="display: block;">Submit</button>
                        </form>
                        <!--Pseudo iframe-->
                        <iframe style="width: 1px; height: 1px; visibility: hidden;" id="t2" name="t2">
                        </iframe>
                        <br>
                        <div id="uploadimg" class="hidden" style="color: green;">
                            Please wait while we upload your image...<img src="https://xeduocdn.sirv.com/icons/spinner.svg" alt="" />
                        </div>
                    </div>
                </div>
            </div>
            <input type="hidden" value="${userToken}" id="token">
            <input type="hidden" value="${chatroomId}" id="chatroomId">
            <input type="hidden" value='${groupName}' id='groupName'>
            <input type="hidden" id="username">
            <iframe id="t" name="t" style="display: none;"></iframe>
            <script>
                connectWs();
                (function () {
                    var groupName = document.getElementById("groupName").value;
                    document.getElementById("group-header").innerText = groupName;
                })();
            </script>
        </div>
    </body>
</html>
