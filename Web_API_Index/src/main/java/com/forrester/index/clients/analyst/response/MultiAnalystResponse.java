package com.forrester.index.clients.analyst.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MultiAnalystResponse {
    @JsonProperty("contentId")
    private String contentId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
}
