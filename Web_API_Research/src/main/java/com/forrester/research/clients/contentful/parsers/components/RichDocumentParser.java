package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichDocument;
import com.forrester.research.clients.contentful.parsers.AbstractRichNodeParser;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("CDARichDocument")
public class RichDocumentParser extends AbstractRichNodeParser<CDARichDocument> {

    @Autowired
    private ContentfulParser parser;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichDocument d, List<RichNode> content) {
        return new RichBlock("document", content);
    }
}
