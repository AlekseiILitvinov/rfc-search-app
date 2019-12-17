package ru.itpark.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentModel {
    private long id;
    private String name;
    private String size;
    private String uploadDate;
    private String url;
}
