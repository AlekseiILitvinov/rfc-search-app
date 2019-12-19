package ru.itpark.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResultModel {
    private int id;
    private String searchPhrase;
    private String status;
    private String url;
}
