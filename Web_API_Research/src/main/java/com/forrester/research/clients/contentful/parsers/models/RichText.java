package com.forrester.research.clients.contentful.parsers.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder(value = {
        "data",
        "marks",
        "value",
        "nodeType"
})
public class RichText extends RichNode {

    private String value;

    private List<RichMark> marks;

    public RichText(String nodeType, String value, List<RichMark> marks) {
        this.nodeType = nodeType;
        this.value = value;
        this.marks = marks;
    }

    public RichText(String nodeType, String value, Map<String, Object> data, List<RichMark> marks) {
        this.nodeType = nodeType;
        this.value = value;
        this.marks = marks;
        this.data = data;
    }

    public String getValue() {
        return value;
    }

    public List<RichMark> getMarks() {
        return marks;
    }
}
