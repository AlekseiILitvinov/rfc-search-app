package ru.itpark.webapp.servlet;

import ru.itpark.webapp.controller.RfcController;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Singleton
public class MainServlet extends HttpServlet {
    @Inject
    private RfcController controller;

    public MainServlet() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String requestURI = req.getRequestURI();
        if (requestURI.equals("/")) {
            final HttpSession session = req.getSession();
            displayFront(req, resp, 1);
        } else if (requestURI.startsWith("/page")) {
            int page = Integer.parseInt(req.getQueryString());
            displayFront(req, resp, page);
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
            displayFront(req, resp, 1);
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
            resp.sendRedirect(rootUrl);
        } else {
            controller.handlePost(req);
            displayFront(req, resp, 1);
        }
    }

    private void displayFront(HttpServletRequest req, HttpServletResponse resp, int page) throws ServletException, IOException {
        if (page == 1) {
            req.setAttribute("prevPage", "false");
        } else {
            req.setAttribute("prevPage", "" + (page - 1));
        }

        int totalItems = controller.getTotalItems();
        int itemAtNext = page * 10 + 1;
        if (itemAtNext > totalItems) {
            req.setAttribute("nextPage", "false");
        } else {
            req.setAttribute("nextPage", "" + (page + 1));
        }
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("items", controller.getAllAtPage(page));
        req.getRequestDispatcher("/WEB-INF/frontpage_new.jsp").forward(req, resp);
    }
}
