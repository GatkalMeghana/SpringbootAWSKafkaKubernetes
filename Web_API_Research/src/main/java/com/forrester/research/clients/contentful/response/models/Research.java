package com.forrester.research.clients.contentful.response.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.CDAEntry;
import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.serializers.BodyJSONAssetsConverter;
import com.forrester.research.clients.contentful.serializers.CDAAssetListConverter;
import com.forrester.research.clients.contentful.serializers.CDAAssetSerializer;
import com.forrester.research.clients.contentful.serializers.CDAPublicImageSerializer;
import com.forrester.research.clients.contentful.serializers.CDARichDocumentSerializer;
import com.forrester.research.utils.DateUtils;

/**
 * @author sgopal
 * @version 2.0
 */
@JsonPropertyOrder({
	"overView",
	"id",
	"contentId",
	"subtype",
	"contentType",
	"title",
	"subtitle",
	"slug",
	"timeToRead",
	"seoTitle",
	"abstractTitle",
	"abstractPreview",
	"researchAbstract",
	"displayHeroImage",
	"heroColor",
	"editorialImage",
	"summaryTitleOverride",
	"seoMetaDescription",
	"authors",
	"contributors",
	"body",
	"bodyJson",
	"bodyJsonReferences",
	"bodyJsonAssets",
	"researchPdf",
	"priceType",
	"price",
	"publishedDate",
	"updatedDate",
	"contentChangeDateByUser",
	"ipType",
	"downloadableAssets",
	"recommendationsSectionTitle",
	"recommendedResearch",
	"keyTakeaways",
	"archiveHtml",
	"archiveJson",
	"contentUrl",
	"legacyContentType",
	"legacyContentId",
	"isArchived",
	"isCourtesyView",
	"archiveAssets"
})
@ContentfulEntryModel("research")
@JsonInclude(Include.NON_NULL)
public class Research implements Serializable{

	private static final long serialVersionUID = -6032328700806246783L;

	// From current Contentful fields limit. Should be moved to external configuration.
	private static final Integer SEO_TITLE_LIMIT = 58;
	private static final Integer SEO_META_DESC_LIMIT = 160;

	@JsonProperty("id")
	@ContentfulSystemField("id")
	private String id;

	@ContentfulField("contentId")
	private String contentId;

	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;

	@ContentfulField("slug")
	private String slug;

	@ContentfulField("timeToRead")
	private Double timeToRead;

	@ContentfulField("title")
	private String title;

	@ContentfulField("subtitle")
	private String subtitle;

	@ContentfulField("authors")
	private List<Analyst> authors;

	@ContentfulField("contributors")
	private List<Analyst> contributors;

	@ContentfulField("body")
	private List<CDAArray> body;

  @ContentfulField("bodyJson")
	private Object bodyJson;

	@ContentfulField("bodyJsonReferences")
	private List<CDAEntry> bodyJsonReferences;
	
	@JsonSerialize(converter = BodyJSONAssetsConverter.class)
	@ContentfulField("bodyJsonAssets")
	private List<CDAAsset> bodyJsonAssets;
	
  @ContentfulField("priceType")
	private String priceType;

	@ContentfulField("ipType")
	private String ipType;

	@ContentfulField("recommendationsSectionTitle")
	private String recommendationsSectionTitle;

	@ContentfulField("recommendedResearch")
	@JsonIgnore
	private List<CDAEntry> recommendedResearchContent;

	//For Json Response
	private List<RecommendedResearch> recommendedResearch;

	@ContentfulField("summaryTitleOverride")
	private String abstractTitle;

	@ContentfulField("summaryPreview")
	private String abstractPreview;

	@ContentfulField("summary")
	@JsonSerialize(using = CDARichDocumentSerializer.class)
	private CDARichDocument researchAbstract;

	@JsonSerialize(using = CDAPublicImageSerializer.class)
	@ContentfulField("editorialImage")
	@JsonIgnore
	private CDAAsset editorialImageContent;

	private String editorialImage;

	@ContentfulField("price")
	private Double price;

	@ContentfulField("publishDate")
	private String publishedDate;

	// system updated date
	@ContentfulSystemField("updatedAt")
	private String updatedDate;

	// content updated by user date
	@ContentfulField("updatedDate")
	private String contentChangeDateByUser;

	@ContentfulField("keyTakeaways")
	private CDARichDocument keyTakeaways;

	private boolean overView;

	@JsonSerialize(converter = CDAAssetListConverter.class)
	@ContentfulField("downloadableAssets")
	private List<CDAAsset> downloadableAssets;

	@ContentfulField("seoMetaDescription")
	private String seoMetaDescription;

	@ContentfulField("seoTitle")
	private String seoTitle;

	@ContentfulField("subtype")
	private String subtype;

	@ContentfulField("archiveHtml")
	@JsonSerialize(using = CDARichDocumentSerializer.class)
	private CDARichDocument archiveHtml;

