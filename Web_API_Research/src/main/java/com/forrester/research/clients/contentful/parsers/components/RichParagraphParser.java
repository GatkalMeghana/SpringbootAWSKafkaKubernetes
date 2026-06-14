package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichParagraph;
import com.forrester.research.clients.contentful.parsers.AbstractRichNodeParser;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("CDARichParagraph")
public class RichParagraphParser extends AbstractRichNodeParser<CDARichParagraph> {

    @Autowired
    private ContentfulParser parser;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichParagraph p, List<RichNode> content) {
        return new RichBlock("paragraph", content);
    }
}
