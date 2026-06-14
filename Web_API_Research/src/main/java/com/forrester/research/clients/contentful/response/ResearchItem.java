package com.forrester.research.clients.contentful.response;

import java.io.Serializable;
import java.util.Date;

public class ResearchItem implements Serializable {

	private static final long serialVersionUID = -9108302201780597745L;

	private String contentId;

	private SysProperties sys;

	private Date publishDate;
	
	public String getContentId() {
		return contentId;
	}

	public SysProperties getSys() {
		return sys;
	}
	
	public Date getPublishDate() {
		return publishDate;
	}
}
