package com.forrester.index.elasticsearch.data;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.forrester.index.clients.research.response.BodyJsonAsset;
import com.forrester.index.clients.research.response.Figure;
import com.forrester.index.clients.taxonomy.response.Tag;
import com.forrester.index.clients.taxonomy.response.TaxonomyResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;

import static com.forrester.index.utils.TaxonomyUtils.*;

@Mapping(mappingPath = "/mappings/fd-research-mapping.json")
@Document(indexName = "fd-research-#{@indexNameProvider.getIndexSuffix()}")
public class ESResearchContainer extends AbstractTaggableContent implements Serializable {
	private static final long serialVersionUID = 44401433698843212L;
	public static final String DOC_TYPE_RESEARCH_CONTAINER = "RESEARCH_CONTAINER";

	@Id
	private String documentID;
	private String publishedDate;
	@Field(analyzer = "synonym_analyzer", type = FieldType.Text, searchAnalyzer = "synonym_search_analyzer")
	private String title;
	@Field(analyzer = "synonym_analyzer", type = FieldType.Text, searchAnalyzer = "synonym_search_analyzer")
	private String subTitle;
	@Field(analyzer = "synonym_analyzer", type = FieldType.Text, searchAnalyzer = "synonym_search_analyzer")
	private String researchAbstract;
	private String docType = ESResearchContainer.DOC_TYPE_RESEARCH_CONTAINER;
	private transient List<Map<String, Object>> authors = new LinkedList<>();
	private transient List<Map<String, Object>> contributors = new LinkedList<>();
	private transient List<Map<String, Object>> keyTakeAways = new LinkedList<>();
	private Searchables searchables;
	private long price;
	private int wordCount;
	private String abstractTitle;
	private String slug;
	private String priceType;
	private List<String> tags = new LinkedList<>();
	private List<Double> title_vector = new LinkedList<>();
	private List<Double> searchable_vector = new LinkedList<>();
	private long imageCount;
	private String ipType;
	private String updatedDate;
	private String archiveHtml;
	private String archiveJson;
	private String editorialImage;
	private String subType;
	private String abstractPreview;
	private String legacyContentType;
	private List<Figure> figures;

	private List<String> accessTypes = new LinkedList<>();
	private List<String> keywords = new LinkedList<>();
	private List<String> marketImperatives = new LinkedList<>();
	private List<String> primaryRoles = new LinkedList<>();
	private List<String> clientGroups = new LinkedList<>();
	private List<String> regions = new LinkedList<>();
	private String image;
	private transient Map<String, Object> publisher;
	private String contentId;
	private Set<String> topicsAgg = new HashSet<>();
	private Set<String> industryAgg = new HashSet<>();
	private Set<String> countryAgg = new HashSet<>();
	private Set<String> serviceAgg = new HashSet<>();
	private Boolean isArchived;
	private String legacyContentId;
	private String contentURL;
	private Double timeToRead;
	private String entryId;
	private String companiesInterviewed;
	private JsonNode downloadableAssets;
	private List<BodyJsonAsset> bodyJsonAssets;

	public List<Double> getTitle_vector() {
		return title_vector;
	}

	public void setTitle_vector(List<Double> title_vector) {
		this.title_vector = title_vector;
	}

	public List<Double> getSearchable_vector() {
		return searchable_vector;
	}

	public void setSearchable_vector(List<Double> searchable_vector) {
		this.searchable_vector = searchable_vector;
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

	public List<Map<String, Object>> getAuthors() {
		return authors;
	}

	public List<Map<String, Object>> getContributors() {
		return contributors;
	}

	public List<Map<String, Object>> getKeyTakeAways() {
		return keyTakeAways;
	}

	public List<String> getAccessTypes() {
		return accessTypes;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public String getImage() {
		return image;
	}

	public List<String> getPrimaryRoles() {
		return primaryRoles;
	}

	public List<String> getClientGroups() {
		return clientGroups;
	}

	public List<String> getRegions() {
		return regions;
	}

	public long getPrice() {
		return price;
	}

	public Map<String, Object> getPublisher() {
		return publisher;
	}

	public Searchables getSearchables() {
		return searchables;
	}

	public void setSearchables(Searchables searchables) {
		this.searchables = searchables;
	}

	public List<String> getMarketImperatives() {
		return marketImperatives;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
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

	public long getImageCount() {
		return imageCount;
	}

	public String getContentId() {
		return contentId;
	}

	public String getIpType() {
		return ipType;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public String getArchiveHtml() {
		return archiveHtml;
	}

	public String getArchiveJson() {
		return archiveJson;
	}

	public String getEditorialImage() {
		return editorialImage;
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

	public Set<String> getTopicsAgg() {
		return topicsAgg;
	}

	public void setTopicsAgg(Set<String> topicsAgg) {
		this.topicsAgg = topicsAgg;
	}

	public Set<String> getIndustryAgg() {
		return industryAgg;
	}

	public void setIndustryAgg(Set<String> industryAgg) {
		this.industryAgg = industryAgg;
	}

	public Set<String> getCountryAgg() {
		return countryAgg;
	}

	public void setCountryAgg(Set<String> countryAgg) {
		this.countryAgg = countryAgg;
	}

	public void setServiceAgg(Set<String> serviceAgg) {
		this.serviceAgg = serviceAgg;
	}
	
	public Boolean getIsArchived() {
        return isArchived;
    }
	
	public String getLegacyContentId() {
	    return legacyContentId;
	}

	public String getContentURL() {
		return contentURL;
	}

	public void setContentURL(String contentURL) {
		this.contentURL = contentURL;
	}

	public Double getTimeToRead() {
		return timeToRead;
	}

	public String getEntryId() {
		return entryId;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public String getCompaniesInterviewed() {
		return companiesInterviewed;
	}

	public void setCompaniesInterviewed(String companiesInterviewed) {
		this.companiesInterviewed = companiesInterviewed;
	}

	public List<Figure> getFigures() {
		return figures;
	}

	public JsonNode getDownloadableAssets() {
		return downloadableAssets;
	}

	public void setDownloadableAssets(JsonNode downloadableAssets) {
		this.downloadableAssets = downloadableAssets;
	}

	public List<BodyJsonAsset> getBodyJsonAssets() {
		return bodyJsonAssets;
	}

	public void setBodyJsonAssets(List<BodyJsonAsset> bodyJsonAssets) {
		this.bodyJsonAssets = bodyJsonAssets;
	}

	/**
	 * Builds Arguments based on the Taxonomy names and required levels for the current object
	 * Arguments included: topicsAgg (L3), industryAgg (L3, L4), countryAgg (L3, L4)
	 * If wanted in all contentTypes, this could be handled inside AbstractTaggableContent
	 * @param taxonomyResponse
	 */
	public void addTaxonomyAggs(TaxonomyResponse taxonomyResponse) {
		List<Tag> tags = taxonomyResponse.getTags();
		if (!tags.isEmpty()) {
			setTopicsAgg(buildTopicsAgg(tags));
			setIndustryAgg(buildIndustryAgg(tags));
			setCountryAgg(buildCountryAgg(tags));
			setServiceAgg(buildServiceAgg(tags));
		}
	}
}
