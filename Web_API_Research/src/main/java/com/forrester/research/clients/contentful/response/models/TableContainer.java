package com.forrester.research.clients.contentful.response.models;

import java.util.List;
import java.util.Map;

import com.contentful.java.cda.CDAAsset;
import com.contentful.java.cda.TransformQuery.ContentfulEntryModel;
import com.contentful.java.cda.TransformQuery.ContentfulField;
import com.contentful.java.cda.TransformQuery.ContentfulSystemField;
import com.contentful.java.cda.rich.CDARichDocument;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.forrester.research.clients.contentful.serializers.CDAAssetListConverter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "id",
	"contentId",
    "contentType",
    "title",
    "columnOrder",
    "table",
    "publishedDate",
    "revision",
    "displayTitle",
    "tableTitle",
    "description",
    "tableLabel",
    "caption",
    "downloadableAssets"
})
@ContentfulEntryModel("tableContainer")
@JsonInclude(Include.NON_NULL)
public class TableContainer {

    @ContentfulSystemField("id")
    private String id;
    
	@ContentfulField("contentId")
	private String contentId;
	
	@ContentfulSystemField("contentType")
	private Map<String,Object> contentType;
    
    @ContentfulField("title")
    private String title;

    @ContentfulField("columnOrder")
    private String columnOrder;
    
    @ContentfulField("table")
    private Object table;
    
    @ContentfulSystemField("updatedAt")
    private String publishedDate;

    @ContentfulSystemField("revision")
    private Double revision;

    @ContentfulField("displayTitle")
    private Boolean displayTitle;
    
    @ContentfulField("tableTitle")
    private String tableTitle;
    
    @ContentfulField("description")
    private CDARichDocument description;
    
    @ContentfulField("tableLabel")
    private String tableLabel;
    
    @ContentfulField("caption")
    private CDARichDocument caption;
    
    @JsonSerialize(converter = CDAAssetListConverter.class)
    @ContentfulField("downloadableAssets")
	private List<CDAAsset> downloadableAssets;
    
	public String getId() {
		return id;
	}

	public String getContentId() {
		return contentId;
	}

	public Map<String, Object> getContentType() {
		return contentType;
	}

	public String getTitle() {
		return title;
	}

	public String getColumnOrder() {
		return columnOrder;
	}

	public Object getTable() {
		return table;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public Double getRevision() {
		return revision;
	}

	public Boolean getDisplayTitle() {
		return displayTitle;
	}

	public void setDisplayTitle(Boolean displayTitle) {
		this.displayTitle = displayTitle;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public CDARichDocument getDescription() {
		return description;
	}

	public CDARichDocument getCaption() {
		return caption;
	}

	public List<CDAAsset> getDownloadableAssets() {
		return downloadableAssets;
	}

	public String getTableLabel() {
		return tableLabel;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public void setContentType(Map<String, Object> contentType) {
		this.contentType = contentType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setColumnOrder(String columnOrder) {
		this.columnOrder = columnOrder;
	}

	public void setTable(Object table) {
		this.table = table;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
	}

	public void setRevision(Double revision) {
		this.revision = revision;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public void setDescription(CDARichDocument description) {
		this.description = description;
	}

	public void setCaption(CDARichDocument caption) {
		this.caption = caption;
	}

	public void setDownloadableAssets(List<CDAAsset> downloadableAssets) {
		this.downloadableAssets = downloadableAssets;
	}

	public void setTableLabel(String tableLabel) {
		this.tableLabel = tableLabel;
	}

	@Override
	public String toString() {
		return "TableContainer [id=" + id + ", contentId=" + contentId + ", contentType=" + contentType + ", title="
				+ title + ", columnOrder=" + columnOrder + ", table=" + table + ", publishedDate=" + publishedDate
				+ ", revision=" + revision + ", displayTitle=" + displayTitle + ", tableTitle=" + tableTitle
				+ ", description=" + description + ", tableLabel=" + tableLabel + ", caption=" + caption
				+ ", downloadableAssets=" + downloadableAssets + "]";
	}
}