<%@ page import="ru.itpark.webapp.model.DocumentModel" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Rfc Search Tool</title>
    <%@ include file="bootstrap-css.jsp" %>
</head>
<body>

<div class="container">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <span class="navbar-brand" >Text Search v0.1</span>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item active">
                    <a class="nav-link" href="<%= request.getContextPath()%>">Home <span class="sr-only">(current)</span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= request.getContextPath()%>/results">Results</a>
                </li>
            </ul>
            <form class="form-inline my-2 my-lg-0" method="GET" action="<%= request.getContextPath() %>search">
                <input name="phrase" class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
            </form>
        </div>
    </nav>

    <br/>


    <div class="row">
        <div class="col">
            <h1>Catalog</h1>
            <h3>Number of items: <%=request.getAttribute("totalItems")%></h3>
            <div class="row mt-3">
                <div class="col">
                    <form class="mt-3" method="post" action="<%= request.getContextPath() %>" enctype="multipart/form-data">
                        <fieldset>
                            <div class="form-horizontal">
                                <div class="form-group">
                                    <div class="row">
                                        <div class="col-md-12">
                                            <div class="input-group">
                                                    <input type="file" class="form-control" id="file" name="file" class="form-control form-control-sm" accept="txt">
                                                    <div class="input-group-btn">
                                                        <input type="submit" value="Upload" class="rounded-0 btn btn-primary">
                                                    </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <br/>
    <div class="row">
        <div class="col-md-12">
        <% if (request.getAttribute("items") != null) { %>
        <% for (DocumentModel item : (List<DocumentModel>) request.getAttribute("items")) { %>
        <ul class="col-12 list-group list-unstyled">
            <li class="list-group-item ">
                <form class="form-inline" action="<%= request.getContextPath() %>/remove/<%= item.getId() %>" method="POST">
                    <div class="form-group">
                        <h6><%=item.getName()%></h6>
                    </div>
                    <div class="form-group pull-right">
                        <button type="submit" class="btn btn-xs mb-2">Delete</button>
                    </div>

                </form>
                <small>Size: <%=item.getSize()%></small>
                <small>Upload date: <%=item.getUploadDate()%></small>

<%--                <div class="col-12">--%>
<%--                    <span><h6><%=item.getName()%></h6></span>--%>
<%--                    <span>--%>
<%--                        <form class="text-right mb-3" action="<%= request.getContextPath() %>/remove/<%= item.getId() %>" method="POST">--%>
<%--                            <button type="submit" class="btn btn-xs">Delete</button>--%>
<%--                        </form>--%>
<%--                    </span>--%>
<%--                </div>--%>
<%--                <small>Size: <%=item.getSize()%></small>--%>
<%--                <small>Upload date: <%=item.getUploadDate()%></small>--%>
            </li>
        </ul>
        <% } %>
        <% } %>
        </div>
    </div>

    <div class="row justify-content-center">
        <div class="col-12">
            <form class="card card-sm" method="GET" action="<%= request.getContextPath() %>search">
                <div class="card-body row no-gutters align-items-center">
                    <div class="col">
                        <input name="phrase" class="form-control form-control-lg form-control-borderless" type="search"
                               placeholder="Search name and contents">
                    </div>
                    <div class="col-auto">
                        <button class="btn btn-lg btn-success" type="submit">Search</button>
                    </div>
                </div>
            </form>
        </div>
    </div>

</div>

<%@ include file="bootstrap-scripts.jsp" %>
</body>
</html>
