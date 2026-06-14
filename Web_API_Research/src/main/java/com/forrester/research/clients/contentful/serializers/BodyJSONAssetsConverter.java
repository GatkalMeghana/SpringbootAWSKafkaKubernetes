package com.forrester.research.clients.contentful.serializers;

import com.contentful.java.cda.CDAAsset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.forrester.research.clients.contentful.response.models.BodyJSONAsset;
import com.forrester.research.clients.contentful.utils.LogThis;
import com.forrester.research.clients.contentful.utils.PackageUtil;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Purpose is to convert bodyjsonassets in required format.
 * 
 * @author meghanag
 *
 */
@JsonComponent
public class BodyJSONAssetsConverter extends StdConverter<List<CDAAsset>, List<Object>> implements InitializingBean {

	@Autowired
	private URLMasker urlMasker;

	@Autowired
    private ObjectMapper mapper;

	public static final String IMAGE = "image";

    @Override
    public void afterPropertiesSet() {    
    	SimpleModule simpleModule = new SimpleModule();
    	simpleModule.addSerializer(CDAAsset.class, new StdDelegatingSerializer(new CDAAssetListConverter()));
    	mapper.registerModule(simpleModule);
    }

    @Override
    @LogThis
    public List<Object> convert(List<CDAAsset> assets) {
    	List<Object> result = new ArrayList<>();

		for(CDAAsset asset : assets) {
    		BodyJSONAsset obj = new BodyJSONAsset();
    		obj.setId(asset.getAttribute("id").toString());
    		obj.setFileType(asset.mimeType());
    		LinkedTreeMap details = (LinkedTreeMap) ((LinkedTreeMap)asset.getField("file")).get("details");

			if (null != details) {
    			obj.setFileSize(PackageUtil.humanReadableByteCountSI(((Double) details.get("size")).longValue()));

				if (details.get(IMAGE) != null) {
    				obj.setDimensions(details.get(IMAGE));
    			}
    		}

			obj.setTitle(asset.title());
    		String maskedEntry = urlMasker.mask(asset.url(), asset.mimeType());
    		obj.setUrl(maskedEntry);    		
			result.add(obj);						
		}

		return result;
    }
}