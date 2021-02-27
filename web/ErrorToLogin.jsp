<%-- 
    Document   : ErrorToLogin
    Created on : Nov 22, 2020, 12:27:21 PM
    Author     : jianqing
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Session Expired</title>
        <script>
            setTimeout(function () {
                var a = document.createElement("a");
                a.href = "login.jsp";
                a.innerHTML = "i";
                a.click();
            }, 1000);
        </script>
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
    </head>
    <body>
        <h1>Your session expired or you did not login.</h1>
        You will be redirected to login page soon. No? Click <a href="login.jsp">here</a>
        <!--Access login-->
        <form method="GET" target="_self" action="login.jsp">
            <button id="abc" type="submit">
                Go
            </button>
        </form>
    </body>
</html>
