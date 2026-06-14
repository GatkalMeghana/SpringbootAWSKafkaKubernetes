package com.forrester.research.clients.contentful.response.models;

import java.util.List;
import java.util.Map;

import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.rich.CDARichOrderedList;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author sgopal
 * @version 2.0
 */
@JsonPropertyOrder({
	"id",
    "contentType",
	"title",
    "columnWidth",
    "fullBleed",
    "backgroundColor",
    "containers"
})
@ContentfulEntryModel("layoutAssembly")
@JsonInclude(Include.NON_NULL)
public class LayoutAssembly {
	@ContentfulSystemField("id")
    private String id;
    @ContentfulSystemField("contentType")
    private Map<String,Object> contentType;
	@ContentfulField("title")
	private String title;
	@ContentfulField("columnWidth")
	private String columnWidth;
	@ContentfulField("fullBleed")
	private Boolean fullBleed = false;
	@ContentfulField("containers")
	private List<CDARichOrderedList> containers;
	@ContentfulField("backgroundColor")
	private String backgroundColor = "White";

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

	public String getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = columnWidth;
	}

	public Boolean isFullBleed() {
		return fullBleed;
	}

	public void setFullBleed(boolean fullBleed) {
		this.fullBleed = fullBleed;
	}

	public List<CDARichOrderedList> getContainers() {
		return containers;
	}

	public void setContainers(List<CDARichOrderedList> containers) {
		this.containers = containers;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

}// end LayoutAssembly