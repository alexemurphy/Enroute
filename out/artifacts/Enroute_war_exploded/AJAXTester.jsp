<%--
  Created by IntelliJ IDEA.
  User: Liam Pugh
  Date: 15/02/2019
  Time: 09:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>AJAX TEST</title>
    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
</head>
<body>
<script>
    function runAJAXCall() {
        $.get('/AJAX/1', function (data) {
            $('#AJAXElement').text(data);
        });
    }
</script>
<button type="submit" onclick="runAJAXCall()">RUN CALL</button>

<p>
    This is the place where a thingy will happen when you press the button:
</p>
<div id="AJAXElement"></div>
</body>
</html>
