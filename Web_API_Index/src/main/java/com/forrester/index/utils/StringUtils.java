package com.forrester.index.utils;

import java.util.function.UnaryOperator;

import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.web.util.HtmlUtils.htmlUnescape;

public class StringUtils {
    private static final String[] SEARCH_VALUES = new String[]{
            "EMPTY_CELL", "_", System.lineSeparator(), CR, LF
    };
    private static final String[] REPLACE_VALUES = new String[]{
            "", "", " ", "", " "
    };
    
    private static final String[] SEARCH_VALUES_IN_JSON = new String[]{
            "EMPTY_CELL","\"","\\", "_", System.lineSeparator(), CR, LF
    };
    private static final String[] REPLACE_VALUES_IN_JSON = new String[]{
            "","", " ", "", " ", "", " "
    };
    public static final UnaryOperator<String> SANITIZE = value -> normalizeSpace(
            replaceEachRepeatedly(null != value ? htmlUnescape(value).replaceAll("\\<.*?\\>", "") : EMPTY, StringUtils.SEARCH_VALUES, StringUtils.REPLACE_VALUES));

    public static final UnaryOperator<String> SANITIZE_JSON = value -> normalizeSpace(
            replaceEachRepeatedly(null != value ? htmlUnescape(value).replaceAll("\\<.*?\\>", "") : EMPTY, StringUtils.SEARCH_VALUES_IN_JSON, StringUtils.REPLACE_VALUES_IN_JSON));
    
    private StringUtils() throws IllegalAccessException {
        throw new IllegalAccessException("For static use only");
    }
}
