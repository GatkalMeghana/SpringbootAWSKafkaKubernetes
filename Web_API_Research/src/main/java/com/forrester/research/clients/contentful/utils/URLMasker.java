package com.forrester.research.clients.contentful.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
*@author anisa
*/

@Component
@RefreshScope
public class URLMasker {

    @Value("${forr.contentful.image.search.value}")
	private String imageSearchValue;

    @Value("${forr.contentful.image.replace.value}")
	private String imageReplaceValue;

	@Value("${forr.contentful.asset.search.value}")
	private String assetSearchValue;

	@Value("${forr.contentful.asset.replace.value}")
	private String assetReplaceValue;

	@Value("${forr.contentful.public.image.search.value}")
	private String publicImageSearchValue;

	@Value("${forr.contentful.public.image.replace.value}")
	private String publicImageReplaceValue;

	@Value("${forr.contentful.video.search.value}")
	private String videoSearchValue;

	@Value("${forr.contentful.downloads.search.value}")
	private String downloadsSearchValue;

	public String mask(String sourceURL, String mimeType) {
		if (StringUtils.isNotBlank(sourceURL)) {
			if (mimeType.startsWith("image")) {
				return maskImage(sourceURL);
			} else if (sourceURL.contains("videos.ctfassets.net")){
				return maskVideo(sourceURL);
			} else if(sourceURL.contains("downloads.ctfassets.net")){
				return maskAssetOverTenMB(sourceURL);
			} else {
				return maskAsset(sourceURL);
			}
		}
		return "";
	}

	private String maskImage(String sourceURL) {
		return StringUtils.replace(sourceURL, imageSearchValue, imageReplaceValue);
	}

	private String maskAsset(String sourceURL) {
		return StringUtils.replace(sourceURL, assetSearchValue, assetReplaceValue);
	}

	private String maskVideo(String sourceURL) {
		return StringUtils.replace(sourceURL, videoSearchValue, assetReplaceValue);
	}

	private String maskAssetOverTenMB(String sourceURL) {
		return StringUtils.replace(sourceURL, downloadsSearchValue, assetReplaceValue);
	}

	public String maskPublicImage(String sourceURL, String mimeType) {
		if (StringUtils.isNotBlank(sourceURL) && mimeType.startsWith("image")) {
			return StringUtils.replace(sourceURL, publicImageSearchValue, publicImageReplaceValue);
		}
		return "";
	}

	public String maskEditorialImage(String sourceURL) {
		if (StringUtils.isNotBlank(sourceURL)) {
				return StringUtils.replace(sourceURL, publicImageSearchValue, publicImageReplaceValue);
		}
		return "";
	}
}