package ru.itpark.webapp.repository;

import ru.itpark.webapp.model.DocumentModel;

import java.util.List;
import java.util.Optional;

public interface ResultsRepository {
    void initialize();

    Optional<DocumentModel> getById(int id);

    void saveItem(int id, String phrase, String url, String doneVsTotal);

    List<DocumentModel> getAll();
}
