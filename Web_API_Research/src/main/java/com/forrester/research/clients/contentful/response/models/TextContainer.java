package com.forrester.research.clients.contentful.response.models;

import java.util.Map;

import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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
    "text"
})
@ContentfulEntryModel("textContainer")
@JsonInclude(Include.NON_NULL)
public class TextContainer {

	@ContentfulField("contentId")
	private String contentId;

	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;

	@ContentfulSystemField("id")
	private String id;


	@ContentfulField("title")
	private String title;

	@ContentfulField("displayTitle")
	private Boolean displayTitle;

	@ContentfulField("text")
	private CDARichDocument text;

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

	public CDARichDocument getText() {
		return text;
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

	public void setDisplayTitle(Boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public void setText(CDARichDocument text) {
		this.text = text;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}
}