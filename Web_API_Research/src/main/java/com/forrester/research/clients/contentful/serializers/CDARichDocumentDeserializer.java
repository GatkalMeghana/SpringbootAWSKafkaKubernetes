package com.forrester.research.clients.contentful.serializers;

import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

@JsonComponent
public class CDARichDocumentDeserializer extends JsonDeserializer<CDARichDocument> implements InitializingBean {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void afterPropertiesSet() {
        mapper.registerModule(new SimpleModule().addDeserializer(CDARichDocument.class, this));
    }

    @Override
    public CDARichDocument deserialize(JsonParser p, DeserializationContext ctxt) {
        return null;
    }
}
