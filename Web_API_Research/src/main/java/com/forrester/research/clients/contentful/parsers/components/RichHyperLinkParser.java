package com.forrester.research.clients.contentful.parsers.components;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.rich.CDARichHyperLink;
import com.forrester.research.clients.contentful.parsers.AbstractRichNodeParser;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.google.gson.internal.LinkedTreeMap;

@Component("CDARichHyperLink")
public class RichHyperLinkParser extends AbstractRichNodeParser<CDARichHyperLink> {

    @Autowired
    private ContentfulParser parser;

    @Autowired
    private URLMasker urlMasker;

    @Override
    public ContentfulParser getParser() {
        return parser;
    }

    @Override
    public RichNode parseContent(CDARichHyperLink l, List<RichNode> content) {
        if (l.getData() instanceof CDAEntry) {
            return new RichBlock("entry-hyperlink", Collections.singletonMap(((CDAEntry) l.getData()).contentType().id(), getParser().entryToModelObject((CDAEntry) l.getData())), content);
        } else if (l.getData() instanceof CDAAsset) {
            return new RichBlock("asset-hyperlink", Collections.singletonMap("uri", urlMasker.mask(((CDAAsset) l.getData()).url(), ((CDAAsset) l.getData()).mimeType())), content);
        } else if (l.getData() instanceof LinkedTreeMap) {
            return new RichBlock("hyperlink", (LinkedTreeMap) l.getData(), content);
        } else {
            return new RichBlock("hyperlink", Collections.singletonMap("uri", l.getData()), content);
        }
    }
}
