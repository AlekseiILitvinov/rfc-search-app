package ru.itpark.webapp.service;

import ru.itpark.webapp.model.DocumentModel;
import ru.itpark.webapp.repository.RfcRepository;

import javax.inject.Inject;
import javax.naming.NamingException;
import java.util.List;
import java.util.Optional;

public class RfcServiceImpl implements RfcService {
    private RfcRepository repository;

    @Inject
    public RfcServiceImpl(RfcRepository repository) {
        this.repository = repository;
        repository.initialize();
    }

    @Override
    public void save(String submittedFileName, String size, String date, String url) {
        repository.saveItem(submittedFileName, size, date, url);
    }

    @Override
    public String remove(int id) {
        Optional<DocumentModel> optional = repository.getById(id);
        String url="";
        if (optional.isPresent()) {
            url = optional.get().getUrl();
            repository.removeById(id);
        }
        if (url != null) {
            return url;
        } else {
            return "";
        }
    }

    @Override
    public int getTotalNumber() {
        return repository.getTotalNumber();
    }

    @Override
    public List<DocumentModel> getItemsFromTo(int lowerBound, int upperBound) {
        return repository.getFromTo(lowerBound, upperBound);
    }

    @Override
    public List<DocumentModel> getItemsWithLimit(int rowsToSkip, int rowsOnPage) {
        return repository.getWithLimit(rowsToSkip, rowsOnPage);
    }
}
