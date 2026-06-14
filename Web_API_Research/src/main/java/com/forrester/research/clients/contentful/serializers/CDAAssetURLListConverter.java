package com.forrester.research.clients.contentful.serializers;

import com.contentful.java.cda.CDAAsset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.forrester.research.clients.contentful.response.models.CustomAsset;
import com.forrester.research.clients.contentful.utils.URLMasker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class CDAAssetURLListConverter extends StdConverter<List<CDAAsset>, List<Object>> implements InitializingBean {

    @Autowired
    private URLMasker urlMasker;

    @Autowired
    private ObjectMapper mapper;


    @Override
    public void afterPropertiesSet() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(CDAAsset.class, new StdDelegatingSerializer(new CDAAssetListConverter()));
        mapper.registerModule(simpleModule);
    }

    @Override
    public List<Object> convert(List<CDAAsset> assets) {
        List<Object> result = new ArrayList<>();

        for (CDAAsset asset : assets) {
            CustomAsset obj = new CustomAsset();
            String maskedEntry = (null != asset) ? urlMasker.mask(asset.url(), asset.mimeType()) : StringUtils.EMPTY;
            obj.setUrl(maskedEntry);
            result.add(obj);
        }

        return result;
    }
}