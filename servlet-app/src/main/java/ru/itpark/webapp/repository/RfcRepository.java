package ru.itpark.webapp.repository;

import ru.itpark.webapp.model.DocumentModel;

import java.util.List;
import java.util.Optional;

public interface RfcRepository {
    Optional<DocumentModel> getById(int id);

    void removeById(int id);

    int getTotalNumber();

    void initialize();

    void saveItem(String submittedFileName, String size, String date, String url);

    List<DocumentModel> getFromTo(int from, int to);
}
