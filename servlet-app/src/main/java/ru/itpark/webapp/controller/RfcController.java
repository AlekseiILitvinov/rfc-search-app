package ru.itpark.webapp.controller;

import ru.itpark.webapp.model.DocumentModel;
import ru.itpark.webapp.model.ResultModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public interface RfcController {

    List<DocumentModel> getAllAtPage(int page);

    boolean doSearch(String phrase);

    List<ResultModel> getAllResults();

    void handlePost(HttpServletRequest req) throws IOException, ServletException;

    void removeById(int id);

    int getTotalItems();

    void readResultsFile(String filename, PrintWriter writer);

    void populateParallel(int from, int to);
}
