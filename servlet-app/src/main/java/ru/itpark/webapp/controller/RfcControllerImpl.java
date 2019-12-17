package ru.itpark.webapp.controller;

import ru.itpark.webapp.model.ResultModel;
import ru.itpark.webapp.service.FileService;
import ru.itpark.webapp.service.RfcService;
import ru.itpark.webapp.model.DocumentModel;
import ru.itpark.worker.SearchWorker;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RfcControllerImpl implements RfcController {
    private RfcService rfcService;
    private FileService fileService;

    @Inject
    public RfcControllerImpl(RfcService rfcService, FileService fileService) {
        this.rfcService = rfcService;
        this.fileService = fileService;
    }

    @Override
    public List<DocumentModel> getAllAtPage(int page) {
        int lowerBound = page*50 + 1;
        int upperBound = (page+1)*50;
        int total = getTotalItems();
        if (upperBound > total){
            upperBound = total;
        }
        return rfcService.getItemsFromTo(lowerBound, upperBound);
    }

    @Override
    public boolean doSearch(String phrase) {
        final SearchWorker searchWorker = new SearchWorker(System.getenv("UPLOAD_PATH"), System.getenv("RESULT_PATH"), phrase);
        searchWorker.search();
        return false;
    }

    @Override
    public List<ResultModel> getAllResults() {
        return null;
    }

    @Override
    public void handlePost(HttpServletRequest req) throws IOException, ServletException {
        final Part part = req.getPart("file");
        final String submittedFileName = part.getSubmittedFileName();
        long size = part.getSize();
        String sizeSuffix = "B";
        if (size >= 1024){
            size /= 1024;
            sizeSuffix = "kB";
            if (size >= 1024){
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

    @Override
    public void removeById(int id) {
        String url = rfcService.remove(id);
        if (!url.isEmpty()){
            fileService.eraseFile(url);
        }
    }

    @Override
    public boolean hasNextPage(int page) {
        int total = rfcService.getTotalNumber();
        return (total > 50*(page+1));
    }

    @Override
    public int getTotalItems() {
        return rfcService.getTotalNumber();
    }

    @Override
    public void populate() {
//        List<DocumentModel> downloads = fileService.downloadFromUrl(1, 50, rfcService);
//        for (DocumentModel download : downloads) {
//            rfcService.save(download.getName(), download.getSize(), download.getUploadDate(), download.getUrl());
//        }

        for (int i = 1; i < 50; i++) {
            String id = UUID.randomUUID().toString();
            final LocalDateTime now = LocalDateTime.now();
            String uploadDate = String.format("%s %d %d", now.getMonth().toString(), now.getDayOfMonth(), now.getYear());

            long size =  fileService.dlSingle(id, i);
            if (size > 0) {
                String sizeSuffix = "B";
                if (size >= 1024){
                    size /= 1024;
                    sizeSuffix = "kB";
                    if (size >= 1024){
                        size /= 1024;
                        sizeSuffix = "MB";
                    }
                }
                String sizeStr = String.format("%d %s", size, sizeSuffix);
                rfcService.save("rfc"+i+".txt", sizeStr, uploadDate, id);
            }
        }
    }
}
