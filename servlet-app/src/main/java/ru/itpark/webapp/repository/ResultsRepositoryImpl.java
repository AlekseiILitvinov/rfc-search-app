package ru.itpark.webapp.repository;

import ru.itpark.webapp.model.DocumentModel;

import java.util.List;
import java.util.Optional;

public class ResultsRepositoryImpl implements ResultsRepository {
    @Override
    public void initialize() {

    }

    @Override
    public Optional<DocumentModel> getById(int id) {
        return Optional.empty();
    }

    @Override
    public void saveItem(int id, String phrase, String url, String doneVsTotal) {

    }

    @Override
    public List<DocumentModel> getAll() {
        return null;
    }
}
