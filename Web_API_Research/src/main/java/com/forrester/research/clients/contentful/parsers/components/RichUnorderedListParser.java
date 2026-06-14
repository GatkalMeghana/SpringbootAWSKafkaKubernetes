package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichUnorderedList;
import com.forrester.research.clients.contentful.parsers.AbstractRichNodeParser;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("CDARichUnorderedList")
public class RichUnorderedListParser extends AbstractRichNodeParser<CDARichUnorderedList> {

    @Autowired
    private ContentfulParser parser;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichUnorderedList ul, List<RichNode> content) {
        return new RichBlock("unordered-list", content);
    }
}
