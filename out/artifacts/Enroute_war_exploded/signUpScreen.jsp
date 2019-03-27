<%@ page import="utils.Authentication" %><%--
  Created by IntelliJ IDEA.
  User: Liam Pugh
  Date: 08/02/2019
  Time: 08:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String newKey = Authentication.genNewSessionKey();
    session.setAttribute("sessionKey", newKey);
    // To go elsewhere: response.sendRedirect("/name.jsp?sessionKey="+newKey);
%>
<html>
<head>
    <title>Sign Up!</title>
</head>
<body>
<script>
    function hideCarInfo() {
        var element = document.getElementById("carInfo");
        element.parentNode.removeChild(element);
    }

    function addElement(parentId, elementTag, elementId, html) {
        // Adds an element to the document
        var p = document.getElementById(parentId);
        var newElement = document.createElement(elementTag);
        newElement.setAttribute('id', elementId);
        newElement.innerHTML = html;
        p.appendChild(newElement);
    }

    function addCarInfo() {
        var html = "" +
            "<div id=\"carInfo\">\n" +
            "        <p>Make: <input type=\"text\" name=\"make\"></p>\n" +
            "        <p>Model: <input type=\"text\" name=\"model\"></p>\n" +
            "        <p>Colour: <input type=\"text\" name=\"color\"></p>\n" +
            "        <p>Reg-Plate: <input type=\"text\" name=\"reg\"></p>\n" +
            "    </div>"
        addElement("master", 'p', "2", html);
    }
</script>
<form action="/addToDBScripts/addUserToDB.jsp">
    <input type="hidden" name="sessionKey" value="<%=newKey%>">
    <p>First Name: <input type="text" name="fname"></p>
    <p>Last Name: <input type="text" name="lname"></p>
    <p>Address: <input type="text" name="address"></p>
    <p>Email: <input type="text" name="email"></p>
    <p>Phone: <input type="text" name="phone"></p>
    <p>Driver Or Passenger Account?:<br>
        <input type="radio" name="DORP" value="Driver" onclick="addCarInfo()"> Driver <br>
        <input type="radio" name="DORP" value="Passenger" onclick="hideCarInfo()"> Passenger <br>
    </p>
    <p>Password: <input type="password" name="password"></p>
    <p>Payment Account Number: <input type="text" name="payment"></p>
    <div id="master">
    </div>
    <input type="submit" value="Submit">
</form>
</body>
</html>
