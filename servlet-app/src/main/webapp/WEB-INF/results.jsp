<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search results</title>
</head>
<body>
<br/>
<h2>Search results</h2>
<p>
    <% if (request.getAttribute("isSearchStarted") != null) { %>
    <%= request.getAttribute("isSearchStarted")%>
    <% } else { %>
    <%= "null"%>
    <% } %>
</p>
</body>
</html>