	@ContentfulField("archiveJson")
	@JsonSerialize(using = CDARichDocumentSerializer.class)
	private CDARichDocument archiveJson;

	@ContentfulField("legacyContentType")
	private String legacyContentType;

	@ContentfulField("legacyContentId")
	private String legacyContentId;

	@ContentfulField("isArchived")
	private Boolean isArchived;

	private Boolean isCourtesyView;

	@JsonSerialize(using = CDAAssetSerializer.class)
	@ContentfulField("researchPdf")
	private CDAAsset researchPdf;

	@ContentfulField("heroColor")
	private String heroColor;

	@ContentfulField("displayHeroImage")
	private Boolean displayHeroImage;

	@ContentfulField("archiveAssets")
	private List<CDAArray> archiveAssets;

	public Boolean getDisplayHeroImage() {
		return displayHeroImage;
	}

	public void setDisplayHeroImage(Boolean displayHeroImage) {
		this.displayHeroImage = displayHeroImage;
	}

	public CDAAsset getResearchPdf() {
		return researchPdf;
	}

	public void setResearchPdf(CDAAsset researchPdf) {
		this.researchPdf = researchPdf;
	}

	public Boolean getIsCourtesyView() {
		return isCourtesyView != null && isCourtesyView;
	}

	public void setIsCourtesyView(Boolean isCourtesyView) {
		this.isCourtesyView = isCourtesyView;
	}

	public CDARichDocument getArchiveJson() {
		return archiveJson;
	}

	public void setArchiveJson(CDARichDocument archiveJson) {
		this.archiveJson = archiveJson;
	}

	public CDARichDocument getArchiveHtml() {
		return archiveHtml;
	}

	public void setArchiveHtml(CDARichDocument archiveHtml) {
		this.archiveHtml = archiveHtml;
	}

	public String getSubtype() {
		String defaultSubtype = "Report";
		return this.subtype != null ? this.subtype: defaultSubtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	private String contentUrl;

	public String getSeoTitle() {
		if (StringUtils.isEmpty(seoTitle)) {
			return getTruncatedSeoData(title, Boolean.TRUE);
		}
		return seoTitle;
	}
	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}
	public String getSeoMetaDescription() {
			if(StringUtils.isEmpty(seoMetaDescription)) {
				return getTruncatedSeoData(abstractPreview, Boolean.FALSE);
			}
			return seoMetaDescription;
	}

	public void setSeoMetaDescription(String seoMetaDescription) {
		this.seoMetaDescription = seoMetaDescription;
	}

