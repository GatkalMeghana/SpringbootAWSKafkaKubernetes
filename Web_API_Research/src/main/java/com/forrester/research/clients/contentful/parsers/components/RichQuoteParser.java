package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichQuote;
import com.forrester.research.clients.contentful.parsers.AbstractRichNodeParser;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("CDARichQuote")
public class RichQuoteParser extends AbstractRichNodeParser<CDARichQuote> {

    @Autowired
    private ContentfulParser parser;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichQuote q, List<RichNode> content) {
        return new RichBlock("blockquote", content);
    }
}
