package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichHorizontalRule;
import com.forrester.research.clients.contentful.parsers.RichNodeParser;
import com.forrester.research.clients.contentful.parsers.models.RichBlock;
import com.forrester.research.clients.contentful.parsers.models.RichNode;

import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("CDARichHorizontalRule")
public class RichHorizontalRuleParser implements RichNodeParser<CDARichHorizontalRule> {

    public RichNode parse(CDARichHorizontalRule t) {
        return new RichBlock("hr", Collections.emptyList());
    }
}
