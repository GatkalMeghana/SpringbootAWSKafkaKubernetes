package com.forrester.index.clients.research.response;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class EmbedCodeDeserializer extends StdDeserializer<String> {
    private static final long serialVersionUID = 1095611390923498227L;
    
    public EmbedCodeDeserializer() {
        super(String.class);
    }
    
    protected EmbedCodeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode jsonNode = p.getCodec().readTree(p);
        return null == jsonNode ? StringUtils.EMPTY : jsonNode.toString();
    }

}
