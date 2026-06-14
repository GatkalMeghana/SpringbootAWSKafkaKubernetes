package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.rich.CDARichEmbeddedInline;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.forrester.research.clients.contentful.parsers.AbstractRichNodeParser;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("CDARichEmbeddedInline")
public class RichEmbeddedInlineParser extends AbstractRichNodeParser<CDARichEmbeddedInline> {

    @Autowired
    private ContentfulParser parser;

    @Autowired
    private URLMasker urlMasker;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichEmbeddedInline ei, List<RichNode> content) {
        if (ei.getData() instanceof CDAEntry) {
            return new RichBlock("embedded-entry-inline", Collections.singletonMap(((CDAEntry) ei.getData()).contentType().id(), getParser().entryToModelObject((CDAEntry) ei.getData())), content);
        } else if (ei.getData() instanceof CDAAsset) {
            return new RichBlock("embedded-asset-inline", Collections.singletonMap("uri", urlMasker.mask(((CDAAsset) ei.getData()).url(), ((CDAAsset) ei.getData()).mimeType())), content);
        } else if (ei.getData() instanceof LinkedTreeMap) {
            return new RichBlock("embedded-inline", (LinkedTreeMap) ei.getData(), content);
        } else {
            return new RichBlock("embedded-inline", Collections.singletonMap("uri", ei.getData()), content);
        }
    }
}
