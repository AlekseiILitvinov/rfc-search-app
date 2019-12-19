<%@ page import="ru.itpark.webapp.model.ResultModel" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Search results</title>
    <%@ include file="bootstrap-css.jsp" %>
</head>
<body>
<div class="container">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <span class="navbar-brand">Text Search v0.1</span>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="<%= request.getContextPath()%>">Home <span
                            class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath()%>/results">Results</a>
                </li>
            </ul>
            <form class="form-inline my-2 my-lg-0" method="POST" action="<%= request.getContextPath() %>search"
                  enctype="multipart/form-data" accept-charset="UTF-8">
                <input name="phrase" class="form-control mr-sm-2" type="search" placeholder="Search"
                       aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
            </form>
        </div>
    </nav>
    <br/>
    <h2>Search results</h2>

    <div class="row">
        <div class="col-md-12">
            <table class="table">
                <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Search query</th>
                    <th scope="col">Status</th>
                    <th scope="col">Action</th>
                </tr>
                </thead>
                <tbody>
                <% if (request.getAttribute("items") != null) { %>
                <% for (ResultModel item : (List<ResultModel>) request.getAttribute("items")) { %>
                <tr>
                    <th scope="row"><%=item.getId()%>
                    </th>
                    <th scope="row"><%=item.getSearchPhrase()%>
                    </th>
                    <td><%=item.getStatus()%>
                    </td>
                    <td>
                        <a href="<%= request.getContextPath() %>resultFile?<%=item.getUrl()%>"
                           class="btn btn-outline-primary <%=item.getStatus().equals("Done")?"":"disabled"%>"
                           >Download</a>
                    </td>
                </tr>
                <% } %>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
