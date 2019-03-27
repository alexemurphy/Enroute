<%@ page import="main.db.DBHelper" %><%--
  Created by IntelliJ IDEA.
  User: Liam Pugh
  Date: 14/02/2019
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String username = (String) session.getAttribute("username");
    int userPicID = DBHelper.instance().getProfilePicID(username);
%>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%=username%>
<img src="${pageContext.request.contextPath}/images/<%=userPicID%>">
</body>
</html>
