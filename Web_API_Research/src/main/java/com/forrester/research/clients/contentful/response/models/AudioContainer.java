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
 */
@JsonPropertyOrder({
	"id",
	"contentId",
    "contentType",
	"title",
    "displayTitle",
    "supportingText",
    "audioLabel",
    "audioTitle",
    "externalId",
    "embedCode",
    "pdfOverride",
    "pdfImages"
})
@ContentfulEntryModel("audioContainer")
@JsonInclude(Include.NON_NULL)
public class AudioContainer {

	@ContentfulField("title")
	private String title;

	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;

	@ContentfulField("supportingText")
	private CDARichDocument supportingText;

	@ContentfulField("displayTitle")
	private Boolean displayTitle;

	@ContentfulField("audioLabel")
	private String audioLabel;

	@ContentfulField("contentId")
	private String contentId;

	@ContentfulField("embedCode")
	private String embedCode;

	@ContentfulField("externalId")
	private String externalId;

	@ContentfulSystemField("id")
    private String id;

	@ContentfulField("pdfOverride")
	private Boolean pdfOverride;

	@JsonSerialize(converter = CDAAssetURLListConverter.class)
	@ContentfulField("pdfImages")
	private List<CDAAsset> pdfImages;

	@ContentfulField("audioTitle")
	private String audioTitle;

	public CDARichDocument getSupportingText() {
		return supportingText;
	}

	public String getAudioTitle() {
		return audioTitle;
	}

	public String getExternalId() {
		return externalId;
	}

	public Boolean getDisplayTitle() {
		return displayTitle;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmbedCode() {
		return embedCode;
	}

	public String getContentId() {
		return contentId;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public String getAudioLabel() {
		return audioLabel;
	}

	public Boolean getPdfOverride() {
		return pdfOverride != null && pdfOverride;
	}

	public List<CDAAsset> getPdfImages() {
		return pdfImages;
	}

	public void setDisplayTitle(Boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public void setAudioTitle(String audioTitle) {
		this.audioTitle = audioTitle;
	}

	public void setSupportingText(CDARichDocument supportingText) {
		this.supportingText = supportingText;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setEmbedCode(String embedCode) {
		this.embedCode = embedCode;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

	public void setAudioLabel(String audioLabel) {
		this.audioLabel = audioLabel;
	}

	public void setPdfOverride(Boolean pdfOverride) {
		this.pdfOverride = pdfOverride;
	}

	public void setPdfImages(List<CDAAsset> pdfImages) {
		this.pdfImages = pdfImages;
	}
}