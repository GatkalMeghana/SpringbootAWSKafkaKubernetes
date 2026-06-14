package com.forrester.research.clients.contentful.parsers.models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class RichNode {
    protected String nodeType;

    protected Map<String, Object> data = new ConcurrentHashMap<>();

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
