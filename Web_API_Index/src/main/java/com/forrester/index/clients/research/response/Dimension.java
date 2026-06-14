package com.forrester.index.clients.research.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class Dimension {
    @JsonProperty("width")
    public Float width;
    @JsonProperty("height")
    public Float height;
}
