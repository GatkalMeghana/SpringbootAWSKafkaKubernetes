package com.forrester.index.clients.research.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class BodyJsonAsset  {
    @JsonProperty("id")
    public String id;
    @JsonProperty("title")
    public String title;
    @JsonProperty("fileType")
    public String fileType;
    @JsonProperty("fileSize")
    public String fileSize;
    @JsonProperty("dimensions")
    public Dimension dimensions;
    @JsonProperty("url")
    public String url;
}
