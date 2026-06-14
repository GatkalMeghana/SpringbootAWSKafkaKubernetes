package com.forrester.research.clients.contentful.parsers.components;

import com.contentful.java.cda.rich.CDARichMark;
import com.forrester.research.clients.contentful.parsers.models.RichMark;

import org.springframework.stereotype.Component;

@Component("RichMarkParser")
public class RichMarkParser {

    private static final String MARK_BOLD = "bold";
    private static final String MARK_ITALIC = "italic";
    private static final String MARK_UNDERLINE = "underline";
    private static final String MARK_CODE = "code";

    public RichMark parse(CDARichMark t) {
        return new RichMark(processMarks(t));
    }

    private String processMarks(CDARichMark t) {
        if (t instanceof CDARichMark.CDARichMarkBold) {
            return MARK_BOLD;
        } else if (t instanceof CDARichMark.CDARichMarkItalic) {
            return MARK_ITALIC;
        } else if (t instanceof CDARichMark.CDARichMarkUnderline) {
            return MARK_UNDERLINE;
        } else if (t instanceof CDARichMark.CDARichMarkCode) {
            return MARK_CODE;
        } else if (t instanceof CDARichMark.CDARichMarkCustom) {
            return ((CDARichMark.CDARichMarkCustom) t).getType();
        }
        return "";
    }
}
