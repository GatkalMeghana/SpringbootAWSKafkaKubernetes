package com.forrester.research.clients.contentful.serializers;

import com.contentful.java.cda.CDAEntry;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.forrester.research.clients.contentful.parsers.ContentfulParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class CDAEntrySerializer extends JsonSerializer<CDAEntry> implements InitializingBean {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ContentfulParser parser;

    @Override
    public void afterPropertiesSet() {
        mapper.registerModule(new SimpleModule().addSerializer(CDAEntry.class, this));
    }

    @Override
    public void serialize(CDAEntry entry, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        mapper.writeValue(gen, parser.entryToModelObject(entry));
    }
}
