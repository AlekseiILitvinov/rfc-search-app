package ru.itpark.webapp.service;

import ru.itpark.webapp.model.DocumentModel;

import java.util.List;

public interface RfcService {

    void save(String submittedFileName, String size, String date, String url);

    String remove(int id);

    int getTotalNumber();

    List<DocumentModel> getItemsFromTo(int lowerBound, int upperBound);

    List<DocumentModel> getItemsWithLimit(int rowsToSkip, int rowsOnPage);
}
