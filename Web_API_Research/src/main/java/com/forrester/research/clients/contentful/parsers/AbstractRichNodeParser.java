package com.forrester.research.clients.contentful.parsers;

import com.contentful.java.cda.rich.CDARichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractRichNodeParser<T extends CDARichBlock> implements RichNodeParser<T> {

    public RichNode parse(T t) {
        List<RichNode> content = t.getContent().stream().map(getParser()::parse).collect(Collectors.toList());
        return parseContent(t, content);
    }

    public abstract RichNode parseContent(T t, List<RichNode> content);

    public abstract ContentfulParser getParser();
}
