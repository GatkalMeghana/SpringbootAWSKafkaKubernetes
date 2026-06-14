package com.forrester.research.clients.contentful.response.models;

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
import com.forrester.research.clients.contentful.serializers.CDAPublicImageSerializer;

/**
 * @author mdemissie
 * @version 1.0
 */
@JsonPropertyOrder({
	"id",
	"contentId",
    "contentType",
	"title",
    "image",
    "caption",
    "altText"
})
@ContentfulEntryModel("supplementalImageContainer")
@JsonInclude(Include.NON_NULL)
public class SupplementalImageContainer {
	@ContentfulSystemField("id")
    private String id;
	@ContentfulField("contentId")
	private String contentId;
	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;
	@ContentfulField("title")
	private String title;
    @JsonSerialize(using = CDAPublicImageSerializer.class)
	@ContentfulField("image")
	private CDAAsset image;
	@ContentfulField("caption")
	private CDARichDocument caption;
	@ContentfulField("altText")
	private String altText;

	public String getId() {
		return id;
	}

	public String getContentId() {
		return contentId;
	}

	public String getTitle() {
		return title;
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

	public Map<String, Object> getContentType() {
		return contentType;
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

	public void setImage(CDAAsset image) {
		this.image = image;
	}

	public void setCaption(CDARichDocument caption) {
		this.caption = caption;
	}

	public void setAltText(String altText) {
		this.altText = altText;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}
}