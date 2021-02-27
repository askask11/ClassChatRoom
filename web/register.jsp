<%-- 
    Document   : register
    Created on : Nov 21, 2020, 6:34:30 PM
    Author     : seunggulee
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>ClassChat Register</title>
        <link href="css/login.css" type="text/css" rel="stylesheet">
        <%@include file="/WEB-INF/jspf/headtags.jspf" %>
    </head>
    <body>
        <a href="index.jsp">Back</a>

        <div class='main-div'>

            <br/>
            <img src="images/Logo.png" class="logo-img">
            <br/>
            <h1>Register</h1>

            <p class="warning-text">${registerMessage}</p>



            <form action="RegisterStep1" method="Post">

                <div class='text-input'>
                    <input id='register-name' name="name" maxlength='40' required>
                    <label for='register-name'>Your Name</label>
                </div><br/>

                <div class='text-input'>
                    <input id='register-email' name="email" required minlength='1' maxlength='50'>
                    <label for='register-email'>School Email Address</label>
                </div><br/>

                <div class='text-input'>
                    <input id='register-schoolname' name="schoolName" required minlength='1' maxlength='30'>
                    <label for='register-schoolname'>School Name</label>
                </div><br/>

                <div class='text-input'>
                    <input id='register-gradyear' name="gradYear" type="number" required>
                    <label for='register-gradyear'>Graduation Year</label>
                </div><br/>

                <div class='text-input'>
                    <!--<select id='register-timezone' name='timezone' required>
                        <option value='' disabled selected hidden>Please select your time zone.</option>
                        <optgroup label='Timezone (U.S.)'>
                            <option value='-10'>Hawaii (UTC-10)</option>
                            <option value='-9'>Alaska (UTC-9)</option>
                            <option value='-8'>Pacific Standard Time (UTC-8)</option>
                            <option value='-7'>Mountain Standard Time (UTC-7)</option>
                            <option value='-6'>Central Standard Time (UTC-6)</option>
                            <option value='-5'>Eastern Standard Time (UTC-5)</option>
                            <option value='10'>Guam (UTC+10)</option>
                        </optgroup>
                        <optgroup label='Timezone (Asian)'>
                            <option value='+7'>Vietnam(UTC+7)</option>
                            <option value='+8'>China (UTC+8)</option>
                            
                        </optgroup>
                    </select>-->
                    <!--World timezone dropdown-->
                    <%@include file="/WEB-INF/jspf/timezonedd.jspf" %>
                </div>
                <p>
                    Please enter these information accurately.<br/>
                    You will need to verify your school email address on the next step.
                </p>
                <br/>



                <h3>Password</h3>

                <div class='text-input'>
                    <input id='register-pw' name="password" type="password" required minlength='6'>
                    <label for='register-pw'>Password</label>
                </div><br/>

                <div class='text-input'>
                    <input id='register-pw-confirm' name="passwordConfirm" required type="password">
                    <label for='register-pw-confirm' onchange='confirmPassword()'>Confirm Password</label>
                </div><br/>

                <p>By continuing, you agree to our Terms and Conditions.</p>


                <input class='submit-button' type="submit" value='Continue'>
            </form>



            <p><a href='login.jsp'>Already have an account? Log In</a></p>
        </div>

        <script>

            document.getElementById("register-pw-confirm")
                    .addEventListener('change',
                            function () {
                                var password1 = document.getElementById("register-pw").value;
                                var password2 = document.getElementById("register-pw-confirm").value;


                                if (password1 !== password2)
                                    alert("Passwords do not match.");
                            });

        </script>

    </body>
</html>

