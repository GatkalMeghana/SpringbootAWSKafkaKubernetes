package com.forrester.index.clients.forrservice.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.forrester.index.clients.contentful.response.helpers.DescriptionDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForrServiceResponse implements Serializable {

	private static final long serialVersionUID = 2354263513227329613L;

	private String contentId;
	private String contentType;
	private String contentURL;
	@JsonDeserialize(using = DescriptionDeserializer.class)
	private String description;
	private String functionalDiscipline;
	private String heroImage;
	private String publishedDate;
	private String title;
	private String slug;
	private String taxonomyID;
	@JsonProperty("id")
	private String entryId;

	public String getContentId() {
		return contentId;
	}

	public String getContentType() {
		return contentType;
	}

	public String getContentURL() {
		return contentURL;
	}

	public String getDescription() {
		return description;
	}

	public String getFunctionalDiscipline() {
		return functionalDiscipline;
	}

	public String getHeroImage() {
		return heroImage;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public String getTitle() {
		return title;
	}

	public String getSlug() {
		return slug;
	}

	public String getTaxonomyID() {
		return taxonomyID;
	}

	public String getEntryId() {
		return entryId;
	}

	@Override
	public String toString() {
		return "ForrServiceResponse{" +
				"contentId='" + contentId + '\'' +
				", contentType='" + contentType + '\'' +
				", contentURL='" + contentURL + '\'' +
				", description='" + description + '\'' +
				", functionalDiscipline='" + functionalDiscipline + '\'' +
				", heroImage='" + heroImage + '\'' +
				", publishedDate='" + publishedDate + '\'' +
				", title='" + title + '\'' +
				", slug='" + slug + '\'' +
				", taxonomyID='" + taxonomyID + '\'' +
				", entryId='" + entryId + '\'' +
				'}';
	}
}
