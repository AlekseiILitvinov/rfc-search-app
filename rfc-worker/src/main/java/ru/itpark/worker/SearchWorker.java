package ru.itpark.worker;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchWorker {
    private String uploadPath;
    private String resultPath;

    private String querryPhrase;

    public SearchWorker(String uploadPath, String resultPath, String querryPhrase) {
        this.uploadPath = uploadPath;
        this.resultPath = resultPath;
        this.querryPhrase = querryPhrase;
    }

    public void search() {
        int[] countMatch = new int[1];

        List<Path> filePaths = new ArrayList<>();
        try (final Stream<Path> walk = Files.walk(Paths.get(uploadPath))) {
            filePaths = walk.filter(path -> path.toString().endsWith(".txt")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numberOfFiles = filePaths.size();
        int[] currentlyOn = new int[1];

        //write to db the status

        String id = UUID.randomUUID().toString();
        Path pathResult = Paths.get(resultPath).resolve(id);

        final ExecutorService execs = Executors.newFixedThreadPool(4);

        List<CompletableFuture<Void>> futures = filePaths.stream()
                .map(path -> CompletableFuture.runAsync(() -> {
                    String line;
                    try (
                            final BufferedReader reader = new BufferedReader(new FileReader(path.toString()));
                            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                    new FileOutputStream(pathResult.toString(), true), "utf-8"))
                    ) {
                        int countLines = 1;
                        while ((line = reader.readLine()) != null) {
                            if (line.toLowerCase().contains(querryPhrase.toLowerCase())) {
                                writer.write(String.format("Match found in file %s at line %d", path.toString(), countLines));
                                writer.newLine();
                                writer.write(line);
                                writer.newLine();
                                writer.newLine();
                                writer.flush();
                                countMatch[0]++;
                            }
                            countLines++;
                        }
                        currentlyOn[0]++;
                        String status = String.format("%d/%d", currentlyOn, numberOfFiles);
                        //update db with status
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, execs))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(pathResult.toString(), true), "utf-8"))) {
            writer.write(String.format("Search query: %s\n", querryPhrase));
            writer.write("Total matches: " + countMatch[0]);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //write to db
    }
}
