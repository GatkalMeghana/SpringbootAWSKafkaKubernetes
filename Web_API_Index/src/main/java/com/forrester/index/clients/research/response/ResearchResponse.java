package com.forrester.index.clients.research.response;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResearchResponse implements Serializable {

	private static final long serialVersionUID = -7248803190618322471L;
	private String id;
	private String contentId;
	private String contentType;
	private String title;
	private String subtitle;
	private String slug;
	private String abstractTitle;
	private String researchAbstract;
	private List<Author> authors  = new LinkedList<>();
	private List<Author> contributors  = new LinkedList<>();
	private List<ResearchContainer> body  = new LinkedList<>();
	private List<ResearchContainer> bodyJson  = new LinkedList<>();
	private String priceType;
	private Double price;
	private List<RecommendedResearch> recommendedResearch = new LinkedList<>();
	private String keyTakeaways;
	private List<String> tags = new LinkedList<>();
	private String publishedDate;
	private String editorialImage;
	private String ipType;
	private String updatedDate;
	private String contentUrl;
	private String archiveHtml;
	private String archiveJson;
	private String subtype;
	private String abstractPreview;
	private String legacyContentType;
	private Boolean isArchived;
	private String legacyContentId;
	private Double timeToRead;
	private JsonNode downloadableAssets;
	private List<BodyJsonAsset> bodyJsonAssets;

	public String getId() {
		return id;
	}

	public String getContentId() {
		return contentId;
	}

	public String getContentType() {
		return contentType;
	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getSlug() {
		return slug;
	}

	public String getAbstractTitle() {
		return abstractTitle;
	}

	public String getResearchAbstract() {
		return researchAbstract;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public List<Author> getContributors() {
		return contributors;
	}

	public List<ResearchContainer> getBody() {
		return body;
	}

	public List<ResearchContainer> getBodyJson() {
		return bodyJson;
	}

	public String getPriceType() {
		return priceType;
	}

	public Double getPrice() {
		return price;
	}

	public List<RecommendedResearch> getRecommendedResearch() {
		return recommendedResearch;
	}

	public String getKeyTakeaways() {
		return keyTakeaways;
	}

	public List<String> getTags() {
		return tags;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public String getEditorialImage() {
		return editorialImage;
	}

	public String getIpType() {
		return ipType;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public String getArchiveHtml() {
		return archiveHtml;
	}

	public String getArchiveJson() {
		return archiveJson;
	}

	public String getSubtype() {
		return subtype;
	}

	public String getAbstractPreview() {
		return abstractPreview;
	}

	public String getLegacyContentType() {
		return legacyContentType;
	}

    public Boolean getIsArchived() {
        return isArchived;
    }
    
    public String getLegacyContentId() {
        return legacyContentId;
    }

	public Double getTimeToRead() {
		return timeToRead;
	}

	public JsonNode getDownloadableAssets() {
		return downloadableAssets;
	}

	public List<BodyJsonAsset> getBodyJsonAssets() {
		return bodyJsonAssets;
	}
}
