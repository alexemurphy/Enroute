<%@ page import="utils.Authentication" %>
<%@ page import="main.db.DBUpdates" %>
<%@ page import="main.db.DBHelper" %>
<%@ page import="java.sql.SQLException" %><%--
  Created by IntelliJ IDEA.
  User: Liam Pugh
  Date: 08/02/2019
  Time: 08:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String success = "Failed";
    try {
        if (session.getAttribute("sessionKey").equals(request.getParameter("sessionKey"))) {
            String newKey = Authentication.genNewSessionKey();
            session.setAttribute("sessionKey", newKey);
            Boolean DORP = request.getParameter("DORP").equals("Passenger");
            if (request.getParameter("DORP").equals("Passenger")) {
                if (new DBUpdates().createNewUser(request.getParameter("fname"), request.getParameter("lname"), request.getParameter("address"),
                        request.getParameter("email"), request.getParameter("phone"), DORP,
                        request.getParameter("password"), request.getParameter("payment"))) {
                    success = "Successful";
                }
                response.sendRedirect("/index.jsp?error=Sign Up " + success);
            } else {
                if (new DBUpdates().createNewUser(request.getParameter("fname"), request.getParameter("lname"), request.getParameter("address"),
                        request.getParameter("email"), request.getParameter("phone"), DORP,
                        request.getParameter("password"), request.getParameter("payment"))) {
                    if (new DBUpdates().createNewCar(request.getParameter("reg"), request.getParameter("color"), request.getParameter("make"), request.getParameter("model"), DBHelper.instance().getIDFromEmail(request.getParameter("email"))))
                        success = "Successful";
                }
                response.sendRedirect("/index.jsp?error=Sign Up " + success);
            }
        } else {
            response.sendRedirect("/index.jsp?error=Session Expired");
        }
    } catch (NullPointerException err) {
        response.sendRedirect("/index.jsp?error=Sign Up " + success);
    } catch (Exception err) {
        err.printStackTrace();
    }

    // To go elsewhere: response.sendRedirect("/name.jsp?sessionKey="+newKey);
%>

