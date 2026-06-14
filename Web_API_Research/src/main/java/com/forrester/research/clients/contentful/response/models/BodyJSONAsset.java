package com.forrester.research.clients.contentful.response.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BodyJSONAsset implements Serializable {
	
	private static final long serialVersionUID = 4727050329055862699L;

	private String id;
	private String title;
	private String fileType;
	private String fileSize;
	private Object dimensions;
	private String url;
	
	public String getTitle() {
		return title;
	}

	public String getFileType() {
		return fileType;
	}

	public String getFileSize() {
		return fileSize;
	}

	public String getUrl() {
		return url;
	}

	public String getId() {
		return id;
	}

	public Object getDimensions() {
		return dimensions;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDimensions(Object dimensions) {
		this.dimensions = dimensions;
	}

	@Override
	public String toString() {
		return "BodyJSONAsset [id=" + id + ", title=" + title + ", fileType=" + fileType + ", fileSize=" + fileSize
				+ ", dimensions=" + dimensions + ", url=" + url + "]";
	}
}