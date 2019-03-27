<%@ page import="utils.Authentication" %>
<%@ page import="main.db.DBHelper" %><%--
  Created by IntelliJ IDEA.
  User: Liam Pugh
  Date: 08/02/2019
  Time: 08:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Session Authentication
    String sessionID = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null)
        for (Cookie cookie : cookies) if (cookie.getName().equals("sessionToken")) sessionID = cookie.getValue();
    if (sessionID == null || !sessionID.equals(session.getAttribute("sessionToken")))
        response.sendRedirect("/index.jsp?error=Session Expired, please log in again");

    int userID = DBHelper.instance().getIDFromEmail((String) session.getAttribute("username"));
%>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1,user-scalable=0, shrink-to-fit=no">
    <link rel="icon" type="image/png" sizes="16x16" href="assets/img/favicon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="assets/img/favicon32.png">
    <link rel="icon" type="image/png" sizes="180x180" href="assets/img/ios.png">
    <link rel="icon" type="image/png" sizes="192x192" href="assets/img/android.png">
    <link rel="icon" type="image/png" sizes="512x512" href="assets/img/androidsplash.png">
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="manifest" href="manifest.json">
    <link rel="stylesheet" href="assets/fonts/font-awesome.min.css">
    <link rel="stylesheet" href="assets/fonts/ionicons.min.css">
    <link rel="stylesheet" href="assets/fonts/material-icons.min.css">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Roboto:100,100i,300,300i,400,400i,500,500i,700,700i,900,900i">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/css/Sidebar-Menu-1.css">
    <link rel="stylesheet" href="assets/css/Sidebar-Menu.css">
    <link rel="stylesheet" href="assets/css/styles.css">
    <title>Enroute - History</title>
</head>
<body>

<body style="background-color: rgb(0,0,0);">
<div class="rideSharersFound" style="width: 100%;height: 100%;">
    <button class="btn btn-primary" type="button" id="backButton"
            style="background-image: url('assets/img/left-arrow.png');background-color: rgba(255,255,255,0);width: 26px;border: 0;height: 31px;background-position: top;background-size: contain;background-repeat: no-repeat;padding-top: 0;position: absolute;z-index: 101;margin-top: 9px;margin-left: 9px;"></button>
    <div style="width: 100%;height: 44px;background-color: #eddc36;"></div>
    <div class="driversFound" style=" height: 85%;width: 100%;background-color: #000000;z-index: 99;font-family: Roboto, sans-serif;color: rgb(183,183,183);font-weight: 300;overflow: auto;
        -webkit-overflow-scrolling: touch;">
        <div id="loadingDIV"
             style="display:none; height: 100%; width: 100%; align-items: center;justify-content: center;">
            <img src="assets/img/loading.gif" style="width: 57px;">
        </div>
        <div id="historyDIV">

        </div>

    </div>
    <div style="width: 100%;height: 60px;position:absolute;bottom: 0;background-color: #181818;">
        <div style="width: 50%;height: 100%;float: left;border-style: solid;border-width: 1px 1px 0 0;">
            <button id="passengerButton"
                    class="btn btn-primary d-flex flex-column justify-content-center align-items-center bottomButton"
                    type="button"
                    style="width: 100%; height: 100%;background-color: rgba(51,122,183,0);border: 0;color: #eddc36;font-family: Roboto, sans-serif;">
                <i class="material-icons historyIcons" style="font-size: 18px;">thumb_up</i>Passenger
            </button>
        </div>
        <div style="width: 50%;height: 100%;margin-left: auto;border-style: solid;border-width: 1px 0 0 0;">
            <button id="driverButton"
                    class="btn btn-primary d-flex flex-column justify-content-center align-items-center bottomButton"
                    type="button"
                    style="width: 100%; height: 100%;border: 0;background-color: rgba(51,122,183,0);color: #eddc36;font-family: Roboto, sans-serif;">
                <i class="fa fa-drivers-license historyIcons" style="font-size: 17px;"></i>Driver
            </button>
        </div>
    </div>


</div>
<script src="assets/js/jquery.min.js"></script>
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="javascript/inobounce.js"></script>
<script>

    init();

    function init() {
        document.getElementById("backButton").addEventListener("click", goBack);
        document.getElementById("driverButton").addEventListener("click", showDriverHistory);
        document.getElementById("passengerButton").addEventListener("click", showPassengerHistory);
        showPassengerHistory();
        iNoBounce.enable();
    }

    function goBack() {
        window.location.href = "/mapMainPage.jsp";
    }

    function showDriverHistory() {

        $('#loadingDIV').css("display", "flex");
        $('#historyDIV').html($('#loadingDIV').html());
        $.post('/AJAX/11', {userID: <%=userID%>}, function (data) {
            $('#loadingDIV').css("display", "none");
            $('#historyDIV').html(data);
        });
    }

    function showPassengerHistory() {
        $('#loadingDIV').css("display", "flex");
        $('#historyDIV').html($('#loadingDIV').html());
        $.post('/AJAX/10', {userID: <%=userID%>}, function (data) {
            $('#loadingDIV').css("display", "none");
            $('#historyDIV').html(data);

        });
    }

</script>
</body>

</html>
