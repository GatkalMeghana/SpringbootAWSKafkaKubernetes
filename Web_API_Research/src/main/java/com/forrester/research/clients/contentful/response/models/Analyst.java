package com.forrester.research.clients.contentful.response.models;

import java.util.Map;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.serializers.CDAAssetSerializer;

@JsonPropertyOrder({ "id", "contentType", "title", "contentId", "firstName", "lastName", "imageUrl" })

@ContentfulEntryModel("analyst")
@JsonInclude(Include.NON_NULL)
public class Analyst {

	@JsonProperty("id")
	@ContentfulSystemField("id")
	private String id;

	@ContentfulSystemField("contentType")
	private Map<String, Object> contentType;

	@ContentfulField("contentId")
	private String contentId;

	@ContentfulField("jobTitle")
	private String title;

	@ContentfulField("firstName")
	private String firstName;

	@ContentfulField("lastName")
	private String lastName;

	@ContentfulField("analystImage")
	private String imageUrl;
	
	@ContentfulField("active")
	private Boolean active;
	
	@JsonSerialize(using = CDAAssetSerializer.class)
	@ContentfulField("analystImages")
	private CDAAsset analystImages;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Boolean isActive() {
		if(null!=active) {
			return active;
		}
		else 
			return false;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public CDAAsset getAnalystImages() {
		return analystImages;
	}

	public void setAnalystImages(CDAAsset analystImages) {
		this.analystImages = analystImages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((contentId == null) ? 0 : contentId.hashCode());
		result = prime * result + ((contentType == null) ? 0 : contentType.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Analyst other = (Analyst) obj;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (contentId == null) {
			if (other.contentId != null)
				return false;
		} else if (!contentId.equals(other.contentId))
			return false;
		if (contentType == null) {
			if (other.contentType != null)
				return false;
		} else if (!contentType.equals(other.contentType))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (title == null) {
			return other.title == null;
		} else {
			return title.equals(other.title);
		}
	}

	@Override
	public String toString() {
		return "Analyst [id=" + id + ", contentType=" + contentType + ", contentId=" + contentId + ", title=" + title
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", imageUrl=" + imageUrl + ", active="
				+ active + ", analystImages=" + analystImages + "]";
	}


}