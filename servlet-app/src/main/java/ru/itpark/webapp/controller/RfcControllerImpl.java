package ru.itpark.webapp.controller;

import ru.itpark.webapp.model.ResultModel;
import ru.itpark.webapp.repository.ResultsRepository;
import ru.itpark.webapp.service.FileService;
import ru.itpark.webapp.service.RfcService;
import ru.itpark.webapp.model.DocumentModel;
import ru.itpark.worker.SearchWorker;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RfcControllerImpl implements RfcController {
    private RfcService rfcService;
    private FileService fileService;
    private ResultsRepository resultsRepository;
    private SearchWorker searchWorker;

    @Inject
    public RfcControllerImpl(RfcService rfcService, FileService fileService, ResultsRepository resultsRepository) {
        this.rfcService = rfcService;
        this.fileService = fileService;
        this.resultsRepository = resultsRepository;
        try {
            String basePath = System.getenv("UPLOAD_PATH");
            String uploadPath = basePath + "/upload";
            String resultPath = basePath + "/results";
            File directory = new File(resultPath);
            if (!directory.exists()) {
                directory.mkdir();
            }
            resultPath = resultPath + "/";
            this.searchWorker = new SearchWorker(uploadPath, resultPath);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<DocumentModel> getAllAtPage(int page) {
        int rowsOnPage = 10;
        int rowsToSkip = (page - 1) * rowsOnPage;
        return rfcService.getItemsWithLimit(rowsToSkip, rowsOnPage);
    }

    @Override
    public boolean doSearch(String phrase) {
        int resultId = resultsRepository.makeNewQuery(phrase);
        searchWorker.startSearch(phrase, resultId);
        return true;
    }

    @Override
    public List<ResultModel> getAllResults() {
        return resultsRepository.getAll();
    }

    @Override
    public void handlePost(HttpServletRequest req) throws IOException, ServletException {

        final List<Part> rfcFiles = req.getParts().stream().filter(part -> part.getSubmittedFileName().endsWith(".txt")).collect(Collectors.toList());

        for (Part part : rfcFiles) {
            final String submittedFileName = part.getSubmittedFileName();
            long size = part.getSize();
            String sizeSuffix = "B";
            if (size >= 1024) {
                size /= 1024;
                sizeSuffix = "kB";
                if (size >= 1024) {
                    size /= 1024;
                    sizeSuffix = "MB";
                }
            }
            String sizeStr = String.format("%d %s", size, sizeSuffix);
            final LocalDateTime now = LocalDateTime.now();
            String uploadDate = String.format("%s %d %d", now.getMonth().toString(), now.getDayOfMonth(), now.getYear());
            String url = fileService.writeFile(part);
            rfcService.save(submittedFileName, sizeStr, uploadDate, url);
        }
    }

    @Override
    public void removeById(int id) {
        String url = rfcService.remove(id);
        if (!url.isEmpty()) {
            fileService.eraseFile(url);
        }
    }

    @Override
    public int getTotalItems() {
        return rfcService.getTotalNumber();
    }

    @Override
    public void readResultsFile(String filename, PrintWriter writer) {
        Path resultPath = Paths.get(System.getenv("UPLOAD_PATH") + "/results/").resolve(filename);
        fileService.readFile(resultPath, writer);
    }

    @Override
    public void populateParallel(int from, int to) {

        List<DocumentModel> downloaded = fileService.downloadParallel(from, to);

        for (DocumentModel documentModel : downloaded) {
            if (documentModel != null) {
                rfcService.save(documentModel.getName(), documentModel.getSize(), documentModel.getUploadDate(), documentModel.getUrl());
            }
        }
    }
}
