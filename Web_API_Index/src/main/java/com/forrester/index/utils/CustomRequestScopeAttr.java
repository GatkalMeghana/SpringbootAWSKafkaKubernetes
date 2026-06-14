package com.forrester.index.utils;

import org.springframework.web.context.request.RequestAttributes;

import java.util.HashMap;
import java.util.Map;

public class CustomRequestScopeAttr implements RequestAttributes {
    private static final int REQUEST_SCOPE = 0;
    private Map<String, Object> requestAttributeMap = new HashMap<>();
    @Override
    public Object getAttribute(String name, int scope) {
        return scope == REQUEST_SCOPE ? this.requestAttributeMap.get(name) : null;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        if (scope == REQUEST_SCOPE) {
            this.requestAttributeMap.put(name, value);
        }
    }

    public void removeAttribute(String name, int scope) {
        if (scope == REQUEST_SCOPE) {
            this.requestAttributeMap.remove(name);
        }
    }
    public String[] getAttributeNames(int scope) {
        return scope == REQUEST_SCOPE ? this.requestAttributeMap.keySet().toArray(new String[0]) : new String[0];
    }
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
    }
    public Object resolveReference(String key) {
        return null;
    }

    @Override
    public String getSessionId() {
        return null;
    }

    @Override
    public Object getSessionMutex() {
        return null;
    }
}
