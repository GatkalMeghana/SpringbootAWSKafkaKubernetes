package com.forrester.research.clients.contentful.response.models;

import java.util.Map;

import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
	"id",
	"contentType",
	"type",
	"url"
})
@ContentfulEntryModel("archiveAsset")
@JsonInclude(Include.NON_NULL)
public class ArchiveAsset {

	@ContentfulSystemField("id")
    private String id;
	@ContentfulSystemField("contentType")
    private Map<String,Object> contentType;
	@ContentfulField("type")
	private String type;
	@ContentfulField("url")
	private String url;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Map<String, Object> getContentType() {
		return contentType;
	}
	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
