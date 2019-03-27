<%@ page import="utils.Authentication" %>
<%@ page import="main.db.DBHelper" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="main.objects.HistorySegment" %>
<%@ page import="main.objects.ReviewListElement" %>
<%@ page import="main.objects.User" %><%--
  Created by IntelliJ IDEA.
  User: Liam Pugh
  Date: 08/02/2019
  Time: 08:57
  To change this template use File | Settings | File Templates.
--%>
<%
    // Session Authentication
    String sessionID = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null)
        for (Cookie cookie : cookies) if (cookie.getName().equals("sessionToken")) sessionID = cookie.getValue();
    if (sessionID == null || !sessionID.equals(session.getAttribute("sessionToken")))
        response.sendRedirect("/index.jsp?error=Session Expired, please log in again");

    Integer[] usersRoute = null;
    int userID = -1;
    boolean isDriver = false;
    User user = null;
    String profilePictureString = "";
    String name = "";
    ArrayList<ReviewListElement> reviewListElements = null;
    try {
        usersRoute = DBHelper.instance().isUserCurrentlyOnRoute((String) session.getAttribute("username"));
        userID = DBHelper.instance().getIDFromEmail((String) session.getAttribute("username"));
        reviewListElements = DBHelper.instance().showLeaveReviewPrompt(userID);
        user = DBHelper.instance().getUserInfo(userID);
        name = user.getFirstName() + " " + user.getLastName();
        profilePictureString = "images/" + Integer.toString(user.getProfilePicture());
        isDriver = DBHelper.instance().isDriver(userID);
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
<html>
<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, maximum-scale=1,user-scalable=0, shrink-to-fit=no">
    <title>Enroute</title>
    <link rel="stylesheet" href="assets/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/fonts/font-awesome.min.css">
    <link rel="stylesheet"
          href="https://fonts.googleapis.com/css?family=Roboto:100,100i,300,300i,400,400i,500,500i,700,700i,900,900i">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/css/Sidebar-Menu.css">
    <link rel="stylesheet" type="text/css" href="assets/css/addtohomescreen.css">
    <link rel="stylesheet" href="assets/css/Sidebar-Menu-1.css">
    <link rel="stylesheet" href="assets/css/styles.css">
    <link rel="apple-touch-icon" href="single-page-icon.png">
    <link rel="icon" type="image/png" sizes="16x16" href="assets/img/favicon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="assets/img/favicon32.png">
    <link rel="icon" type="image/png" sizes="180x180" href="assets/img/ios.png">
    <link rel="icon" type="image/png" sizes="192x192" href="assets/img/android.png">
    <link rel="icon" type="image/png" sizes="512x512" href="assets/img/androidsplash.png">

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

    <link rel="manifest" href="manifest.json">
    <link rel="stylesheet" href="assets/fonts/font-awesome.min.css">
    <link rel="stylesheet" href="assets/fonts/ionicons.min.css">
    <link rel="stylesheet" href="assets/fonts/material-icons.min.css">


</head>

<body>
<div id="wrapper" class="mapPage" style="position: absolute; z-index: 99;">
    <div id="sidebar-wrapper">
        <ul class="sidebar-nav" style="margin-top: 50px;">

            <li><img src="<%=profilePictureString%>"
                     style="display:block; margin-left:auto; margin-right:auto; object-fit:cover; border-radius: 50%;"
                     height="100px" width="100px"></li>
            <li style="margin-top: 11px; font-family: Roboto, sans-serif;font-size: 22px;font-weight:300;color: #a59fa1;">
                <p style="width: 90%; text-align: center"><%=name%>
                </p></li>
            <li>
                <a style="margin-top: 80px; font-family: Roboto, sans-serif;font-size: 25px;font-weight:300;color: #eddc36;"
                   href="${pageContext.request.contextPath}/history.jsp"
                >History </a></li>
            <li>
                <a style="margin-top: 25px;font-family: Roboto, sans-serif;font-size: 25px;font-weight:300;color: #eddc36;"
                   href="${pageContext.request.contextPath}/reviews.jsp"
                >Reviews</a></li>
        </ul>
    </div>
    <div class="page-content-wrapper">
        <div class="container-fluid" style="padding-left: 5px;padding-top: 7px;"><a class="btn btn-link" role="button"
                                                                                    href="#menu-toggle"
                                                                                    id="menu-toggle"><i
                class="fa fa-bars" style="color: rgb(0,0,0);font-size: 20px;"></i></a></div>
    </div>
</div>

<button class="btn btn-primary" type="button" id="backButton"
        style="background-image: url(&quot;assets/img/left-arrow.png&quot;);background-color: rgba(255,255,255,0);width: 26px;border: 0;height: 31px;background-position: top;background-size: contain;background-repeat: no-repeat;padding-top: 0;position: absolute;z-index: 101;margin-top: 9px;margin-left: 9px;"></button>
<div style="display: none; width: 100%;height: 44px;background-color: #eddc36; position: absolute; top:0; z-index: 100"
     class="rideSharersFound"></div>
<div class="mapPage"
     style="width: 90%;height: 90px;position: absolute;z-index: 2;background-color: #eddc36;margin-right: auto;margin-left: auto;left: 0;right: 0;-webkit-box-shadow: 0px 0px 18px 0px rgba(0,0,0,0.25);border-radius: 5px;margin-top: 45px;">
    <div style="width: 10%;height: 100%;float: left;"><img src="assets/img/destination.png"
                                                           style="margin-top: 10px;margin-left: 7px;height: 75%;"></div>
    <div style="height: 100%;width: 90%;margin-right: 0px;margin-left: auto;">
        <div style="height: 50%;width: 100%;"><input type="text" id="origin-input"
                                                     style="height: 100%;float: left;background-color: rgba(255,255,255,0);border: 0;font-family: Roboto, sans-serif;color: rgb(0,0,0);font-size: 17px;font-weight: 300;padding-left: 10px;width: 70%;"
                                                     placeholder="Origin">
            <div style="height: 100%;width: 45px;margin-left: auto;margin-right: 0px;display: flex;align-items: center;justify-content: center;">
                <button class="btn btn-primary" type="button" id="currentLocationButton" onclick="getCurrentLocation()"
                        style="border: 0;background-image: url(&quot;assets/img/crosshair-target-interface.png&quot;);background-size: contain;background-repeat: no-repeat;background-color: rgba(51,122,183,0);width: 30px;height: 30px;opacity: 1;"></button>
            </div>
        </div>
        <div style="height: 50%;width: 100%;"><input type="text" id="destination-input"
                                                     style="float: left;height: 100%;background-color: rgba(255,255,255,0);border: 0;font-weight: 300;font-size: 17px;font-family: Roboto, sans-serif;padding-left: 10px;width: 70%"
                                                     placeholder="Destination">
            <div style="height: 100%;width: 45px;margin-right: 0px;margin-left: auto;display: flex;justify-content: center;align-items: center;">
                <button class="btn btn-primary" type="button" id="swapButton"
                        style="width: 30px;height: 30px;background-color: rgba(51,122,183,0);border: 0;background-image: url(&quot;assets/img/sort.png&quot;);background-size: contain;background-repeat: no-repeat;opacity: 1;"></button>
            </div>
        </div>
    </div>
</div>
<div id="map"
     style="width: 100%;height: 100%;position: absolute; transition: height 1s; -webkit-transition: height 1s;"></div>

<div id="alertWindow" class="justify-content-center align-items-center" style="display:none; width: 70%;height: 250px;z-index: 150;position: absolute;background-color: #000000;margin-top: 70px;-webkit-box-shadow: 0px 0px 18px 0px rgba(0,0,0,0.75);left: 50%;margin-left: -35%;">
    <div><button class="btn btn-primary" type="button" id="alertExitButton" style="background-color:rgba(255,255,255,0);background-image:url('assets/img/close.png');background-size:cover;background-repeat:no-repeat;height:27px;width:27px;margin-top:7px;margin-left:7px;border:0;"></button>
        <div
                class="d-flex d-sm-flex flex-column align-items-center justify-content-sm-center align-items-sm-center">
            <p class="text-center" style="  color: #eddc36;
  font-family: Roboto, sans-serif;
  font-weight: 300;
  margin-top: 10px;
  margin-left: 20px;
  margin-right: 20px;
">Please select how long until you intend to depart.</p>
            <div class="d-flex d-sm-flex align-items-center align-items-sm-center"><input type="number" value="30" min="15" max="45" step="2" class="form-control-lg" id="minuteSpinner" style="  background-color: rgb(0,0,0);
  font-size: 24px;
  color: #eddc36;
  border: 0;
  height: 50px;
" />
                <p style="  float: left;
  color: #eddc36;
  font-family: Roboto, sans-serif;
  font-weight: 300;
  margin-top: 8px;
">minutes</p>
            </div><button class="btn btn-primary d-sm-flex" type="button" id="alertFindRidersharersButton" style="  bottom: 0;
  background-color: #eddc36;
  color: rgb(0,0,0);
  font-family: Roboto, sans-serif;
  font-weight: bold;
  border-radius: 0;
  width: 191px;
  height: 49px;
  font-size: 18px;
  margin-top: 20px;
">FIND RIDESHARERS</button></div>
    </div>
</div>

<button class="btn btn-primary" class="mapPage" type="button" id="goToChat"
        style="display: none;width: 60px;height: 60px;border: 0;border-radius: 50%;background-color: #eddc36;-webkit-box-shadow: 0px 0px 18px 0px rgba(0,0,0,0.5);position: absolute;margin-top: auto;bottom: 0;margin-bottom: 23px;right: 0;margin-right: 22px;">
    <i class="icon ion-chatbubble" style="color: rgb(0,0,0);font-size: 28px;"></i></button>

<div id="leaveReviewButtonDIV"
     style="display: none; position: absolute;margin-top: auto;bottom: 0;margin-bottom: 23px;left: 0;margin-right: auto;margin-left: 22px;">
    <div class="d-flex justify-content-center"
         style="right: 0;height: 20px;width: 20px;margin-left: auto;background: red;border-radius: 50%;position: absolute;margin-top: -6px;margin-right: -6px;">
        <p id="numberOfReviews" style="font-family: Roboto, sans-serif;color: rgb(255,255,255);font-weight: bold;">0</p>
    </div>
    <button class="btn btn-primary" type="button" id="leaveReviewButton"
            style="width: 60px;height: 60px;border: 0;border-radius: 50%;background-color: #eddc36;-webkit-box-shadow: 0px 0px 18px 0px rgba(0,0,0,0.5);">
        <i class="material-icons" style="color: rgb(0,0,0);font-size: 28px;">rate_review</i></button>
</div>

<div id="bottomDiv"
     style="overflow: auto; -webkit-overflow-scrolling: touch; bottom: 0;height: 0%;position: absolute;width: 100%;background-color: #000000;z-index: 99;-webkit-box-shadow: 0px 0px 18px 0px rgba(0,0,0,0.75);  transition: height 1s; -webkit-transition: height 1s;">
    <div class="driversFound" style="display: none">
        <div id="driversDIV"
             style="height: 100%; width: 100%; display: block;align-items: center;justify-content: center;">
            <img src="assets/img/loading.gif" style="width: 57px;"></div>

    </div>

    <div class="requestAndPay" style="display: none">
        <div id="requestAndPayDIV"
             style="height: 100%; width: 100%; display: flex;align-items: center;justify-content: center;">
            <img src="assets/img/loading.gif" style="width: 57px;">
        </div>
    </div>

    <div class="driversInfo" style="display: none">
        <div id="driversInfoDIV"
             style="height: 100%; width: 100%; display: flex;align-items: center;justify-content: center;">
            <img src="assets/img/loading.gif" style="width: 57px;">
        </div>
    </div>

    <div class="rideSharersFound" style="display: none">
        <div id="rideSharersDIV"
             style="height: 100%; width: 100%; display: flex;align-items: center;justify-content: center; padding-top: 44px;">
            <img src="assets/img/loading.gif" style="width: 57px;"></div>
    </div>

    <div class="chooseUserType" style="display: none">
        <div style="height: 6.6%;"></div>
        <button class="btn btn-primary" type="button" id="findRidesharersButton" onclick="showAlert()"
                style="display: none;margin-right: auto;border-radius: 0px;height: 40%;border: 0;margin-bottom: auto;width: 90%;max-width: 320px;margin-left: auto;background-color: #eddc36;font-family: Roboto, sans-serif;font-weight: 700;font-size: 23px;color: rgb(0,0,0);margin-top: auto;">
            FIND RIDESHARERS
        </button>
        <div style="height: 6.6%;"></div>
        <button class="btn btn-primary" type="button" id="findARideButton" onclick="findARide()"
                style="margin-right: auto;display: block;margin-top: auto;border-radius: 0px;height: 40%;border: 0;margin-bottom: auto;width: 90%;max-width: 320px;margin-left: auto;background-color: #eddc36;font-family: Roboto, sans-serif;font-weight: 700;font-size: 24px;color: rgb(0,0,0);">
            FIND A RIDE
        </button>
    </div>

</div>

<script src="assets/js/jquery.min.js"></script>
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/Sidebar-Menu.js"></script>
<script src="javascript/inobounce.js"></script>

<script>
    // This example requires the Places library. Include the libraries=places
    // parameter when you first load the API. For example:
    // <script
    // src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">
    var map, currentPos, autocompleteHandler, routeIDpassenger;
    var markerArray = [];
    var riderSharerInterval;
    var routeIDdriver;

    var backButton = document.getElementById("backButton");
    backButton.addEventListener("click", goBack);
    backButton.style.display = "none";

    document.getElementById("swapButton").addEventListener("click", swapLocation);

    checkReviews();
    checkUserType();

    function checkUserType(){
        if(<%=isDriver%>){
            document.getElementById("findRidesharersButton").style.display = "block";
        }
    }

    function checkReviews() {
        if (<%=reviewListElements.size() > 0%>) {
            document.getElementById("leaveReviewButtonDIV").style.display = "block";
            document.getElementById("numberOfReviews").innerHTML = <%=Integer.toString(reviewListElements.size())%>;
            document.getElementById("leaveReviewButton").addEventListener("click", leaveReviewRedirect);
        }
    }

    function checkIfOnRoute() {
        if (<%=(usersRoute[0] == 1)%>) {
            showMainMapPage(false);
            showRideSharersFound(true);
            routeIDdriver = <%=Integer.toString(usersRoute[1])%>;
            riderSharerInterval = setInterval(ajaxFindRideSharersRepeat, 3000);
        }
        else if (<%=(usersRoute[0] == 2)%>) { //passenger
            showMainMapPage(false);
            showDriversInfo(true);
            ajaxDriversInfo(<%=usersRoute[1]%>);
        }
        else if (<%=(usersRoute[0] == 3)%>) { //driver - departed
            showChatButton(<%=usersRoute[1]%>);
        }
        else {
            //do nothing
        }
    }

    function goBack() {
        switch (pageLocation) {
            case 1:
                showChooseUserType(false);
                showMainMapPage(true);
                autocompleteHandler.directionsDisplay.setMap(null);
                break;
            case 10:
                showDriversFound(false);
                showMainMapPage(true);
                clearInterval(driverFindInterval);
                removeMarkers();
                break;
            case 20:
                if (confirm("Are you sure you want to cancel this ride?")) {
                    console.log(routeIDdriver);
                    ajaxCancelRoute(routeIDdriver);
                    showRideSharersFound(false);
                    clearInterval(riderSharerInterval);
                    showMainMapPage(true);
                }
                else {
                }
                break;
            case 21:
                showRequestAndPay(false);
                showDriversFound(true);
                break;
            case 22:
                if (confirm("Are you sure you want to leave this ride? You may still be charged.")) {
                    showDriversInfo(false);
                    showMainMapPage(true);
                    ajaxRemovePassenger(routeIDpassenger, <%=DBHelper.instance().getIDFromEmail((String) session.getAttribute("username")) %>);
                    removeMarkers();
                }
                else {
                }
                break;
        }
    }

    function showAlert(){

        document.getElementById("alertWindow").style.display = "block";
        document.getElementById("alertExitButton").addEventListener("click", function(){
            document.getElementById("alertWindow").style.display = "none";
        });
        document.getElementById("alertFindRidersharersButton").addEventListener("click", function(){
            document.getElementById("alertWindow").style.display = "none";
            findARidesharer(document.getElementById("minuteSpinner").value);
        })

    }

    function findARidesharer(timeTillDepart) {
        showChooseUserType(false);
        showRideSharersFound(true);
        autocompleteHandler.directionsDisplay.setMap(null);
        ajaxFindRideSharers(<%=DBHelper.instance().getIDFromEmail((String) session.getAttribute("username"))%>, timeTillDepart);
    }

    function returnRouteID(data) {
        routeIDdriver = data;
        riderSharerInterval = setInterval(ajaxFindRideSharersRepeat, 3000);
    }

    $(document).ready(function () {
        $("#driversDIV").on('click', 'tr', function () { //clicking on rows
            showDriversFound(false);
            showRequestAndPay(true);
            clearInterval(driverFindInterval);
            routeIDpassenger = $(this).attr('id');
            ajaxRequestandPay(routeIDpassenger);
            removeMarkers();
        });

        $("#requestAndPayDIV").on("click", ".btn", function () {
            showRequestAndPay(false);
            showDriversInfo(true);
            ajaxDriversInfo(routeIDpassenger);
            ajaxChosenDriverMarker(routeIDpassenger);
        });
        $("#rideSharersDIV").on("click", ".btn", function () {
            ajaxDepart(routeIDdriver);
            window.location.href = "/chatScreen.jsp?routeid=" + routeIDdriver.toString();
        });
        addToHomescreen();
    });


    function plotMarkersRS(jsonObject) {
        console.log(jsonObject);

        for (i in jsonObject) {

            var pos = {
                lat: jsonObject[i].latitude,
                lng: jsonObject[i].longitude
            };

            var icon = {
                url: jsonObject[i].picture, // url
                scaledSize: new google.maps.Size(45, 45), // size
                origin: new google.maps.Point(0, 0), // origin
                anchor: new google.maps.Point(0, 0) // anchor
            };

            markerArray.push(new google.maps.Marker({
                position: pos,
                map: map,
                icon: icon
            }))
        }
        map.setCenter(pos);
        map.setZoom(18);

    }

    function removeMarkers() {
        for (i in markerArray) {
            markerArray[i].setMap(null);
        }
    }

    function leaveReviewRedirect() {
        window.location.href = "/reviews.jsp";
    }

    function initMap() {
        showMainMapPage(true);
        map = new google.maps.Map(document.getElementById('map'), {
            mapTypeControl: false,
            center: {lat: -33.8688, lng: 151.2195},
            zoom: 15,
            zoomControl: false,
            streetViewControl: false,
            fullscreenControl: false
        });

        // Try HTML5 geolocation.
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(function (position) {
                currentPos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };

                var icon = {
                    url: "assets/img/currentLocationMarker.png", // url
                    scaledSize: new google.maps.Size(35, 35), // size
                    origin: new google.maps.Point(0, 0), // origin
                    anchor: new google.maps.Point(0, 0) // anchor
                };

                var currentLocationMarker = new google.maps.Marker({
                    position: currentPos,
                    map: map,
                    icon: icon
                });
                map.setCenter(currentPos);
                document.getElementById('origin-input').value = 'Current Location';

                var geocoder = new google.maps.Geocoder; //get place_ID of current location
                geocoder.geocode({'location': currentPos}, function (results, status) {
                    if (status === google.maps.GeocoderStatus.OK) {
                        if (results[1]) {
                            autocompleteHandler.originPlaceId = results[1].place_id;
                        } else {
                            window.alert('No results found');
                        }
                    } else {
                        window.alert('Geocoder failed due to: ' + status);
                    }
                });
            }, function () {

            });
        } else {
            // Browser doesn't support Geolocation
            handleLocationError(false, infoWindow, map.getCenter());
        }
        autocompleteHandler = new AutocompleteDirectionsHandler(map);
        checkIfOnRoute();
    }

    function swapLocation() {

        var tempLocationPlaceID = autocompleteHandler.originPlaceId;
        autocompleteHandler.originPlaceId = autocompleteHandler.destinationPlaceId;
        autocompleteHandler.destinationPlaceId = tempLocationPlaceID;

        var tempLocationValue = document.getElementById('origin-input').value;
        document.getElementById('origin-input').value = document.getElementById('destination-input').value;
        document.getElementById('destination-input').value = tempLocationValue;

    }

    function getCurrentLocation() {

        if (navigator.geolocation) { //Get current location
            navigator.geolocation.getCurrentPosition(function (position) {
                currentPos = {
                    lat: position.coords.latitude,
                    lng: position.coords.longitude
                };
            }, function () {
                handleLocationError(true, infoWindow, map.getCenter());
            });
        }

        document.getElementById('origin-input').value = 'Current Location';

        var geocoder = new google.maps.Geocoder; //get place_ID of current location
        geocoder.geocode({'location': currentPos}, function (results, status) {
            if (status === google.maps.GeocoderStatus.OK) {
                if (results[1]) {
                    autocompleteHandler.originPlaceId = results[1].place_id;
                } else {
                    window.alert('No results found');
                }
            } else {
                window.alert('Geocoder failed due to: ' + status);
            }
        });
        autocompleteHandler.route();

    }

    /**
     * @constructor
     */
    function AutocompleteDirectionsHandler(map) {
        this.map = map;
        this.originPlaceId = null;
        this.destinationPlaceId = null;
        this.travelMode = 'DRIVING';
        this.directionsService = new google.maps.DirectionsService;
        var polylineOptions = new google.maps.Polyline({
            strokeColor: '#000000',
            strokeOpacity: 1.0,
            strokeWeight: 5
        });
        this.directionsDisplay = new google.maps.DirectionsRenderer({
            map: map,
            suppressMarkers: true,
            polylineOptions: polylineOptions
        });


        var originInput = document.getElementById('origin-input');
        var destinationInput = document.getElementById('destination-input');


        var originAutocomplete = new google.maps.places.Autocomplete(originInput);
        // Specify just the place data fields that you need.
        originAutocomplete.setFields(['place_id']);

        var destinationAutocomplete =
            new google.maps.places.Autocomplete(destinationInput);
        // Specify just the place data fields that you need.
        destinationAutocomplete.setFields(['place_id']);


        this.setupPlaceChangedListener(originAutocomplete, 'ORIG');
        this.setupPlaceChangedListener(destinationAutocomplete, 'DEST');

    }


    AutocompleteDirectionsHandler.prototype.setupPlaceChangedListener = function (autocomplete, mode) {
        var me = this;
        autocomplete.bindTo('bounds', this.map);

        autocomplete.addListener('place_changed', function () {
            var place = autocomplete.getPlace();

            if (!place.place_id) {
                window.alert('Please select an option from the dropdown list.');
                return;
            }
            if (mode === 'ORIG') {
                me.originPlaceId = place.place_id;
            } else {
                me.destinationPlaceId = place.place_id;
            }
            me.route();
        });
    };

    AutocompleteDirectionsHandler.prototype.route = function () {
        if (!this.originPlaceId || !this.destinationPlaceId) {
            return;
        }
        var me = this;

        this.directionsService.route(
            {
                origin: {'placeId': this.originPlaceId},
                destination: {'placeId': this.destinationPlaceId},
                travelMode: this.travelMode
            },
            function (response, status) {
                showMainMapPage(false);
                showChooseUserType(true);
                if (status === 'OK') {
                    me.directionsDisplay.setDirections(response);
                    me.directionsDisplay.setMap(map);
                    routeFound();
                } else {
                    window.alert('Directions request failed due to ' + status);
                }
            });
    };


</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDHEu6l_OvI7aCHQ0jyydKvBw6qD7OM2Ko&libraries=places&callback=initMap"
        async defer></script>

<script type="text/javascript" src="javascript/mainMapScreen.js"></script>
<script type="text/javascript" src="javascript/addtohomescreen.js"></script>

<%--<form action="LogoutServlet" method="post">
    <input type="submit" value="Logout" >
</form>--%>

</body>
</html>


