package com.forrester.research.clients.contentful.response.models;

import java.util.List;
import java.util.Map;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.serializers.CDAAssetURLListConverter;

/**
 * @author mdemissie
 * @version 1.0
 * @created 08-Nov-2020 8:49:02 AM
 */
@JsonPropertyOrder({
	"id",
	"contentId",
    "contentType",
	"title",
    "displayTitle",
    "supportingText",
    "videoLabel",
    "videoTitle",
    "externalId",
    "embedCode",
    "pdfOverride",
    "pdfImages"
})
@ContentfulEntryModel("videoContainer")
@JsonInclude(Include.NON_NULL)
public class VideoContainer {
	@ContentfulSystemField("id")
    private String id;
	@ContentfulField("contentId")
	private String contentId;
	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;
	@ContentfulField("title")
	private String title;
	@ContentfulField("displayTitle")
	private Boolean displayTitle;
	@ContentfulField("supportingText")
	private CDARichDocument supportingText;
	@ContentfulField("videoLabel")
	private String videoLabel;
	@ContentfulField("externalId")
	private String externalId;
	@ContentfulField("embedCode")
	private String embedCode;
	@ContentfulField("pdfOverride")
	private Boolean pdfOverride;
	@JsonSerialize(converter = CDAAssetURLListConverter.class)
	@ContentfulField("pdfImages")
	private List<CDAAsset> pdfImages;
	@ContentfulField("videoTitle")
	private String videoTitle;
	
	public String getVideoTitle() {
		return videoTitle;
	}

	public String getId() {
		return id;
	}

	public String getContentId() {
		return contentId;
	}

	public String getTitle() {
		return title;
	}

	public Boolean getDisplayTitle() {
		return displayTitle;
	}

	public CDARichDocument getSupportingText() {
		return supportingText;
	}

	public String getExternalId() {
		return externalId;
	}

	public String getEmbedCode() {
		return embedCode;
	}

	public String getVideoLabel() {
		return videoLabel;
	}

	public Boolean getPdfOverride() {
		return pdfOverride != null && pdfOverride;
	}

	public List<CDAAsset> getPdfImages() {
		return pdfImages;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public void setVideoTitle(String videoTitle) {
		this.videoTitle = videoTitle;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDisplayTitle(Boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setSupportingText(CDARichDocument supportingText) {
		this.supportingText = supportingText;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setEmbedCode(String embedCode) {
		this.embedCode = embedCode;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

	public void setVideoLabel(String videoLabel) {
		this.videoLabel = videoLabel;
	}

	public void setPdfOverride(Boolean pdfOverride) {
		this.pdfOverride = pdfOverride;
	}

	public void setPdfImages(List<CDAAsset> pdfImages) {
		this.pdfImages = pdfImages;
	}
}