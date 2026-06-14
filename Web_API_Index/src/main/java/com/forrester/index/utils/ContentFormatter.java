package com.forrester.index.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.*;
import static org.springframework.web.util.HtmlUtils.htmlUnescape;

@Service
public class ContentFormatter {
    private static final String[] SEARCH_VALUES = {"EMPTY_CELL", "_", System.lineSeparator(), CR, LF};
    private static final String[] REPLACE_VALUES = {"", "", " ", "", " "};
    private static final UnaryOperator<String> SANITIZE = value ->
            normalizeSpace(replaceEachRepeatedly(null != value ? htmlUnescape(value).replaceAll("\\<.*?\\>", "") :
                    EMPTY, SEARCH_VALUES, REPLACE_VALUES));

    public final Function<JsonNode, Object> GET_COMBINED_TEXT = node -> fetchCombinedText(node, Arrays.asList("value"));

    private String fetchCombinedText(JsonNode node, List<String> textFieldNames) {
        List<String> values = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(child -> values.add(fetchCombinedText(child, textFieldNames)));
        } else {
            node.fieldNames().forEachRemaining(childField -> {
                if (textFieldNames.contains(childField)) {
                    values.add(node.findValue(childField).asText(StringUtils.EMPTY));
                } else {
                    values.add(fetchCombinedText(node.findValue(childField), textFieldNames));
                }
            });
        }
        return values.stream()
                .map(SANITIZE)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(StringUtils.SPACE));
    }
}
