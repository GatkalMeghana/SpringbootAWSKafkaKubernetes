package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichOrderedList;
import com.forrester.research.clients.contentful.parsers.AbstractRichNodeParser;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("CDARichOrderedList")
public class RichOrderedListParser extends AbstractRichNodeParser<CDARichOrderedList> {

    @Autowired
    private ContentfulParser parser;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichOrderedList ol, List<RichNode> content) {
        return new RichBlock("ordered-list", content);
    }
}
