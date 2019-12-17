package ru.itpark.webapp.servlet;

import lombok.val;
import ru.itpark.webapp.controller.RfcController;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class MainServlet extends HttpServlet {
    @Inject
    private RfcController controller;

    public MainServlet() {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String requestURI = req.getRequestURI();
        if (requestURI.equals("/")) {
            req.setAttribute("hasNextPage", controller.hasNextPage(0));
            req.setAttribute("totalItems", controller.getTotalItems());
            req.setAttribute("items", controller.getAllAtPage(0));
            req.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(req, resp);
        } else if (requestURI.equals("/page?")) {
            String uri = req.getRequestURI().substring(req.getContextPath().length());
            uri = uri.substring(uri.indexOf("?")+1);
            req.setAttribute("items", controller.getAllAtPage(Integer.parseInt(uri)));
            req.setAttribute("hasNextPage", controller.hasNextPage(Integer.parseInt(uri)));
            req.setAttribute("totalItems", controller.getTotalItems());
            req.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(req, resp);
        } else if (requestURI.startsWith("/search")) {
            String query = req.getQueryString();
            String phrase = query.substring("phrase=".length());
            req.setAttribute("isSearchStarted", controller.doSearch(phrase));
            req.getRequestDispatcher("WEB-INF/confirmation.jsp").forward(req, resp);
        } else if (requestURI.equals("/results")) {
            req.setAttribute("items", controller.getAllResults());
            req.getRequestDispatcher("WEB-INF/results.jsp").forward(req, resp);
        } else if (requestURI.equals("/populateASAP")) {
            controller.populate();
            req.setAttribute("items", controller.getAllAtPage(0));
            req.setAttribute("hasNextPage", controller.hasNextPage(0));
            req.setAttribute("totalItems", controller.getTotalItems());
            req.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(req, resp);
        } else {
            System.out.println("in routeGet");
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI().substring(req.getContextPath().length());
        String rootUrl = req.getContextPath().isEmpty() ? "/" : req.getContextPath();
        if (url.startsWith("/remove/")) {
            int id = Integer.parseInt(url.substring("/remove/".length()));
            controller.removeById(id);
            req.setAttribute("hasNextPage", controller.hasNextPage(0));
            req.setAttribute("totalItems", controller.getTotalItems());
            req.setAttribute("items", controller.getAllAtPage(0));
            resp.sendRedirect(rootUrl);
//            req.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(req, resp);
        } else {
            controller.handlePost(req);
            req.setAttribute("hasNextPage", controller.hasNextPage(0));
            req.setAttribute("totalItems", controller.getTotalItems());
            req.setAttribute("items", controller.getAllAtPage(0));
            req.getRequestDispatcher("/WEB-INF/frontpage.jsp").forward(req, resp);
        }
    }
}
