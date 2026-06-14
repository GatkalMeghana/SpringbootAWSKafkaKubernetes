package com.forrester.index.clients.research.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResearchContainer implements Serializable {
	
	private static final long serialVersionUID = 5303912274568102764L;
	private String type;
	private String title;
	private String text;
	private String figureTitle;
	private String description;
	private JsonNode image;
	private String caption;
	private String altText;
	private String supportingText;
	private String externalId;
	@JsonDeserialize(using = EmbedCodeDeserializer.class)
	private String embedCode;
	private String figureLabel;
	private String embedLabel;
	private String videoLabel;
	private String audioLabel;
	private String tableLabel;
	private String indexText;
	private String id;
	private String contentId;
	private Boolean pdfOverride;
	private List<PdfImage> pdfImages = new ArrayList<>();
	private String toolType;
	private String downloadableTool;
	private List<Item> items;
	private String mediaTitle;
	private String mediaLabel;
	private String tableTitle;
	private String embedTitle;
	private String quote;
	private String quoteLabel;
	private List<List<String>> table;

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public String getFigureTitle() {
		return figureTitle;
	}

	public String getDescription() {
		return description;
	}

	public JsonNode getImage() {
		return image;
	}

	public String getCaption() {
		return caption;
	}

	public String getAltText() {
		return altText;
	}

	public String getSupportingText() {
		return supportingText;
	}

	public String getExternalId() {
		return externalId;
	}

	public String getEmbedCode() {
		return embedCode;
	}

	public String getFigureLabel() {
		return figureLabel;
	}

	public String getEmbedLabel() {
		return embedLabel;
	}

	public String getVideoLabel() {
		return videoLabel;
	}

	public String getAudioLabel() {
		return audioLabel;
	}

	public String getTableLabel() {
		return tableLabel;
	}

	public String getIndexText() {
		return indexText;
	}

    public String getId() {
        return id;
    }

    public String getContentId() {
        return contentId;
    }

    public Boolean getPdfOverride() {
        return pdfOverride;
    }

    public List<PdfImage> getPdfImages() {
        return pdfImages;
    }

    public String getToolType() {
        return toolType;
    }

    public String getDownloadableTool() {
        return downloadableTool;
    }

	public List<Item> getItems() {
		return items;
	}

	public String getMediaTitle() {
		return mediaTitle;
	}

	public String getMediaLabel() {
		return mediaLabel;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public String getEmbedTitle() {
		return embedTitle;
	}

	public String getQuote() {
		return quote;
	}

	public String getQuoteLabel() {
		return quoteLabel;
	}

	public List<List<String>> getTable() {
		return table;
	}
}
