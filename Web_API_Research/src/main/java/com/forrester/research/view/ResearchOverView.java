package com.forrester.research.view;

import com.contentful.java.cda.CDAArray;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.response.models.Analyst;
import com.forrester.research.clients.contentful.response.models.Research;
import com.forrester.research.clients.contentful.serializers.CDARichDocumentSerializer;

import java.io.Serializable;
import java.util.List;

@JsonPropertyOrder({
	"overView",
    "id",
	"contentId",
	"subtype",
	"slug",
    "title",
    "subtitle",
    "abstractTitle",
    "abstractPreview",
    "researchAbstract",
    "authors",
    "contributors",
	"body",
    "priceType",
	"price",
    "editorialImage",
    "publishedDate",
    "legacyContentType",
	"isArchived",
	"legacyContentId"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResearchOverView implements Serializable {

	private static final long serialVersionUID = 4961845276491303897L;

	private String id;

	private String contentId;

	private String slug;

	private String title;

	private String subtitle;

	private String abstractTitle;

	private String abstractPreview;

	private List<Analyst> authors;

	private List<Analyst> contributors;

	private String priceType;

	private Double price;

    private String publishedDate;

	private String editorialImage;

	private boolean overView;

	private Boolean isArchived;

	private String legacyContentType;

	private String subtype;

	private String legacyContentId;

	private String ipType ;

	private String seoMetaDescription;

	private String seoTitle;

	@JsonSerialize(using = CDARichDocumentSerializer.class)
	private CDARichDocument researchAbstract;

	@ContentfulField("body")
	private List<CDAArray> body;

	public ResearchOverView(Object content) {

		Research research = (Research) content;
		this.setId(research.getId());
		this.setContentId(research.getContentId());
		this.setSlug(research.getSlug());
		this.setTitle(research.getTitle());
		this.setSubtitle(research.getSubtitle());
		this.setAbstractTitle(research.getAbstractTitle());
		this.setAbstractPreview(research.getAbstractPreview());
		this.setAuthors(research.getAuthors());
		this.setContributors(research.getContributors());
		if (null != research.getEditorialImage()) {
			this.setEditorialImage(research.getEditorialImage());
		}
		this.setPriceType(research.getPriceType());
		this.setPrice(research.getPrice());
		this.setPublishedDate(research.getPublishedDate());
		this.setIsArchived(research.getIsArchived());
		this.setLegacyContentType(research.getLegacyContentType());
		this.setSubtype(research.getSubtype());
		this.setLegacyContentId(research.getLegacyContentId());
		this.setIpType(research.getIpType());
		this.setSeoTitle(research.getSeoTitle());
		this.setSeoMetaDescription(research.getSeoMetaDescription());
        this.setResearchAbstract(research.getResearchAbstract());
		this.setBody(research.getBody());
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

	public void setContentId(String id) {
		this.contentId = id;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
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

	public String getAbstractTitle() {
		return abstractTitle;
	}

	public String getAbstractPreview() {
		return abstractPreview;
	}

	public void setAbstractPreview(String abstractPreview) {
		this.abstractPreview = abstractPreview;
	}

	public void setAbstractTitle(String abstractTitle) {
		this.abstractTitle = abstractTitle;
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

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setContributors(List<Analyst> contributors) {
		this.contributors = contributors;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public String getEditorialImage() {
		return editorialImage;
	}

	public void setEditorialImage(String editorialImage) {
		this.editorialImage = editorialImage;
	}

	public boolean isOverView() {
		return true;
	}

	public Boolean getIsArchived() {
		return isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}

	public String getLegacyContentType() {
		return legacyContentType;
	}
	public void setLegacyContentType(String legacyContentType) {
		this.legacyContentType = legacyContentType;
	}


	public String getSubtype() {
		return subtype;
	}

	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}

	public String getLegacyContentId() {
		return legacyContentId;
	}

	public void setLegacyContentId(String legacyContentId) {
		this.legacyContentId = legacyContentId;
	}

	public String getIpType() {
		return ipType;
	}

	public void setIpType(String ipType) {
		this.ipType = ipType;
	}

	@Override
	public String toString() {
		return "ResearchOverView [id=" + id + ", contentId=" + contentId + ", slug=" + slug + ", title=" + title + ", subtitle="
				+ subtitle + ", abstractTitle=" + abstractTitle + ", abstractPreview=" + abstractPreview + ", researchAbstract=" + researchAbstract + ", authors="
				+ authors + ", contributors=" + contributors + ", priceType=" + priceType + ", price=" + price
				+ ", publishedDate=" + publishedDate + ", editorialImage=" + editorialImage + ", overView=" + overView
				+ ", isArchived=" + isArchived + ", legacyContentType=" + legacyContentType + ", subtype=" + subtype
				+ ", legacyContentId=" + legacyContentId + "]";
	}

	public String getSeoMetaDescription() {
		return seoMetaDescription;
	}

	public void setSeoMetaDescription(String seoMetaDescription) {
		this.seoMetaDescription = seoMetaDescription;
	}

	public String getSeoTitle() {
		return seoTitle;
	}

	public void setSeoTitle(String seoTitle) {
		this.seoTitle = seoTitle;
	}

    public CDARichDocument getResearchAbstract() {
        return researchAbstract;
    }

    public void setResearchAbstract(CDARichDocument researchAbstract) {
        this.researchAbstract = researchAbstract;
    }

	public List<CDAArray> getBody() {
		return body;
	}

	public void setBody(List<CDAArray> body) {
		this.body = body;
	}
}