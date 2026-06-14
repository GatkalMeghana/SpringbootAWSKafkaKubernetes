package com.forrester.index.clients.research.response;

import java.io.Serializable;

public class RecommendedResearch implements Serializable {
	private String id;
	private String title;
	private String contentType;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContentType() {
		return contentType;
	}
}
