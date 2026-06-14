package com.forrester.research.clients.contentful.serializers;

import com.contentful.java.cda.CDAAsset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.forrester.research.clients.contentful.response.models.BrandedImageCustomAsset;
import com.forrester.research.clients.contentful.utils.PackageUtil;
import com.forrester.research.clients.contentful.utils.URLMasker;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author manthanb
 * @version 1.0
 */

public class CDAAssetBrandedImageConverter extends StdConverter<CDAAsset, Object> implements InitializingBean {

	@Autowired
	private URLMasker urlMasker;

	@Autowired
	private ObjectMapper mapper;

	private static final String TITLE = "Download Figure";

	@Override
	public void afterPropertiesSet() {
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(CDAAsset.class, new StdDelegatingSerializer(new CDAAssetBrandedImageConverter()));
		mapper.registerModule(simpleModule);
	}

	@Override
	public Object convert(CDAAsset asset) {
		List<Object> result = new ArrayList<>();
		BrandedImageCustomAsset obj = new BrandedImageCustomAsset();
		obj.setFileType(asset.mimeType());
		LinkedTreeMap details = (LinkedTreeMap) ((LinkedTreeMap) asset.getField("file")).get("details");

		if (null != details) {
			obj.setFileSize(PackageUtil.humanReadableByteCountSI(((Double) details.get("size")).longValue()));
		}

		obj.setTitle(TITLE);
		String maskedEntry = urlMasker.mask(asset.url(), asset.mimeType());
		obj.setUrl(maskedEntry);
		obj.setBranded(true);
		obj.setExtension(maskedEntry.lastIndexOf(".") > 0 ? maskedEntry.substring(maskedEntry.lastIndexOf(".") + 1) : "");
		result.add(obj);

		return result;
	}
}