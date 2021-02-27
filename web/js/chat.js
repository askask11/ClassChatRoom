/* 
 * Author: jianqing gao
 * Date: Nov 23, 2020
 * Description: This document is created for chat
 */

function handleChatMessage(jsonData)
{
    var msgElement = document.createElement("div");
    //msgElement.classList.add("container");

    var type = jsonData.type;
    if (type === "connected")
    {
        var username = jsonData.username;
        document.getElementById("messageText").disabled = false;
        msgElement.style.setProperty("color", "green");
        msgElement.innerText = "[System] " + username + ", You are connected to chat room id " + document.getElementById("chatroomId").value + ", let's start chatting!";

        document.getElementById("username").value = username;

    } else if (type === "error")
    {
        msgElement.innerText = "[System] Error: " + jsonData.body + "\n";
        msgElement.style.setProperty("color", "red");
    } else if (type === "text" || type === "image")
    {
        var localUsername = document.getElementById("username").value;
        msgElement.classList.add("msgcontainer");
        //main container
        //adjust css
        var avatar = document.createElement("img");//avatar
        avatar.alt = "Avatar";
        avatar.style.setProperty("width", "100%");
        avatar.classList.add("avatar");

        var textElement = document.createElement("p");
        var imageElement = document.createElement("img");
        imageElement.classList.add("userImage");
        var usernameElement = document.createElement("h4");
        usernameElement.classList.add("inline");
        var timeElement = document.createElement("span");
        //check if this is my message
        if (jsonData.username === localUsername)
        {
            timeElement.classList.add("time-left");
            avatar.classList.add("right");
            msgElement.classList.add("darker");

        } else
        {
            timeElement.classList.add("time-right");
        }


        //assign data
        timeElement.innerHTML = jsonData.time;
        if (jsonData.avatar === undefined)
        {
            avatar.style.setProperty("display", "none");
        } else
        {
            avatar.src = jsonData.avatar;
        }

        //means it's retriving history record
        if (jsonData.username === undefined)
        {
            usernameElement.innerText = jsonData.emailusername + "@" + jsonData.domain;
        } else
        {
            usernameElement.innerText = jsonData.username + "  <" + jsonData.emailusername + "@" + jsonData.domain + ">";
        }

        if (type === "image")
        {
            imageElement.src = jsonData.body;
            imageElement.onclick=function () {
                window.open(jsonData.body,"_blank");
            };
            imageElement.style.setProperty("cursor","pointer");
            imageElement.title = "Click to view original.";
        } else
        {
            textElement.innerText = jsonData.body;
        }

        //put on main frame

        msgElement.appendChild(avatar);
        msgElement.appendChild(usernameElement);
        if (type === "image")
        {
            //if user retrive history record, flip the time and image
            if (jsonData.username === undefined)
            {
                imageElement.classList.add("centralized");
                msgElement.appendChild(timeElement);
                msgElement.appendChild(imageElement);
            } else
            {
                msgElement.appendChild(imageElement);
                msgElement.appendChild(timeElement);
            }


        } else
        {
            msgElement.appendChild(textElement);
            msgElement.appendChild(timeElement);
        }

        document.getElementById("messagesTextArea").appendChild(msgElement);
    } else
    {
        alert("There is an error!!!");
    }
    scrollButton();
    document.getElementById("messagesTextArea").appendChild(msgElement);
}

/**
 * Fetch the history unread for users.
 * @returns {undefined}
 */
function fetchHistory()
{
    //construct variables
    var waitdiv = document.createElement("div");
    var waitimage = document.createElement("img");
    var waitp = document.createElement("p");

    //assign attributes
    waitimage.src = "https://xeduocdn.sirv.com/icons/spinner.svg";
    waitimage.alt = "loading";
    waitp.innerText = "Loading your missing chats...";
    waitp.style.setProperty("color", "#3399ff");

    //add components to containers
    waitdiv.appendChild(waitp);
    waitdiv.appendChild(waitimage);
    document.getElementById("messagesTextArea").appendChild(waitdiv);

    //start the job
    var id = document.getElementById("chatroomId").value;
    var xhr = new XMLHttpRequest();
    xhr.open("GET", "./ChatRoomHistory?id=" + id);
    xhr.send();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200)
        {
            //history record fetched
            var jsonArray = JSON.parse(xhr.responseText);
            for (var i = 0; i < jsonArray.length; i++) {
                var jj = jsonArray[i];
                handleChatMessage(jj);
            }
            var h = document.createElement("div");
            h.innerHTML = "<center>-Here Are The Chats That You Missed-</center>";
            document.getElementById("messagesTextArea").appendChild(h);
            waitdiv.style.setProperty("display", "none");
        } else if (xhr.readyState === 4)
        {
            waitdiv.style.setProperty("display", "none");
            appendError("Failed to fetch history:( <a href='javascript:fetchHistory()'>retry</a>");
        }
    };
}

