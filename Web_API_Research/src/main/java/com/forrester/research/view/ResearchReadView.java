package com.forrester.research.view;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResearchReadView implements Serializable {

	private static final long serialVersionUID = 4961845276491303893L;

	private String contentId;

	private String title;

	private String userId;

	private String accessTime;

	private String originUrl;

	private boolean isClientUser;

	private boolean isFullContentRequest;

	private String source;
	
	private String priceType;
	
	private String price;
	
	private String appSource;
	
	private boolean isPdf;

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}

	public void setOriginUrl(String originUrl) {
		this.originUrl = originUrl;
	}

	public void setClientUser(boolean isClientUser) {
		this.isClientUser = isClientUser;
	}

	public void setFullContentRequest(boolean isFullContentRequest) {
		this.isFullContentRequest = isFullContentRequest;
	}


	public void setSource(String source) {
		this.source = source;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public void setAppSource(String appSource) {
		this.appSource = appSource;
	}

	public void setPdf(boolean isPdf) {
		this.isPdf = isPdf;
	}

	public String getPriceType() {
		return priceType;
	}

	public String getPrice() {
		return price;
	}

	public boolean isPdf() {
		return isPdf;
	}

	public boolean isFullContentRequest() {
		return isFullContentRequest;
	}

	@Override
	public String toString() {
		return "ResearchReadView [contentId=" + contentId + ", title=" + title + ", userId=" + userId + ", accessTime="
				+ accessTime + ", originUrl=" + originUrl + ", isClientUser=" + isClientUser + ", isFullContentRequest="
				+ isFullContentRequest + ", source=" + source + ", priceType=" + priceType + ", price=" + price
				+ ", appSource=" + appSource + "]";
	}

}
