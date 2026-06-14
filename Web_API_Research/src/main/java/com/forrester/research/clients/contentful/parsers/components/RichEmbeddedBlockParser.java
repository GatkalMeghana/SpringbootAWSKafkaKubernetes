package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.rich.CDARichEmbeddedBlock;
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

@Component("CDARichEmbeddedBlock")
public class RichEmbeddedBlockParser extends AbstractRichNodeParser<CDARichEmbeddedBlock> {

    @Autowired
    private ContentfulParser parser;

    @Autowired
    private URLMasker urlMasker;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichEmbeddedBlock eb, List<RichNode> content) {
        if (eb.getData() instanceof CDAEntry) {
            return new RichBlock("embedded-entry-block", Collections.singletonMap(((CDAEntry) eb.getData()).contentType().id(), getParser().entryToModelObject((CDAEntry) eb.getData())), content);
        } else if (eb.getData() instanceof CDAAsset) {
            return new RichBlock("embedded-asset-block", Collections.singletonMap("uri", urlMasker.mask(((CDAAsset) eb.getData()).url(), ((CDAAsset) eb.getData()).mimeType())), content);
        } else if (eb.getData() instanceof LinkedTreeMap) {
            return new RichBlock("embedded-block", (LinkedTreeMap) eb.getData(), content);
        } else {
            return new RichBlock("embedded-block", Collections.singletonMap("uri", eb.getData()), content);
        }
    }
}
