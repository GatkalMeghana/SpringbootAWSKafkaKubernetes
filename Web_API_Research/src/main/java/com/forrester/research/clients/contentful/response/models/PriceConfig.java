package com.forrester.research.clients.contentful.response.models;

import java.util.Map;

import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@ContentfulEntryModel("priceConfig")
@JsonInclude(Include.NON_NULL)
public class PriceConfig {
	@ContentfulSystemField("id")
    private String id;
	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;
	@ContentfulField("title")
	private String title;

	public Map<String, Object> getContentType() {
		return contentType;
	}
	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}
	@ContentfulField("price")
    private String price;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}   
	@Override
	public String toString() {
		return "PriceConfig [id=" + id + ", title=" + title + ", price=" + price + "]";
	}
	
}
