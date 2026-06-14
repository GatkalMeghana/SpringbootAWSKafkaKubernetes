package com.forrester.research.clients.contentful.parsers.models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

@JsonPropertyOrder(value = {
        "data",
        "content",
        "nodeType"
})
public class RichBlock extends RichNode {

    private List<RichNode> content;

    public RichBlock(String nodeType, List<RichNode> content) {
        this.nodeType = nodeType;
        this.content = content;
    }

    public RichBlock(String nodeType, Map<String, Object> data, List<RichNode> content) {
        this.nodeType = nodeType;
        this.content = content;
        this.data = data;
    }

    public List<RichNode> getContent() {
        return content;
    }
}