	public boolean isOverView() {
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Integer getTimeToRead() {
		if(null != timeToRead) {
			return timeToRead.intValue();
		}
		return null;
	}

	public void setTimeToRead(Double timeToRead) {
		this.timeToRead = timeToRead;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public List<Analyst> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Analyst> authors) {
		this.authors = authors;
	}

	public List<Analyst> getContributors() {
		return contributors;
	}

	public void setContributors(List<Analyst> contributors) {
		this.contributors = contributors;
	}

	public List<CDAArray> getBody() {
		return body;
	}

	public void setBody(List<CDAArray> body) {
		this.body = body;
	}
	
	public Object getBodyJson() {
		return bodyJson;
	}
	
	public void setBodyJson(Object bodyJson) {
		this.bodyJson = bodyJson;
	}
	
	public List<CDAEntry> getBodyJsonReferences() {
		return bodyJsonReferences;
	}

	public void setBodyJsonReferences(List<CDAEntry> bodyJsonReferences) {
		this.bodyJsonReferences = bodyJsonReferences;
	}

	public List<CDAAsset> getBodyJsonAssets() {
		return bodyJsonAssets;
	}

	public void setBodyJsonAssets(List<CDAAsset> bodyJsonAssets) {
		this.bodyJsonAssets = bodyJsonAssets;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public String getIpType() {
		return ipType;
	}

	public void setIpType(String ipType) {
		this.ipType = ipType;
	}

	public String getHeroColor() {
		String defaultHeroColor = "Dark";
		return this.heroColor != null ? this.heroColor: defaultHeroColor;
	}

	public void setHeroColor(String heroColor) {
		this.heroColor = heroColor;
	}

	public String getRecommendationsSectionTitle() {
		return recommendationsSectionTitle;
	}

	public void setRecommendationsSectionTitle(String recommendationsSectionTitle) {
		this.recommendationsSectionTitle = recommendationsSectionTitle;
	}

	public List<CDAEntry> getRecommendedResearchContent() {
		return recommendedResearchContent;
	}

	public void setRecommendedResearchContent(List<CDAEntry> recommendedResearchContent) {
		this.recommendedResearchContent = recommendedResearchContent;
	}

	public List<RecommendedResearch> getRecommendedResearch() {
		return recommendedResearch;
	}

	public void setRecommendedResearch(List<RecommendedResearch> recommendedResearch) {
		this.recommendedResearch = recommendedResearch;
	}

	public String getAbstractTitle() {
		return abstractTitle;
	}

	public void setAbstractTitle(String abstractTitle) {
		this.abstractTitle = abstractTitle;
	}

	public String getAbstractPreview() {
		return abstractPreview;
	}

	public void setAbstractPreview(String abstractPreview) {
		this.abstractPreview = abstractPreview;
	}

	public CDARichDocument getResearchAbstract() {
		return researchAbstract;
	}

	public void setResearchAbstract(CDARichDocument researchAbstract) {
		this.researchAbstract = researchAbstract;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

	public CDARichDocument getKeyTakeaways() {
		return keyTakeaways;
	}

	public void setKeyTakeaways(CDARichDocument keyTakeaways) {
		this.keyTakeaways = keyTakeaways;
	}

	public String getPublishedDate() {
		return DateUtils.convertDateToISOFormat(publishedDate);
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}
	
	public String getContentChangeDateByUser() {
		return contentChangeDateByUser;
	}

	public void setContentChangeDateByUser(String contentChangeDateByUser) {
		this.contentChangeDateByUser = contentChangeDateByUser;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public List<CDAAsset> getDownloadableAssets() {
		return downloadableAssets;
	}

	public void setDownloadableAssets(List<CDAAsset> downloadableAssets) {
		this.downloadableAssets = downloadableAssets;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getLegacyContentType() {
		return legacyContentType;
	}

	public void setLegacyContentType(String legacyContentType) {
		this.legacyContentType = legacyContentType;
	}

	public String getLegacyContentId() {
		return legacyContentId;
	}

	public void setLegacyContentId(String legacyContentId) {
		this.legacyContentId = legacyContentId;
	}

	public Boolean getIsArchived() {
		return isArchived != null && isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}

	public List<CDAArray> getArchiveAssets() {
		return archiveAssets;
	}

	public void setArchiveAssets(List<CDAArray> archiveAssets) {
		this.archiveAssets = archiveAssets;
	}

	@JsonIgnore
	public CDAAsset getEditorialImageContent() {
		return editorialImageContent;
	}

	public void setEditorialImageContent(CDAAsset editorialImageContent) {
		this.editorialImageContent = editorialImageContent;
	}

	public String getEditorialImage() {
		return editorialImage;
	}

	public void setEditorialImage(String editorialImage) {
		this.editorialImage = editorialImage;
	}

	@Override
	public String toString() {
		return "Research [id=" + id + ", contentId=" + contentId + ", contentType=" + contentType + ", slug=" + slug
				+ ", timeToRead=" + timeToRead + ", title=" + title + ", subtitle=" + subtitle + ", authors=" + authors
				+ ", contributors=" + contributors + ", body=" + body + ", bodyJson=" + bodyJson + ", bodyJsonReferences=" + bodyJsonReferences
				+ ", bodyJsonAssets=" + bodyJsonAssets + ", priceType=" + priceType + ", ipType=" + ipType
				+ ", recommendationsSectionTitle=" + recommendationsSectionTitle + ", recommendedResearchContent="
				+ recommendedResearchContent + ", recommendedResearch=" + recommendedResearch + ", abstractTitle="
				+ abstractTitle + ", abstractPreview=" + abstractPreview + ", researchAbstract=" + researchAbstract
				+ ", editorialImage=" + editorialImage + ", price=" + price + ", publishedDate=" + publishedDate
				+ ", updatedDate=" + updatedDate + ", contentChangeDateByUser=" + contentChangeDateByUser + ", keyTakeaways=" + keyTakeaways + ", overView=" + overView
				+ ", downloadableAssets=" + downloadableAssets + ", seoMetaDescription=" + seoMetaDescription
				+ ", seoTitle=" + seoTitle + ", subtype=" + subtype + ", archiveHtml=" + archiveHtml + ", archiveJson="
				+ archiveJson + ", legacyContentType=" + legacyContentType + ", legacyContentId=" + legacyContentId
				+ ", isArchived=" + isArchived + ", isCourtesyView=" + isCourtesyView + ", researchPdf=" + researchPdf
				+ ", heroColor=" + heroColor + ", displayHeroImage=" + displayHeroImage + ", archiveAssets="
				+ archiveAssets + ", contentUrl=" + contentUrl + "]";
	}

	public String getTruncatedSeoData(String dataToPopulate, Boolean titleOrDesc) {
		Integer seoLimit = titleOrDesc ? SEO_TITLE_LIMIT : SEO_META_DESC_LIMIT;

		if (dataToPopulate.length() > seoLimit) {
			String[] truncatedSeoData = dataToPopulate.substring(0, seoLimit).split(" ");
			truncatedSeoData = ArrayUtils.remove(truncatedSeoData, truncatedSeoData.length - 1);

			return String.join(" ", truncatedSeoData) + "...";
		}

		return dataToPopulate;
	}
}