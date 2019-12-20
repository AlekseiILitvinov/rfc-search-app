package ru.itpark.webapp.service;

import ru.itpark.webapp.exception.FileAccessException;
import ru.itpark.webapp.model.DocumentModel;

import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
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
        uploadPath = System.getenv("UPLOAD_PATH") + "/upload";
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new FileAccessException(e);
        }
    }

    @Override
    public String writeFile(Part part) {
        final String id = part.getSubmittedFileName() + UUID.randomUUID().toString() + ".txt";
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

    @Override
    public void readFile(Path file, PrintWriter printWriter) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                printWriter.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DocumentModel> downloadParallel(int start, int stop) {
        String urlBase = "https://tools.ietf.org/rfc/";
        List<DocumentModel> resultsList;

        final ExecutorService execs = Executors.newFixedThreadPool(40);

        List<CompletableFuture<DocumentModel>> downloaders =IntStream.range(start, stop)
                .mapToObj(i -> {
                    String fileName = String.format("rfc%d.txt", i);
                    final String webUrl = urlBase + fileName;

                    final String urlForDb = fileName + UUID.randomUUID().toString() + ".txt";

                    final Path path = Paths.get(uploadPath).resolve(urlForDb);

                    return CompletableFuture.supplyAsync(()->{
                        try (BufferedInputStream in = new BufferedInputStream(new URL(webUrl).openStream())) {
                            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
                            long size = Files.size(path);
                            String sizeStr = "0 B";
                            if (size > 0) {
                                String sizeSuffix = "B";
                                if (size >= 1024) {
                                    size /= 1024;
                                    sizeSuffix = "kB";
                                    if (size >= 1024) {
                                        size /= 1024;
                                        sizeSuffix = "MB";
                                    }
                                }
                                sizeStr = String.format("%d %s", size, sizeSuffix);
                            }
                            final LocalDateTime now = LocalDateTime.now();
                            String uploadDate = String.format("%s %d %d", now.getMonth().toString(), now.getDayOfMonth(), now.getYear());

                            return new DocumentModel(0, fileName, sizeStr, uploadDate, urlForDb);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }, execs);
                }).collect(Collectors.toList());

        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(downloaders.toArray(new CompletableFuture[0]));
        CompletableFuture<List<DocumentModel>> allDocsFuture = allFutures.thenApply(o -> {
            return downloaders.stream().map(future -> future.join()).collect(Collectors.toList());
        });

        final List<DocumentModel> joinedListDocs = allDocsFuture.join();
        execs.shutdown();

        return joinedListDocs;
    }
}
