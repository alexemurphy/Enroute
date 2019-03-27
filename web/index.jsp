<%--
  Created by IntelliJ IDEA.
  User: Liam Pugh
  Date: 08/02/2019
  Time: 08:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="main.db.DBHelper" %>
<html>

<%
    /*
    If authenticated then:
    String newKey = new Authentication().genNewSessionKey();
    session.setAttribute("sessionKey", newKey);
    // To go elsewhere: response.sendRedirect("/name.jsp?sessionKey="+newKey);
     */
    DBHelper dbHelper = DBHelper.instance();

    // Check for errors when hitting the index (usually session timeout)
    String error = "";
    error = error + request.getParameter("error");
    if (!error.equals("null")) {%>
<script> alert("<%=error%>") </script>
<%
        ;
    }
%>


<html style="opacity: 1;">

<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width,maximum-scale=1,user-scalable=0, initial-scale=1.0, shrink-to-fit=no">
    <title>Enroute</title>
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Roboto:100,100i,300,300i,400,400i,500,500i,700,700i,900,900i">
    <link rel="stylesheet" href="assets/css/styles2.css">
    <link rel="apple-touch-icon" href="single-page-icon.png">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <link href="splashscreens/iphone5_splash.png"
          media="(device-width: 320px) and (device-height: 568px) and (-webkit-device-pixel-ratio: 2)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/iphone6_splash.png"
          media="(device-width: 375px) and (device-height: 667px) and (-webkit-device-pixel-ratio: 2)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/iphoneplus_splash.png"
          media="(device-width: 621px) and (device-height: 1104px) and (-webkit-device-pixel-ratio: 3)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/iphonex_splash.png"
          media="(device-width: 375px) and (device-height: 812px) and (-webkit-device-pixel-ratio: 3)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/iphonexr_splash.png"
          media="(device-width: 414px) and (device-height: 896px) and (-webkit-device-pixel-ratio: 2)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/iphonexsmax_splash.png"
          media="(device-width: 414px) and (device-height: 896px) and (-webkit-device-pixel-ratio: 3)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/ipad_splash.png"
          media="(device-width: 768px) and (device-height: 1024px) and (-webkit-device-pixel-ratio: 2)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/ipadpro1_splash.png"
          media="(device-width: 834px) and (device-height: 1112px) and (-webkit-device-pixel-ratio: 2)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/ipadpro3_splash.png"
          media="(device-width: 834px) and (device-height: 1194px) and (-webkit-device-pixel-ratio: 2)"
          rel="apple-touch-startup-image"/>
    <link href="splashscreens/ipadpro2_splash.png"
          media="(device-width: 1024px) and (device-height: 1366px) and (-webkit-device-pixel-ratio: 2)"
          rel="apple-touch-startup-image"/>

</head>

<body style="height: auto;/*max-width: auto;*/background-position: center;background-size: cover;margin-left: auto;margin-right: auto;margin-top: 0;margin-bottom: 0;background-repeat: no-repeat;background-attachment: fixed;background-image: url(&quot;assets/img/Crazy-Traffic-Laws-2.jpg&quot;);min-height: 0;">
<img src="assets/img/logo-wide.png" class="center"
     style="margin-top: 56px;margin-right: auto;margin-left: auto;padding-left: 20px;padding-right: 20px;max-width: 360px;width: 100%;">

<div class="center"
     style="margin-right: auto;margin-left: auto;margin-top: 45px;min-height: 100px;padding-left: 20px;padding-right: 20px;max-width: 360px;">
    <form action="LoginServlet" method="post">
        <div style="border-radius: 4px;margin-bottom: 17px;min-width: 100%;background-color: #000000;min-height: 100px;">
            <div style="min-width: 100%;min-height: 50px;">
                <div style="max-width: 20%;margin-bottom: 0px;float: left;padding-left: 20px;padding-top: 12px;"><img
                        src="assets/img/email.png" style="max-width: 30px;"></div>
                <div style="min-height: 49px;/*min-width: 100;*/margin-right: 0;margin-left: auto;max-width: 80%;">
                    <input type="text"
                           style="border: 0px solid;min-width: 93%;min-height: 50px;background-color: rgb(0,0,0);color: rgb(173,173,173);font-family: Roboto, sans-serif;font-size: 17px;padding-left: 9px;font-weight: 300;"
                           name="username" placeholder="Username"></div>
            </div>
            <div style="min-height: 50px;">
                <div style="max-width: 20%;margin-bottom: 0px;float: left;padding-left: 25px;padding-top: 8px;"><img
                        src="assets/img/password.png" style="max-width: 19px;"></div>
                <div style="margin-right: 0px;margin-left: auto;min-width: 80%;max-width: 80%;"><input type="password"
                                                                                                       style="border: 0px solid;min-width: 93%;min-height: 50px;background-color: rgb(0,0,0);color: rgb(173,173,173);font-family: Roboto, sans-serif;font-size: 17px;padding-left: 9px;font-weight: 300;"
                                                                                                       name="password"
                                                                                                       placeholder="Password">
                </div>
            </div>
        </div>
        <button class="btn btn-primary center" type="submit"
                style="border:0;padding: 0px;margin-right: auto;margin-left: auto;min-width: 100%;min-height: 53px;background-color: rgb(237,220,54);color: rgb(0,0,0);font-size: 21px;font-family: Roboto, sans-serif;font-weight: 600;font-style: normal;">
            LOG IN
        </button>
    </form>

    <form action="signUpScreen.jsp">
        <button class="btn btn-primary center" type="submit"
                style="border: 0;background-color: rgba(0,123,255,0);font-size: 13px;margin-top: 12px;margin-left: auto;margin-right: 0px;color: rgb(237,220,54);font-family: Roboto, sans-serif;font-weight: 500;">
            Sign Up
        </button>
    </form>
</div>

<script src="assets/js/jquery.min.js"></script>
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="javascript/inobounce.js"></script>
</body>

</html>


</html>