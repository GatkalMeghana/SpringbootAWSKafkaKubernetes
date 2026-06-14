package com.forrester.index.clients.research.response;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ResearchContainerDTO implements Serializable {
    private static final long serialVersionUID = -3849633503647640435L;
    private String documentID;
    private String publishedDate;
    private String title;
    private String subTitle;
    private String researchAbstract;
    private Long price;
    private List<Map<String, Object>> authors = new LinkedList<>();
    private List<Map<String, Object>> contributors = new LinkedList<>();
    private List<Map<String, Object>> keyTakeAways = new LinkedList<>();
    private String abstractTitle;
    private String slug;
    private String priceType;
    private List<String> tags;
    private long imageCount;
    private String ipType;
    private String updatedDate;
    private String editorialImage;
    private String archiveHtml;
    private String archiveJson;
    private String subType;
    private String abstractPreview;
    private String legacyContentType;
    private Boolean isArchived;
    private String legacyContentId;
    private String contentId;
    private Double timeToRead;
    private String entryId;
    private String companiesInterviewed;
    private List<Figure> figures;

    private transient List<RecommendedResearch> recommendedResearch;
    private transient String body;
    private transient String bodyJson;
    private JsonNode downloadableAssets;
    private List<BodyJsonAsset> bodyJsonAssets;

    public static Builder builder() {
        return new Builder();
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getResearchAbstract() {
        return researchAbstract;
    }

    public Long getPrice() {
        return price;
    }

    public List<Map<String, Object>> getAuthors() {
        return authors;
    }

    public List<Map<String, Object>> getContributors() {
        return contributors;
    }

    public List<Map<String, Object>> getKeyTakeAways() {
        return keyTakeAways;
    }

    public String getAbstractTitle() {
        return abstractTitle;
    }

    public String getSlug() {
        return slug;
    }

    public String getPriceType() {
        return priceType;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<RecommendedResearch> getRecommendedResearch() {
        return recommendedResearch;
    }

    public String getBody() {
        return body;
    }

    public String getBodyJson() {
        return bodyJson;
    }

    public long getImageCount() {
		return imageCount;
	}
    
    public String getIpType() {
        return ipType;
    }
    
    public String getUpdatedDate() {
		return updatedDate;
	}

	public String getEditorialImage() {
		return editorialImage;
	}

	public String getArchiveHtml() {
		return archiveHtml;
	}

	public String getArchiveJson() {
		return archiveJson;
	}

	public String getSubType() {
		return subType;
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
	
    public String getContentId() {
        return contentId;
    }

	public Double getTimeToRead() {
		return timeToRead;
	}

    public String getEntryId() {
        return entryId;
    }

    public String getCompaniesInterviewed() {
        return companiesInterviewed;
    }

    public List<Figure> getFigures() {
        return figures;
    }

    public JsonNode getDownloadableAssets() {
        return downloadableAssets;
    }

    public List<BodyJsonAsset> getBodyJsonAssets() {
        return bodyJsonAssets;
    }

    public static final class Builder {
        private String documentID;
        private String publishedDate;
        private String title;
        private String subTitle;
        private String researchAbstract;
        private Long price;
        private transient List<Map<String, Object>> authors = new LinkedList<>();
        private transient List<Map<String, Object>> contributors = new LinkedList<>();
        private transient List<Map<String, Object>> keyTakeAways = new LinkedList<>();
        private transient String abstractTitle;
        private transient String slug;
        private transient String priceType;
        private transient List<RecommendedResearch> recommendedResearch;
        private transient String body;
        private transient String bodyJson;
        private transient List<String> tags;
        private long imageCount;
        private String ipType;
        private String updatedDate;
        private String editorialImage;
        private String archiveHtml;
        private String archiveJson;
        private String subType;
        private String abstractPreview;
        private String legacyContentType;
        private Boolean isArchived;
        private String legacyContentId;
        private String contentId;
        private Double timeToRead;
        private String entryId;
        private String companiesInterviewed;
        public List<Figure> figures;
        private JsonNode downloadableAssets;
        private List<BodyJsonAsset> bodyJsonAssets;

        private Builder() {
        }

        public Builder withDocumentID(String documentID) {
            this.documentID = documentID;
            return this;
        }

        public Builder withPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withSubTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder withResearchAbstract(String researchAbstract) {
            this.researchAbstract = researchAbstract;
            return this;
        }

        public Builder withPrice(Long price) {
            this.price = price;
            return this;
        }

        public Builder withAuthors(List<Map<String, Object>> authors) {
            this.authors = authors;
            return this;
        }

        public Builder withContributors(List<Map<String, Object>> contributors) {
            this.contributors = contributors;
            return this;
        }

        public Builder withKeyTakeAways(List<Map<String, Object>> keyTakeAways) {
            this.keyTakeAways = keyTakeAways;
            return this;
        }

        public Builder withAbstractTitle(String abstractTitle) {
            this.abstractTitle = abstractTitle;
            return this;
        }

        public Builder withSlug(String slug) {
            this.slug = slug;
            return this;
        }

        public Builder withPriceType(String priceType) {
            this.priceType = priceType;
            return this;
        }

        public Builder withRecommendedResearch(List<RecommendedResearch> recommendedResearch) {
            this.recommendedResearch = recommendedResearch;
            return this;
        }

        public Builder withBody(String body) {
            this.body = body;
            return this;
        }

        public Builder withBodyJson(String bodyJson) {
            this.bodyJson = bodyJson;
            return this;
        }

        public Builder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withImageCount(long imageCount) {
            this.imageCount = imageCount;
            return this;
        }
        
        public Builder withIpType(String ipType) {
            this.ipType = ipType;
            return this;
        }
        
        public Builder withUpdatedDate(String updatedDate) {
            this.updatedDate = updatedDate;
            return this;
        }
        
        public Builder withEditorialImage(String editorialImage) {
            this.editorialImage = editorialImage;
            return this;
        }
        
        public Builder withArchiveHtml(String archiveHtml) {
            this.archiveHtml = archiveHtml;
            return this;
        }
        
        public Builder withArchiveJson(String archiveJson) {
            this.archiveJson = archiveJson;
            return this;
        }
        
        public Builder withSubType(String subType) {
            this.subType = subType;
            return this;
        }
        
        public Builder withAbstractPreview(String abstractPreview) {
            this.abstractPreview = abstractPreview;
            return this;
        }
        
        public Builder withLegacyContentType(String legacyContentType) {
            this.legacyContentType = legacyContentType;
            return this;
        }        
        
        public Builder withIsArchived(Boolean isArchived) {
            this.isArchived = isArchived;
            return this;
        }
        
        public Builder withLegacyContentId(String legacyContentId) {
            this.legacyContentId = legacyContentId;
            return this;
        }
        
        public Builder withContentId(String contentId) {
            this.contentId = contentId;
            return this;
        }
        
        public Builder withTimeToRead(Double timeToRead) {
        	this.timeToRead = timeToRead;
        	return this;
        }

        public Builder withEntryId(String entryId){
            this.entryId = entryId;
            return this;
        }

        public Builder withCompaniesInterviewed(String companiesInterviewed){
            this.companiesInterviewed = companiesInterviewed;
            return this;
        }

        public Builder withFigures(List<Figure> figures){
            this.figures = figures;
            return this;
        }

        public Builder withDownloadableAssets(JsonNode downloadableAssets){
            this.downloadableAssets = downloadableAssets;
            return this;
        }

        public Builder withBodyJsonAssets(List<BodyJsonAsset> bodyJsonAssets){
            this.bodyJsonAssets = bodyJsonAssets;
            return this;
        }



        public ResearchContainerDTO build() {
            ResearchContainerDTO researchContainerDTO = new ResearchContainerDTO();
            researchContainerDTO.slug = this.slug;
            researchContainerDTO.authors = this.authors;
            researchContainerDTO.publishedDate = this.publishedDate;
            researchContainerDTO.recommendedResearch = this.recommendedResearch;
            researchContainerDTO.price = this.price;
            researchContainerDTO.contributors = this.contributors;
            researchContainerDTO.documentID = this.documentID;
            researchContainerDTO.title = this.title;
            researchContainerDTO.researchAbstract = this.researchAbstract;
            researchContainerDTO.subTitle = this.subTitle;
            researchContainerDTO.body = this.body;
            researchContainerDTO.priceType = this.priceType;
            researchContainerDTO.keyTakeAways = this.keyTakeAways;
            researchContainerDTO.abstractTitle = this.abstractTitle;
            researchContainerDTO.tags = this.tags;
            researchContainerDTO.imageCount = this.imageCount;
            researchContainerDTO.ipType = this.ipType;
            researchContainerDTO.updatedDate = this.updatedDate;
            researchContainerDTO.editorialImage = this.editorialImage;
            researchContainerDTO.archiveHtml = this.archiveHtml;
            researchContainerDTO.archiveJson = this.archiveJson;
            researchContainerDTO.subType = this.subType;
            researchContainerDTO.abstractPreview = this.abstractPreview;
            researchContainerDTO.legacyContentType = this.legacyContentType;
            researchContainerDTO.isArchived = this.isArchived;
            researchContainerDTO.legacyContentId = this.legacyContentId;
            researchContainerDTO.contentId = this.contentId;
            researchContainerDTO.timeToRead = this.timeToRead;
            researchContainerDTO.entryId = this.entryId;
            researchContainerDTO.companiesInterviewed = this.companiesInterviewed;
            researchContainerDTO.bodyJson = this.bodyJson;
            researchContainerDTO.figures = this.figures;
            researchContainerDTO.downloadableAssets = this.downloadableAssets;
            researchContainerDTO.bodyJsonAssets = this.bodyJsonAssets;
            return researchContainerDTO;
        }
    }
}
