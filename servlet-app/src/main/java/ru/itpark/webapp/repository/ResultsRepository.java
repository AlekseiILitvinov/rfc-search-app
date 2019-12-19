package ru.itpark.webapp.repository;

import ru.itpark.webapp.model.ResultModel;

import java.util.List;
import java.util.Optional;

public interface ResultsRepository {
    void initialize();

    Optional<ResultModel> getById(int id);

//    void saveItem(int id, String phrase, String status, String url);

    int makeNewQuery(String phrase);

    List<ResultModel> getAll();
}
