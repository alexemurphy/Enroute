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
    // FOR TESTING PURPOSES ONLY:
    //session.setAttribute("routeid",1);


    // Session Authentication
    String sessionID = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null)
        for (Cookie cookie : cookies) if (cookie.getName().equals("sessionToken")) sessionID = cookie.getValue();
    if (sessionID == null || !sessionID.equals(session.getAttribute("sessionToken")))
        response.sendRedirect("/index.jsp?error=Session Expired, please log in again");

    int userID = DBHelper.instance().getIDFromEmail((String) session.getAttribute("username"));
    int routeID = Integer.parseInt(request.getParameter("routeid"));
    session.setAttribute("routeid", routeID);
%>
<html>
<head>
    <title>Messenger</title>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1,user-scalable=0, shrink-to-fit=no">
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/fonts/font-awesome.min.css">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Roboto:100,100i,300,300i,400,400i,500,500i,700,700i,900,900i">
    <link rel="stylesheet" href="assets/css/chat.css">

    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
</head>
<body style="background: #000000">

<button class="btn btn-primary" type="button" id="backButton"
        style="background-image: url('assets/img/left-arrow.png');background-color: rgba(255,255,255,0);width: 26px;border: 0;height: 31px;background-position: top;background-size: contain;background-repeat: no-repeat;padding-top: 0;position: absolute;z-index: 101;margin-top: 9px;margin-left: 9px;"></button>
<div style="position:absolute; width: 100%;height: 44px;background-color: #eddc36;"></div>

<div class="container2">

    <div class="messaging">
        <div class="mesgs">
            <div class="msg_history">

            </div>
            <div class="type_msg" style="position:absolute; bottom:0;">
                <div class="input_msg_write">
                    <input id="message" type="text" class="write_msg" placeholder="Type a message">
                    <button id="sendButton" onclick="sendMessage()" class="msg_send_btn" type="button"><i
                            class="fa fa-paper-plane-o" aria-hidden="true" style="margin-left: -9px;"></i></button>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    document.getElementById("backButton").addEventListener("click", goBack);

    function goBack() {
        window.location.href = "/mapMainPage.jsp";
    }

    function getMessages() {
        $.get('/AJAX/2', function (data) {
            $('.msg_history').html(data);
            document.getElementById("message").scrollTop = document.getElementById("message").scrollHeight;
        });
    }

    console.log(<%=routeID%>);
    setInterval(function () {
        getMessages()
    }, 3000);

    $("#message").on('keyup', function (e) {
        if (e.keyCode == 13) {
            sendMessage();
        }
    });

    function sendMessage() {
        $.get('/SendMessage/<%=userID%>/<%=routeID%>/' + document.getElementById("message").value, function () {
            document.getElementById('message').value = "";
        });
    }
</script>
</body>
</html>
