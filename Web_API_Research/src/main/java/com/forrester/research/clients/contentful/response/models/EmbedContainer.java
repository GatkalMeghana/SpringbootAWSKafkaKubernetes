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
import com.forrester.research.clients.contentful.serializers.CDAAssetListConverter;
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
    "embedLabel",
    "embedTitle",
    "caption",
    "embedCode",
    "pdfOverride",
    "pdfImages",
    "downloadableAssets",
    "indexText",
    "fullwidthToggle"
    
})
@ContentfulEntryModel("embedContainer")
@JsonInclude(Include.NON_NULL)
public class EmbedContainer {

	@ContentfulField("displayTitle")
	private Boolean displayTitle;

	@ContentfulField("supportingText")
	private CDARichDocument supportingText;

	@ContentfulField("contentId")
	private String contentId;

	@ContentfulField("embedLabel")
	private String embedLabel;

	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;

	@ContentfulField("caption")
	private CDARichDocument caption;

	@ContentfulSystemField("id")
    private String id;

	@ContentfulField("title")
	private String title;

	@ContentfulField("embedCode")
	private String embedCode;

	@JsonSerialize(converter = CDAAssetListConverter.class)
	@ContentfulField("downloadableAssets")
	private List<CDAAsset> downloadableAssets;

	@ContentfulField("indexText")
	private String indexText;

	@ContentfulField("fullwidthToggle")
    private Boolean fullwidthToggle;

	@ContentfulField("pdfOverride")
	private Boolean pdfOverride;

	@JsonSerialize(converter = CDAAssetURLListConverter.class)
	@ContentfulField("pdfImages")
	private List<CDAAsset> pdfImages;

	@ContentfulField("embedTitle")
	private String embedTitle;

	public String getEmbedTitle() {
		return embedTitle;
	}

	public String getId() {
		return id;
	}

	public Boolean getFullwidthToggle() {
		return null != fullwidthToggle && fullwidthToggle;
	}

	public String getTitle() {
		return title;
	}

	public String getIndexText() {
		return indexText;
	}

	public String getContentId() {
		return contentId;
	}

	public String getEmbedCode() {
		return embedCode;
	}

	public List<CDAAsset> getDownloadableAssets() {
		return downloadableAssets;
	}

	public CDARichDocument getSupportingText() {
		return supportingText;
	}

	public Boolean getDisplayTitle() {
		return displayTitle;
	}

	public List<CDAAsset> getPdfImages() {
		return pdfImages;
	}

	public CDARichDocument getCaption() {
		return caption;
	}

	public String getEmbedLabel() {
		return embedLabel;
	}

	public Boolean getPdfOverride() {
		return pdfOverride != null && pdfOverride;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public void setEmbedTitle(String embedTitle) {
		this.embedTitle = embedTitle;
	}

	public void setFullwidthToggle(Boolean fullwidthToggle) {
		this.fullwidthToggle = fullwidthToggle;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

	public void setIndexText(String indexText) {
		this.indexText = indexText;
	}

	public void setSupportingText(CDARichDocument supportingText) {
		this.supportingText = supportingText;
	}

	public void setCaption(CDARichDocument caption) {
		this.caption = caption;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setEmbedCode(String embedCode) {
		this.embedCode = embedCode;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDownloadableAssets(List<CDAAsset> downloadableAssets) {
		this.downloadableAssets = downloadableAssets;
	}

	public void setDisplayTitle(Boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setEmbedLabel(String embedLabel) {
		this.embedLabel = embedLabel;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPdfOverride(Boolean pdfOverride) {
		this.pdfOverride = pdfOverride;
	}

	public void setPdfImages(List<CDAAsset> pdfImages) {
		this.pdfImages = pdfImages;
	}
}