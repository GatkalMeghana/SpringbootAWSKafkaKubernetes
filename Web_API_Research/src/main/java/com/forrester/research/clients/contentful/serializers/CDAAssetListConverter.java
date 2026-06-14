package com.forrester.research.clients.contentful.serializers;

import com.contentful.java.cda.CDAAsset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.forrester.research.clients.contentful.response.models.CustomAsset;
import com.forrester.research.clients.contentful.utils.PackageUtil;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.List;

@JsonComponent
public class CDAAssetListConverter extends StdConverter<List<CDAAsset>, List<Object>> implements InitializingBean {

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

        for(CDAAsset asset : assets) {
    		CustomAsset obj = new CustomAsset();
    		obj.setFileType(asset.mimeType());
    		LinkedTreeMap details = (LinkedTreeMap) ((LinkedTreeMap)asset.getField("file")).get("details");

            if(null != details) {
    			obj.setFileSize(PackageUtil.humanReadableByteCountSI(((Double) details.get("size")).longValue()));
    		}

    		obj.setTitle(asset.title());
    		String maskedEntry = urlMasker.mask(asset.url(), asset.mimeType());
    		obj.setUrl(maskedEntry);    		
			result.add(obj);						
		}

        return result;
    }
}