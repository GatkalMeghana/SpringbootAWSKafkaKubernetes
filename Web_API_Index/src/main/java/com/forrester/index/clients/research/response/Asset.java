package com.forrester.index.clients.research.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.List;

@Data
@NoArgsConstructor
public class Asset {
    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("url")
    private String url;
    @JsonProperty("linkedReports")
    private List<String> linkedReports;
    @JsonProperty("publishedDate")
    private String publishedDate;
    @JsonProperty("fileType")
    private String fileType;
}
