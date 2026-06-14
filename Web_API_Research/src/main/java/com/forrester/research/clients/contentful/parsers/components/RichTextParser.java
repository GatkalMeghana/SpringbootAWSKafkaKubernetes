package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichText;
import com.forrester.research.clients.contentful.parsers.RichNodeParser;
import com.forrester.research.clients.contentful.parsers.models.RichNode;
import com.forrester.research.clients.contentful.parsers.models.RichText;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component("CDARichText")
public class RichTextParser implements RichNodeParser<CDARichText> {

    @Autowired
    private RichMarkParser markParser;

    public RichNode parse(CDARichText t) {
        return new RichText(
                "text",
                t.getText().toString(),
                t.getMarks().stream().map(m -> markParser.parse(m)).collect(Collectors.toList())
        );
    }
}
