<%@ page import="ru.itpark.webapp.model.ResultModel" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META http-equiv="refresh" content="5;URL=<%= request.getContextPath()%>/results">
    <title>Title</title>
    <%@ include file="bootstrap-css.jsp" %>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col">
            <br>
            <h1>Search initiated</h1>
            <h3>You are redirected to the Results page</h3>
        </div>
    </div>
</div>
</body>
</html>
