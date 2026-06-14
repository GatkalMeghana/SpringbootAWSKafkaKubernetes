package com.forrester.research.clients.contentful.response.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Mesfin Demissie
 * @version 1.0
 */
@JsonInclude(Include.NON_NULL)
public class CustomAsset implements Serializable {

	private static final long serialVersionUID = 1L;

	private String title;

	private String fileType;

	private String fileSize;

	private String url;

	public CustomAsset() {
		// Default constructor for empty instances.
	}

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
}