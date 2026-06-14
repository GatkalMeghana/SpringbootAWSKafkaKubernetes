package com.forrester.index.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class TextExtractUtil {

    private static final String KEY_NODE_TYPE = "nodeType";
    private static final String KEY_VALUE = "value";
    private static final String KEY_DATA = "data";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_FNTEXT = "fnText";
    private static final String KEY_FNTABLE = "fnTable";
    private static final String KEY_TEXT = "text";
    private static final String KEY_TABLE = "table";
    private static final String KEY_TITLE = "title";
    private static final String VALUE_TEXT = "text";
    private static final String VALUE_EMBEDDED_ENTRY_BLOCK = "embedded-entry-block";

    private TextExtractUtil() throws IllegalAccessException {
        throw new IllegalAccessException("For static use only");
    }

    public static String extract(Map<String, Object> textData) {
        return StringUtils.normalizeSpace(consolidatedTextData(new LinkedHashSet<>(), textData).stream().collect(Collectors.joining(" ")));
    }

    private static Set<String> consolidatedTextData(Set<String> extracted, Map<String, Object> textData) {
        if (null != textData) {
            if (textData.containsKey(KEY_NODE_TYPE) && textData.get(KEY_NODE_TYPE).equals(VALUE_TEXT) &&
                    textData.containsKey(KEY_VALUE) && StringUtils.isNotEmpty((String) textData.get(KEY_VALUE))) {
                extracted.add(com.forrester.index.utils.StringUtils.SANITIZE.apply((String) textData.get(KEY_VALUE)));
            }
            if (textData.containsKey(KEY_NODE_TYPE) && textData.get(KEY_NODE_TYPE).equals(VALUE_EMBEDDED_ENTRY_BLOCK) &&
                    textData.containsKey(KEY_DATA) && (null != textData.get(KEY_DATA))) {
                consolidateEmbeddedEntryBlock(extracted, (Map<String, Object>) textData.get(KEY_DATA));
            } else if (textData.containsKey(KEY_CONTENT) && (null != textData.get(KEY_CONTENT))) {
                List<Object> content = ((List<Object>) textData.get(KEY_CONTENT));
                content.stream().forEach(o -> {
                    if (o instanceof Map) {
                        consolidatedTextData(extracted, (Map<String, Object>) o);
                    }
                });
            }
        }
        return extracted;
    }

    private static Set<String> consolidateEmbeddedEntryBlock(Set<String> extracted, Map<String, Object> entryBlock) {
        if (entryBlock.containsKey(KEY_FNTEXT) && (null != entryBlock.get(KEY_FNTEXT))) {
            Map<String, Object> fnText = (Map<String, Object>) entryBlock.get(KEY_FNTEXT);
            if (null != fnText.get(KEY_TEXT)) {
                consolidatedTextData(extracted, (Map<String, Object>) fnText.get(KEY_TEXT));
            }
        } else if (entryBlock.containsKey(KEY_FNTABLE) && (null != entryBlock.get(KEY_FNTABLE))) {
            Map<String, Object> fnTable = (Map<String, Object>) entryBlock.get(KEY_FNTABLE);
            if (null != fnTable.get(KEY_TITLE)) {
                extracted.add(com.forrester.index.utils.StringUtils.SANITIZE.apply((String) fnTable.get(KEY_TITLE)));
            }
            if (null != fnTable.get(KEY_TABLE)) {
                List<Map<String, String>> list = ((List<Map<String, String>>) fnTable.get(KEY_TABLE));
                extracted.addAll(list.stream().map(Map::keySet).flatMap(Set::stream).map(com.forrester.index.utils.StringUtils.SANITIZE).collect(Collectors.toSet()));
                extracted.addAll(list.stream().map(Map::values).flatMap(Collection::stream).map(com.forrester.index.utils.StringUtils.SANITIZE).collect(Collectors.toSet()));
            }
        }
        return extracted;
    }
}