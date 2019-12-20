package ru.itpark.webapp.service;

import ru.itpark.webapp.model.DocumentModel;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

public interface FileService {

    String writeFile(Part part);

    void eraseFile(String url);

//    List<DocumentModel> downloadFromUrl(int start, int stop, RfcService rfcService);

    long dlSingle(String id, int number);

    public void readFile(Path file, PrintWriter printWriter);

    List<DocumentModel> downloadParallel(int start, int stop);
}
