package com.forrester.research.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.forrester.research.clients.contentful.response.models.Analyst;
import com.forrester.research.clients.contentful.response.models.RecommendedResearch;
import com.forrester.research.clients.contentful.response.models.Research;

/**
 * @author sgopal
 */

@JsonPropertyOrder({
	"contentId",
	"contentType",
	"slug",
    "title",
    "subtitle",
    "abstractTitle",
    "researchAbstract",
    "authors",
    "contributors",
    "contentURL",
    "priceType",
    "price",
    "ipType",
    "editorialImageURL",
    "dates",
    "recommendedResearch"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResearchReportMetadata implements Serializable {

	private static final long serialVersionUID = 4961845276491303897L;

	private String contentId;
	
	private String contentType;
	
	private String slug;
	
	private String title;
	
	private String subtitle;
	
	private String abstractTitle;
	
	private List<String> authors;
	
	private List<String> contributors;
	
    private List<ContentDate> dates;
    
	private String editorialImageURL;
	
	private String contentURL;
	
	private Double price;
	
	private String priceType;
	
	private String ipType;
	
	private String researchAbstract;
	
	private List<RecommendedResearch> recommendedResearch;
	
	public ResearchReportMetadata(Object content) {

		Research research = (Research) content;
		this.setContentId(research.getContentId());
		this.setSlug(research.getSlug());
		this.setTitle(research.getTitle());
		this.setSubtitle(research.getSubtitle());
		this.setAbstractTitle(research.getAbstractTitle());
		this.setIpType(research.getIpType());
		this.setPrice(research.getPrice());		
		this.setPriceType(research.getPriceType());
		this.setRecommendedResearch(research.getRecommendedResearch());
		this.setAuthors(populateNames(research.getAuthors()));
		this.setContributors(populateNames(research.getContributors()));
	}

	private List<String> populateNames(List<Analyst> authors) {
		List<String> names = new ArrayList<>();
		if(null != authors && !authors.isEmpty()) {
			for(Analyst a : authors) {
				String authorName = a.getFirstName() +" "+ a.getLastName();
				names.add(authorName);
			}
		}
		return names;
	}


	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public void setAbstractTitle(String abstractTitle) {
		this.abstractTitle = abstractTitle;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public void setContributors(List<String> contributors) {
		this.contributors = contributors;
	}

	public void setDates(List<ContentDate> dates) {
		this.dates = dates;
	}

	public void setEditorialImageURL(String editorialImageURL) {
		this.editorialImageURL = editorialImageURL;
	}

	public void setContentURL(String contentURL) {
		this.contentURL = contentURL;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public void setIpType(String ipType) {
		this.ipType = ipType;
	}


	public void setResearchAbstract(String researchAbstract) {
		this.researchAbstract = researchAbstract;
	}


	public void setRecommendedResearch(List<RecommendedResearch> list) {
		this.recommendedResearch = list;
	}

	@Override
	public String toString() {
		return "ResearchReportMetadata [contentId=" + contentId + ", contentType=" + contentType + ", slug=" + slug
				+ ", title=" + title + ", subtitle=" + subtitle + ", abstractTitle=" + abstractTitle + ", authors="
				+ authors + ", contributors=" + contributors + ", dates=" + dates + ", editorialImageURL="
				+ editorialImageURL + ", contentURL=" + contentURL + ", priceType=" + priceType + ", price=" + price + ", ipType=" + ipType
				+ ", researchAbstract=" + researchAbstract + ", recommendedResearch=" + recommendedResearch + "]";
	}
	
}