var websocket;

function connectWs() {
    var e = document.createElement("div");
    e.innerHTML = "[System]Connecting to chat... <img src=\"https://xeduocdn.sirv.com/icons/spinner.svg\" alt=\"loading...\" />";
    e.style.setProperty("color", "#3399ff");
    e.classList.add("connecttext");
    document.getElementById("messagesTextArea").appendChild(e);
    var host = document.location.host;
//    if(host==="localhost:8080")
//    {
//        host+="/ClassChatRoom";
//    }
    websocket = new WebSocket("ws" + (host === "localhost:8080" ? "" : "s") + "://" + host + (host === "localhost:8080" ? "/ClassChatRoom" : "") + "/Chat" + (host === "localhost:8080" ? "" : "/"));

    websocket.onopen = function handleOnopen()
    {
        var userToken = document.getElementById("token").value;
        var chatroomId = document.getElementById("chatroomId").value;
        var loaddocs = document.getElementsByClassName("connecttext");
        var verifyJsonObject = {
            "type": "verify",
            "chatroomid": chatroomId,
            "body": userToken
        };
        try {
            websocket.send(JSON.stringify(verifyJsonObject));
            for (var i = 0; i < loaddocs.length; i++) {//chat opened
                loaddocs[i].style.setProperty("display", "none");
            }
        } catch (e) {
            appendError("Failed to send. Please sign-in again!");
        }
        fetchHistory();
    };

    websocket.onmessage = function processMessage(message)
    {
        var jsonData = JSON.parse(message.data);
        handleChatMessage(jsonData);
    };


    websocket.onclose = function handleClose()
    {
        appendError("Chat session has been closed. <a href='javascript:connectWs()'>reconnect</a>");
        document.getElementById("messageText").disabled = true;
    };
}


function appendError(err)
{
    var msgElement = document.createElement("div");
    msgElement.style.setProperty("color", "red");
    msgElement.innerHTML = "[System] " + err + "\n";
    document.getElementById("messagesTextArea").appendChild(msgElement);
}



function sendTextMessage()
{
    var sendObj = {
        "type": "text",
        "body": document.getElementById("messageText").value,
        "chatroomid": document.getElementById("chatroomId").value
    };
    try {
        websocket.send(JSON.stringify(sendObj));//GOODBYE SIR
    } catch (exception) {
        appendError("Please sign-in again! Your session has expired! You will leave this page in 3 seconds");
        setTimeout(function () {
            window.open("/index.jsp");
        }, 3000);

    }
    document.getElementById("messageText").value = "";
}
;
function scrollButton()
{
    document.getElementById("messagesTextArea").scrollTo(0, document.querySelector("#messagesTextArea").scrollHeight);
}

function backToRoot()
{
    var a = document.createElement("a");
    a.href = "#";
    a.click();
}

const ALLOWED_EXTENSIONS = [".jpg", ".jpeg", ".gif", ".png", ".svg", ".bmp"];
function isValidExtension()
{
    var p = document.getElementById("file").value;
    for (var i = 0, max = ALLOWED_EXTENSIONS.length; i < max; i++) {
        if (p.endsWith(ALLOWED_EXTENSIONS[i]))
        {
            return true;
        }
    }
    return false;
}

function sendImage()
{

    //var currenturl;
//xhr connection
    if (!isValidExtension())
    {
        appendError("Please upload a supported format file. Support: .jpg, .jpeg, .gif, .png, .svg, .bmp");
        backToRoot();
        scrollButton();
        return;
    }
    document.getElementById("uploadimg").classList.remove("hidden");

    const Http = new XMLHttpRequest();
    //const url =;
    Http.open("POST", "https://server.1000classes.com/bmcserver/UpHomework");
    Http.send(new FormData(document.getElementById("form1")));
    Http.onreadystatechange = (e) => {
        if (Http.readyState === 4 && Http.status === 200)
        {
            console.log(Http.responseText);
            var jobj = JSON.parse(Http.responseText);
            var msg = jobj.msg;
            if (msg === "ok")
            {
                var jurl = jobj.picurl;
                var sendObj = {
                    "type": "image",
                    "chatroomid": document.getElementById("chatroomId").value,
                    "body": jurl
                };
                try {
                    websocket.send(JSON.stringify(sendObj));
                    document.getElementById("uploadimg").classList.add("hidden");
                    backToRoot();
                    scrollButton();
                } catch (exception) {
                    appendError("failed to send your image");
                    document.getElementById("uploadimg").classList.add("hidden");
                    backToRoot();
                    scrollButton();
                    console.log(exception);
                }
            } else
            {
                appendError("Sorry, we failed to send image due to storage error:(");
                backToRoot();
                scrollButton();
            }
        } else if (Http.readyState === 4)
        {
            appendError("Sorry, failed to send your image");
            backToRoot();
            scrollButton();
        }
    };

}

