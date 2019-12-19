package ru.itpark.webapp.service;

import ru.itpark.webapp.exception.FileAccessException;

import javax.servlet.http.Part;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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
}
