package com.forrester.research.clients.contentful.response.models;

import java.util.List;
import java.util.Map;

import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.serializers.CDARichDocumentSerializer;


@JsonPropertyOrder({ 
	"id", 
	"contentType", 
	"title", 
	"researchCard", 
	"description"
})
@ContentfulEntryModel("researchCollectionContainer")
@JsonInclude(Include.NON_NULL)
public class ResearchCollectionContainer {

		@JsonProperty("id")
		@ContentfulSystemField("id")
		private String id;

		@ContentfulSystemField("contentType")
		private Map<String, Object> contentType;

		@ContentfulField("title")
		private String title;

		@ContentfulField("researchCard")
		private List<Research> researchCard;
		
		@JsonSerialize(using = CDARichDocumentSerializer.class)
		@ContentfulField("description")
		private CDARichDocument description;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public List<Research> getResearchCard() {
			return researchCard;
		}

		public void setResearchCard(List<Research> researchCard) {
			this.researchCard = researchCard;
		}

		public CDARichDocument getDescription() {
			return description;
		}

		public void setDescription(CDARichDocument description) {
			this.description = description;
		}
		

}
