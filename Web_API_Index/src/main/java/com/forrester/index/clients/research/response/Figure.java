package com.forrester.index.clients.research.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Figure {

    @JsonProperty("type")
    private String type;
    @JsonProperty("id")
    private String id;
    @JsonProperty("asset")
    private Asset asset;
    @JsonProperty("altText")
    private String altText;
    @JsonProperty("caption")
    private String caption;
    @JsonProperty("figureLabel")
    private String figureLabel;
    @JsonProperty("figureTitle")
    private String figureTitle;
}
