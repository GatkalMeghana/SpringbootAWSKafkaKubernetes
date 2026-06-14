package com.forrester.research.clients.contentful.parsers;

import com.forrester.research.clients.contentful.parsers.models.RichNode;

@FunctionalInterface
public interface RichNodeParser<T> {

    RichNode parse(T t);
}
