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

    int userID = 0;
    try {
        userID = DBHelper.instance().getIDFromEmail((String) session.getAttribute("username"));
    } catch (Exception e) {
        e.printStackTrace();
    }
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

    <title>Enroute - Reviews</title>
</head>
<body>

<body style="background-color: rgb(0,0,0);">
<div class="rideSharersFound" style="width: 100%;height: 100%;">

    <button class="btn btn-primary" type="button" id="backButton"
            style="background-image: url('assets/img/left-arrow.png');background-color: rgba(255,255,255,0);width: 26px;border: 0;height: 31px;background-position: top;background-size: contain;background-repeat: no-repeat;padding-top: 0;position: absolute;z-index: 101;margin-top: 9px;margin-left: 9px;"></button>
    <div style="width: 100%;height: 44px;background-color: #eddc36;"></div>
    <div class="driversFound" style="height: 100%;width: 100%;background-color: #000000;z-index: 99;font-family: Roboto, sans-serif;color: rgb(183,183,183);font-weight: 300;overflow: auto;
        -webkit-overflow-scrolling: touch;">
        <div id="reviewsDIV" class="reviews1">

        </div>
        <div id="ratingDIV" class="submitRating">

        </div>

    </div>
</div>
<script src="assets/js/jquery.min.js"></script>
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script src="javascript/inobounce.js"></script>
<script>

    var pageLocation = 0;
    var routeID;
    var userIDreviewee;
    var starRating;

    init();

    function init() {
        document.getElementById("backButton").addEventListener("click", goBack);
        showReviews();
    }

    $(document).ready(function () {
        $(document).on('click', '.reviewCellDIV', function () { //clicking on rows
            hideOrShowClass(".reviews1", "none");
            hideOrShowClass(".submitRating", "block");
            userIDreviewee = $(this).attr('id');
            pageLocation = 1;
            ajaxShowSubmitRatingPage(userIDreviewee);
        });

        $("#ratingDIV").on("click", ".btn", function () {
            submitRating();
            pageLocation = 0;
            hideOrShowClass(".submitRating", "none");
            hideOrShowClass(".reviews1", "block");
        });
        $(document).on('click', '.fa', function () {
            $('.fa').siblings('input.rating-value').val($(this).data('rating'));
            return SetRatingStar();
        });
    });

    function goBack() {
        if (pageLocation === 0) {
            window.location.href = "/mapMainPage.jsp";
        }
        else if (pageLocation === 1) {
            hideOrShowClass(".submitRating", "none");
            hideOrShowClass(".reviews1", "block");
        }

    }

    function showReviews() {
        $.post('/AJAX/12', {userID: <%=userID%>}, function (data) {
            $('#reviewsDIV').html(data);
        });
    }

    function ajaxShowSubmitRatingPage(userIDreviewee) {
        $.post('/AJAX/13', {userIDreviewee: userIDreviewee}, function (data) {
            $('#ratingDIV').html(data);

        });
    }

    function submitRating() {

        $.post('/AJAX/15', {userIDreviewee: userIDreviewee, userID: <%=userID%>, rating: starRating}, function (data) {
            showReviews();
        });
    }

    function hideOrShowClass(classToChange, newDisplayStyle) {
        var elements = document.querySelectorAll(classToChange);
        for (var i = 0; i < elements.length; i++) {
            elements[i].style.display = newDisplayStyle;
        }
    }


    var SetRatingStar = function () {
        return $('.fa').each(function () {
            starRating = $('.fa').siblings('input.rating-value').val();
            if (parseInt($('.fa').siblings('input.rating-value').val()) >= parseInt($(this).data('rating'))) {
                return $(this).removeClass('fa-star-o').addClass('fa-star');
            } else {
                return $(this).removeClass('fa-star').addClass('fa-star-o');
            }
        });
    };


</script>
</body>

</html>
