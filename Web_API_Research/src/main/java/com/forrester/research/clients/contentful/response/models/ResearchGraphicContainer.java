package com.forrester.research.clients.contentful.response.models;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.serializers.CDAAssetBrandedImageConverter;
import com.forrester.research.clients.contentful.serializers.CDAAssetListConverter;
import com.forrester.research.clients.contentful.serializers.CDAAssetSerializer;
import com.forrester.research.clients.contentful.serializers.CDAAssetURLListConverter;

import java.util.List;
import java.util.Map;

/**
 * @author sgopal
 * @version 2.0
 */
@JsonPropertyOrder({
	"id",
	"contentId",
    "contentType",
	"title",
    "displayTitle",
    "figureTitle",
    "description",
    "figureLabel",
    "image",
    "pdfOverride",
    "pdfImages",
    "caption",
    "altText",
    "downloadableAssets"
})
@ContentfulEntryModel("researchGraphicContainer")
@JsonInclude(Include.NON_NULL)
public class ResearchGraphicContainer {

	@ContentfulField("displayTitle")
	private Boolean displayTitle;

	@ContentfulField("figureLabel")
	private String figureLabel;

	@ContentfulField("title")
	private String title;

	@ContentfulField("contentId")
	private String contentId;

	@ContentfulField("figureTitle")
	private String figureTitle;

	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;

	@ContentfulSystemField("id")
	private String id;

	@ContentfulField("description")
	private CDARichDocument description;

	@JsonSerialize(using = CDAAssetSerializer.class)
	@ContentfulField("image")
	private CDAAsset image;

	@ContentfulField("caption")
	private CDARichDocument caption;

	@ContentfulField("altText")
	private String altText;

    @JsonSerialize(converter = CDAAssetListConverter.class)
	@ContentfulField("downloadableAssets")
	private List<CDAAsset> downloadableAssets;

	@ContentfulField("pdfOverride")
	private Boolean pdfOverride;

	@JsonSerialize(converter = CDAAssetURLListConverter.class)
	@ContentfulField("pdfImages")
	private List<CDAAsset> pdfImages;

	@JsonSerialize(converter = CDAAssetBrandedImageConverter.class)
   	@ContentfulField("image")
   	private CDAAsset brandedImage;

	public String getContentId() {
		return contentId;
	}

	public String getFigureLabel() {
		return figureLabel;
	}

	public String getId() {
		return id;
	}

	public CDAAsset getBrandedImage() {
		return brandedImage;
	}

	public CDARichDocument getDescription() {
		return description;
	}

	public Boolean getDisplayTitle() {
		return displayTitle;
	}

	public String getTitle() {
		return title;
	}

	public String getFigureTitle() {
		return figureTitle;
	}

	public CDAAsset getImage() {
		return image;
	}

	public CDARichDocument getCaption() {
		return caption;
	}

	public String getAltText() {
		return altText;
	}

	public List<CDAAsset> getDownloadableAssets() {
		return downloadableAssets;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public Boolean getPdfOverride() {
		return pdfOverride != null && pdfOverride;
	}

	public List<CDAAsset> getPdfImages() {
		return pdfImages;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setDescription(CDARichDocument description) {
		this.description = description;
	}

	public void setFigureTitle(String figureTitle) {
		this.figureTitle = figureTitle;
	}

	public void setImage(CDAAsset image) {
		this.image = image;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCaption(CDARichDocument caption) {
		this.caption = caption;
	}

	public void setDisplayTitle(Boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDownloadableAssets(List<CDAAsset> downloadableAssets) {
		this.downloadableAssets = downloadableAssets;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

	public void setFigureLabel(String figureLabel) {
		this.figureLabel = figureLabel;
	}

	public void setPdfOverride(Boolean pdfOverride) {
		this.pdfOverride = pdfOverride;
	}

	public void setPdfImages(List<CDAAsset> pdfImages) {
		this.pdfImages = pdfImages;
	}

	public void setBrandedImage(CDAAsset brandedImage) {
		this.brandedImage = brandedImage;
	}
}