package com.forrester.research.clients.contentful.serializers;

import com.contentful.java.cda.CDAAsset;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.forrester.research.clients.contentful.response.models.CustomAsset;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

@JsonComponent
public class CDAAssetSerializer extends JsonSerializer<CDAAsset> implements InitializingBean {

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
        if (null != asset) {
            if (asset.mimeType().startsWith("image")) {
                mapper.writeValue(gen, urlMasker.mask(asset.url(), asset.mimeType()));
            } else {
                CustomAsset obj = new CustomAsset();
                obj.setFileType(asset.mimeType());
                LinkedTreeMap details = (LinkedTreeMap) ((LinkedTreeMap) asset.getField("file")).get("details");
                if (null != details) {
                    obj.setFileSize(humanReadableByteCountSI(((Double) details.get("size")).longValue()));
                }
                obj.setTitle(asset.title());
                String maskedEntry = urlMasker.mask(asset.url(), asset.mimeType());
                obj.setUrl(maskedEntry);
                mapper.writeValue(gen, obj);
            }
        }
    }

    private String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }
}