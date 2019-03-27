var pageLocation;
var originLat, originLng, destinationLat, destinationLng;
var routeIDdriver;
var driverFindInterval;
//Page location for back buttons screens below 10 are before choosing driver or passenger
//10 - 20 is passenger
//20 - 30 is driver


/**
 *
 * Things that need changing before release
 *
 * Profile pictures on drivers found are all the same (change for each one)
 * Distance is set at loads of miles
 * There is no time limits of rides
 *
 */

function showMainMapPage(yes) {
    console.log("HU")
    if (yes) {
        hideOrShowClass('.mapPage', "block");
        backButton.style.display = "none";
        setMapHeight("100%");
        setBottomDivHeight("0%");
        pageLocation = 0;
    }
    else {
        hideOrShowClass('.mapPage', "none");
    }
}

function showChooseUserType(yes) {
    if (yes) {
        hideOrShowClass('.chooseUserType', "block");
        backButton.style.display = "block";
        backButton.style.backgroundImage = "url('assets/img/left-arrow.png')";
        setMapHeight("77%");
        setBottomDivHeight("23%");
        pageLocation = 1;
    }
    else {
        hideOrShowClass('.chooseUserType', "none");
    }
}

function showDriversFound(yes) {
    if (yes) {
        hideOrShowClass('.driversFound', "block");
        backButton.style.display = "block";
        backButton.style.backgroundImage = "url('assets/img/left-arrow.png')";
        setMapHeight("55%");
        setBottomDivHeight("45%");
        pageLocation = 10;
    }
    else {
        hideOrShowClass('.driversFound', "none");
    }
}

function showRideSharersFound(yes) {
    if (yes) {
        hideOrShowClass('.rideSharersFound', "block");
        backButton.style.display = "block";
        backButton.style.backgroundImage = "url('assets/img/left-arrow.png')";
        setMapHeight("0%");
        setBottomDivHeight("100%");
        pageLocation = 20;
    }
    else {
        hideOrShowClass('.rideSharersFound', "none");
    }
}

function showRequestAndPay(yes) {
    if (yes) {
        hideOrShowClass('.requestAndPay', "block");
        backButton.style.display = "block";
        backButton.style.backgroundImage = "url('assets/img/left-arrow-yellow.png')";
        setMapHeight("0%");
        setBottomDivHeight("100%");
        pageLocation = 21;
    }
    else {
        hideOrShowClass('.requestAndPay', "none");
    }
}

function showDriversInfo(yes) {
    if (yes) {
        hideOrShowClass('.driversInfo', "block");
        backButton.style.display = "block";
        backButton.style.backgroundImage = "url('assets/img/letter-x.png')";
        setMapHeight("55%");
        setBottomDivHeight("45%");
        pageLocation = 22;
    }
    else {
        hideOrShowClass('.driversInfo', "none");
    }
}


function hideOrShowClass(classToChange, newDisplayStyle) {
    var elements = document.querySelectorAll(classToChange);

    for (var i = 0; i < elements.length; i++) {
        elements[i].style.display = newDisplayStyle;
    }
}

function setMapHeight(height) {
    document.getElementById('map').style.height = height;
}

function setBottomDivHeight(height) {
    document.getElementById('bottomDiv').style.height = height;
}

function showChatButton(routeID) {
    document.getElementById('goToChat').style.display = "block";
    document.getElementById('goToChat').addEventListener("click", function () {
        window.location.href = "/chatScreen?routeid=" + routeID.toString();
    })
}


function ajaxFindDrivers() {

    $.post('/AJAX/1', {
        startLat: originLat,
        startLng: originLng,
        endLat: destinationLat,
        endLng: destinationLng
    }, function (data) {
        $('#driversDIV').html(data);

    });
}

function ajaxFindRideSharers(userIDin, timeTillDepartin) {

    $.post('/AJAX/2', {
        startLat: originLat,
        startLng: originLng,
        endLat: destinationLat,
        endLng: destinationLng,
        userID: userIDin,
        timeTillDepart: timeTillDepartin
    }, function (data) {
        returnRouteID(data);
    });
}

function ajaxRequestandPay(routeIDin) {

    $.post('/AJAX/3', {routeID: routeIDin}, function (data) {
        $('#requestAndPayDIV').html(data);
    });
}

function ajaxDriversInfo(routeIDin) {

    $.post('/AJAX/4', {routeID: routeIDin}, function (data) {
        $('#driversInfoDIV').html(data);
    });
}

function ajaxRemovePassenger(routeIDin, userIDin) {

    $.post('/AJAX/5', {routeID: routeIDin, userID: userIDin}, function (data) {
        alert(data);
    });

}

function ajaxDriversMarkers() {

    $.post('/AJAX/6', {
        startLat: originLat,
        startLng: originLng,
        endLat: destinationLat,
        endLng: destinationLng
    }, function (data) {
        var jsonObject = JSON.parse(data);
        plotMarkersRS(jsonObject);
    });
}

function ajaxChosenDriverMarker(routeID) {
    $.post('/AJAX/7', {routeID: routeID}, function (data) {
        var jsonObject = JSON.parse(data);
        plotMarkersRS(jsonObject);
    });
}

function ajaxFindRideSharersRepeat() {
    console.log(routeIDdriver);
    $.post('/AJAX/8', {routeID: routeIDdriver}, function (data) {
        $('#rideSharersDIV').html(data);
    });
}

function ajaxDepart(routeIDin) {

    $.post('/AJAX/9', {routeID: routeIDin}, function (data) {

    });
}

function ajaxCancelRoute(routeIDdriverin) {
    console.log(routeIDdriver);
    $.post('/AJAX/14', {routeID: routeIDdriverin}, function (data) {
    });
}


function findARide() {

    showChooseUserType(false);
    showDriversFound(true);
    autocompleteHandler.directionsDisplay.setMap(null);
    ajaxFindARide();
    driverFindInterval = setInterval(ajaxFindARide, 3000);
    map.zoom = 10;
}

function ajaxFindARide() {
    ajaxFindDrivers();
    ajaxDriversMarkers();
}


function routeFound() {

    showMainMapPage(false);

    /**

     I know this is horrible, but I can't work out how to do this elegantly, and I've wasted too much time on it already,
     so fix if you can.

     Also in theory will crash if user clicks button before it has finished getting the lat and lng

     */

    geocoder = new google.maps.Geocoder();
    geocoder.geocode({'placeId': autocompleteHandler.originPlaceId}, function (results, status) {
        if (status === 'OK') {
            originLat = results[0].geometry.location.lat();
            originLng = results[0].geometry.location.lng();
        } else {
            window.alert('Geocoder failed due to: ' + status);
        }
    });

    geocoder.geocode({'placeId': autocompleteHandler.destinationPlaceId}, function (results, status) {
        if (status === 'OK') {
            destinationLat = results[0].geometry.location.lat();
            destinationLng = results[0].geometry.location.lng();
        } else {
            window.alert('Geocoder failed due to: ' + status);
        }
    });

}
