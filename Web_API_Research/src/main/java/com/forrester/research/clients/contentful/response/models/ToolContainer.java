package com.forrester.research.clients.contentful.response.models;

import java.util.Map;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.serializers.CDAAssetSerializer;

@JsonPropertyOrder({
	"id",
	"contentId",
	"contentType",
	"toolType",
    "downloadableTool",
    "embedCode"
})
@ContentfulEntryModel("toolContainer")
@JsonInclude(Include.NON_NULL)
public class ToolContainer {

	@ContentfulSystemField("id")
    private String id;
	@ContentfulField("contentId")
	private String contentId;
	@ContentfulSystemField("contentType")
    private Map<String,Object> contentType;
	@ContentfulField("toolType")
	private String toolType;
	@JsonSerialize(using = CDAAssetSerializer.class)
	@ContentfulField("downloadableTool")
	private CDAAsset downloadableTool;
	@ContentfulField("embedCode")
	private CDAEntry embedCode;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public Map<String, Object> getContentType() {
		return contentType;
	}
	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}
	public String getToolType() {
		return toolType;
	}
	public void setToolType(String toolType) {
		this.toolType = toolType;
	}
	public CDAAsset getDownloadableTool() {
		return downloadableTool;
	}
	public void setDownloadableTool(CDAAsset downloadableTool) {
		this.downloadableTool = downloadableTool;
	}
	public CDAEntry getEmbedCode() {
		return embedCode;
	}
	public void setEmbedCode(CDAEntry embedCode) {
		this.embedCode = embedCode;
	}
}
