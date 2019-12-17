package ru.itpark.webapp.service;

import ru.itpark.webapp.exception.FileAccessException;
import ru.itpark.webapp.model.DocumentModel;

import javax.inject.Inject;
import javax.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FileServiceImpl implements FileService{
    private String uploadPath;

    public FileServiceImpl() {
        uploadPath = System.getenv("UPLOAD_PATH");
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public String writeFile(Part part) {
        final String id = UUID.randomUUID().toString();
        try {
            part.write(Paths.get(uploadPath).resolve(id).toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileAccessException(e);
        }
        return id;
    }

    @Override
    public void eraseFile(String id) {
        final Path file = Paths.get(uploadPath).resolve(id);
        try {
            Files.delete(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileAccessException(e);
        }
    }

//    @Override
//    public List<DocumentModel> downloadFromUrl(int start, int stop, RfcService rfcService) {
//        List<DocumentModel> results = new ArrayList<>();
//
//        String urlBase = "https://tools.ietf.org/rfc/";
//        String urlBase2 = "rfc%d.txt";
//        final LocalDateTime now = LocalDateTime.now();
//        String uploadDate = String.format("%s %d %d", now.getMonth().toString(), now.getDayOfMonth(), now.getYear());
//
//        final ExecutorService execs = Executors.newFixedThreadPool(10);
//        final List<CompletableFuture<Void>> downloaders = IntStream.range(start, stop+1)
//                .mapToObj(i -> {
//                    final String name = String.format(urlBase2, i);
//                    final String url = urlBase+name;
//                    final String id = UUID.randomUUID().toString();
//                    final Path path = Paths.get(uploadPath).resolve(id);
//                    final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//                        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
//                            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }, execs);
//                    try {
//                        long size = Files.size(path);
//                        String sizeSuffix = "B";
//                        if (size >= 1024){
//                            size /= 1024;
//                            sizeSuffix = "kB";
//                            if (size >= 1024){
//                                size /= 1024;
//                                sizeSuffix = "MB";
//                            }
//                        }
//                        String sizeStr = String.format("%d %s", size, sizeSuffix);
//                        rfcService.save(name, sizeStr, uploadDate, url);
////                        results.add(new DocumentModel(0, name, sizeStr, uploadDate, url));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    return voidCompletableFuture;
//                })
//                .collect(Collectors.toList());
//        CompletableFuture.allOf(downloaders.toArray(new CompletableFuture[downloaders.size()])).join();
//        execs.shutdown();
//
//        return results;
//    }

    @Override
    public long dlSingle(String id, int number) {
        long size=0;
        String url = String.format("https://tools.ietf.org/rfc/rfc%d.txt", number);
        final Path path = Paths.get(uploadPath).resolve(id);
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream())) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            size = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }
}
