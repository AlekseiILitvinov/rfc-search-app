package ru.itpark.webapp.controller;

import ru.itpark.webapp.model.DocumentModel;
import ru.itpark.webapp.model.ResultModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface RfcController {

    List<DocumentModel> getAllAtPage(int page);

    boolean doSearch(String phrase);

    List<ResultModel> getAllResults();

    void handlePost(HttpServletRequest req) throws IOException, ServletException;

    void removeById(int id);

    boolean hasNextPage(int page);

    int getTotalItems();

    void populate();
}
