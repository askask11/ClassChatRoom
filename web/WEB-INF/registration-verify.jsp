<%-- 
    Document   : registration-verify
    Created on : Nov 21, 2020, 7:48:48 PM
    Author     : seunggulee
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Classchat Registration</title>
         <link href="css/login.css" type="text/css" rel="stylesheet">
         <%@include file="/WEB-INF/jspf/headtags.jspf" %>
    </head>
    <body>
        <a href="index.jsp">Cancel</a>
        
        <div class='main-div'>
            <br/>
            <img src="images/Logo.png" class="logo-img">
            <br/>
            <h1>Verify Email</h1>

            <p>
                An email has been sent to your email address (${email}) with a
                6-digit verification code. Please enter the code here to complete your
                registration process.
            </p>
            <br/>

            <form action="RegisterStep2" method="Post">
                
                <div class='text-input'>
                    <input id='register-vcode' name="verificationCode" required>
                    <label for='register-vcode'>Verification Code</label>
                </div><br/>
                
                
                <input class='submit-button' type="submit" value='Complete'>
            </form>
            
            <p>If you haven't received any email, please check your spam box.</p>
        
        
        </div>
        
    </body>
</html>
