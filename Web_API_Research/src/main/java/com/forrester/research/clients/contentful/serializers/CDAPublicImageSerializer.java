package com.forrester.research.clients.contentful.serializers;

import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import com.contentful.java.cda.CDAAsset;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.forrester.research.clients.contentful.utils.URLMasker;

@JsonComponent
public class CDAPublicImageSerializer extends JsonSerializer<CDAAsset> implements InitializingBean {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private URLMasker urlMasker;

    @Override
    public void afterPropertiesSet() {
        mapper.registerModule(new SimpleModule().addSerializer(CDAAsset.class, this));
    }

    @Override
    public void serialize(CDAAsset asset, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        mapper.writeValue(gen, (null != asset) ? urlMasker.maskPublicImage(asset.url(), asset.mimeType()) : "");
    }
}
