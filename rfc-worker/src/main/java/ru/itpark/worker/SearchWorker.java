package ru.itpark.worker;

import ru.itpark.util.JdbcTransactionHelper;

import javax.naming.NamingException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchWorker {
    private String uploadPath;
    private String resultPath;
    private JdbcTransactionHelper jdbcTransactionHelper;

    public SearchWorker(String uploadPath, String resultPath) throws NamingException {
        this.uploadPath = uploadPath;
        this.resultPath = resultPath;
        this.jdbcTransactionHelper = new JdbcTransactionHelper();
    }

    public void startSearch(String phrase, int resultId){
        search(phrase,resultId);
    }

    public void search(String queryPhrase, int ResultDbId) {
        int[] countMatch = new int[1];

        List<Path> filePaths = new ArrayList<>();
        try (final Stream<Path> walk = Files.walk(Paths.get(uploadPath))) {
            filePaths = walk.filter(path -> path.toString().endsWith(".txt")).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numberOfFiles = filePaths.size();
        int[] currentlyOn = new int[1];

        jdbcTransactionHelper.updateStatus("In progress", ResultDbId);
        String url;
        if (Charset.forName("US-ASCII").newEncoder().canEncode(queryPhrase)){
            url = queryPhrase + ResultDbId + ".txt";
        } else {
            url = "nonASCIIQuery" + ResultDbId + ".txt";
        }
        jdbcTransactionHelper.updateUrl(url, ResultDbId);
        Path pathResult = Paths.get(resultPath).resolve(url);

        final ExecutorService execs = Executors.newFixedThreadPool(4);

        List<CompletableFuture<Void>> futures = filePaths.stream()
                .map(path -> CompletableFuture.runAsync(() -> {
                    String line;
                    try (
                            final BufferedReader reader = new BufferedReader(new InputStreamReader(
                                    new FileInputStream(path.toString()), StandardCharsets.UTF_8));
                            final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                                    new FileOutputStream(pathResult.toString(), true), StandardCharsets.UTF_8))
                    ) {
                        int countLines = 1;
                        String friendlyFileName = path.getFileName().toString();
                        friendlyFileName = friendlyFileName.substring(0, friendlyFileName.indexOf(".txt")+4);
                        while ((line = reader.readLine()) != null) {
                            if (line.toLowerCase().contains(queryPhrase.toLowerCase())) {
                                writer.write(String.format("Match found in file %s at line %d", friendlyFileName, countLines));
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
                        String status = String.format("%d/%d", currentlyOn[0], numberOfFiles);
                        if (currentlyOn[0] == numberOfFiles){
                            status="Done";
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }, execs))
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        execs.shutdown();

        jdbcTransactionHelper.updateStatus("Done", ResultDbId);

        try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(pathResult.toString(), true), StandardCharsets.UTF_8))) {
            writer.write(String.format("Search query: %s\n", queryPhrase));
            writer.write("Total matches: " + countMatch[0]);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
