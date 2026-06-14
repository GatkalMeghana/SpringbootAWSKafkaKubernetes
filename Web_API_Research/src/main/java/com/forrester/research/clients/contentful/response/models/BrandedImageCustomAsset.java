package com.forrester.research.clients.contentful.response.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author manthanb
 * @version 1.0
 */

@JsonInclude(Include.NON_NULL)
public class BrandedImageCustomAsset implements Serializable {

	private static final long serialVersionUID = 1L;
	private String title;
	private String fileType;
	private String fileSize;
	private String url;
	private String extension;
	private boolean branded;
	private String containerId;
	
	public BrandedImageCustomAsset(){
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

	public String getExtension() {
		return extension;
	}

	public boolean isBranded() {
		return branded;
	}

	public String getContainerId() {
		return containerId;
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

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public void setBranded(boolean branded) {
		this.branded = branded;
	}

	public void setContainerId(String containerId) {
		this.containerId = containerId;
	}
}