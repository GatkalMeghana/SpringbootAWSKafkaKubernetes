package com.forrester.research.service.impl;

import static org.apache.commons.lang3.StringUtils.CR;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;
import static org.apache.commons.lang3.StringUtils.replaceEachRepeatedly;
import static org.springframework.web.util.HtmlUtils.htmlUnescape;

import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.forrester.research.utils.DateUtils;


@Service
public class ContentFormatter {
    private static final String[] SEARCH_VALUES = {"EMPTY_CELL", "_", System.lineSeparator(), CR, LF};
    private static final String[] REPLACE_VALUES = {"", "", " ", "", " "};
    private static final UnaryOperator<String> SANITIZE = value ->
            normalizeSpace(replaceEachRepeatedly(null != value ? htmlUnescape(value).replaceAll("\\<.*?\\>", "") : EMPTY, SEARCH_VALUES, REPLACE_VALUES));

    private final Function<JsonNode, Object> GET_SANITIZED_TEXT = node -> SANITIZE.apply(node.asText(StringUtils.EMPTY));
    private final Function<JsonNode, Object> KEEP_ORIGINAL_VALUE = node -> node;
    private final Function<JsonNode, Object> GET_ALL_AUTHORS = this::getAuthor;
    private final Function<JsonNode, Object> GET_PUBLISHED_DATE = node -> DateUtils.convertDateToISOFormat (node.textValue());
    private final Function<JsonNode, Object> GET_COMBINED_TEXT = node -> fetchCombinedText(node, Collections.singletonList("value"));
    private final Function<JsonNode, Object> GET_ARCHIVE_ASSETS = this::getArchiveAssets;
    private Map<String, Function<JsonNode, Object>> researchContentTransformers = new LinkedHashMap<>();
    private Map<String, List<String>> flattenedFieldByContainer = new HashMap<>();
    private Map<String, List<String>> flattenedFieldByType = new HashMap<>();
    @Autowired
    private ObjectMapper objectMapper;

    private static final String CONTENT_ID = "contentId";
    private static final String TITLE = "title";
    private static final String SUPPORTING_TEXT = "supportingText";
    private static final String PDF_OVERRIDE = "pdfOverride";
    private static final String PDF_IMAGES = "pdfImages";
    private static final String RESEARCH_CARD = "researchCard";
    private static final String ID = "id";
    private static final String CONTENT_TYPE = "contentType";
    private static final String TYPE = "type";
    private static final String URL = "url";
    private static final String IMAGE_URL = "imageUrl";
    private static final String DESCRIPTION = "description";
    private static final String INDEX_TEXT = "indexText";
    private static final String CAPTION = "caption";
    private static final String EMBED_LABEL = "embedLabel";
    private static final String EMBED_TITLE = "embedTitle";
    private static final String EMBED_CODE = "embedCode";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String ITEMS = "items";
    private static final String DATA = "data";

    @PostConstruct
    private void initContentTransformers() {
        researchContentTransformers.put(ID, GET_SANITIZED_TEXT);
        researchContentTransformers.put(CONTENT_ID, GET_SANITIZED_TEXT);
        researchContentTransformers.put(CONTENT_TYPE, node -> getPropertyValue(node, "sys", ID));
        researchContentTransformers.put(TITLE, GET_SANITIZED_TEXT);
        researchContentTransformers.put("subtitle", GET_SANITIZED_TEXT);
        researchContentTransformers.put("slug", GET_SANITIZED_TEXT);
        researchContentTransformers.put("timeToRead", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("abstractTitle", GET_SANITIZED_TEXT);
        researchContentTransformers.put("abstractPreview", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("researchAbstract", GET_COMBINED_TEXT);
        researchContentTransformers.put("displayHeroImage",KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("heroColor",KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("editorialImage", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("authors", GET_ALL_AUTHORS);
        researchContentTransformers.put("contributors", GET_ALL_AUTHORS);
        researchContentTransformers.put("body", this::getResearchBody);
        researchContentTransformers.put("bodyJson", this::getBodyJson);
        researchContentTransformers.put("bodyJsonReferences", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("bodyJsonAssets", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("priceType", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("price", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("publishedDate", GET_PUBLISHED_DATE);
        researchContentTransformers.put("methodology", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("recommendationsSectionTitle", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("recommendedResearch", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("keyTakeaways", GET_COMBINED_TEXT);
        researchContentTransformers.put("contentUrl", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("ipType", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("updatedDate", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("contentChangeDateByUser", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("archiveHtml", GET_COMBINED_TEXT);
        researchContentTransformers.put("archiveJson", GET_COMBINED_TEXT);
        researchContentTransformers.put("isArchived", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("legacyContentType", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("subtype", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("downloadableAssets", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("researchPdf", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("legacyContentId", KEEP_ORIGINAL_VALUE);
        researchContentTransformers.put("archiveAssets", GET_ARCHIVE_ASSETS);
        researchContentTransformers.put("status", KEEP_ORIGINAL_VALUE);


        //Fields map for body
        flattenedFieldByContainer.put("textContainer", Arrays.asList(ID, CONTENT_ID, TITLE, "text"));
        flattenedFieldByContainer.put("researchGraphicContainer", Arrays.asList(ID, CONTENT_ID, TITLE, "figureTitle", "figureLabel", DESCRIPTION, "image", CAPTION, "altText", PDF_OVERRIDE, PDF_IMAGES));
        flattenedFieldByContainer.put("embedContainer", Arrays.asList(ID, CONTENT_ID, TITLE, SUPPORTING_TEXT, INDEX_TEXT, CAPTION, EMBED_LABEL, EMBED_TITLE, PDF_OVERRIDE, PDF_IMAGES));
        flattenedFieldByContainer.put("videoContainer", Arrays.asList(ID, CONTENT_ID, TITLE, SUPPORTING_TEXT, "externalId", EMBED_CODE, "videoLabel","videoTitle", PDF_OVERRIDE, PDF_IMAGES));
        flattenedFieldByContainer.put("audioContainer", Arrays.asList(ID, CONTENT_ID, TITLE, SUPPORTING_TEXT, "externalId", EMBED_CODE, "audioLabel","audioTitle", PDF_OVERRIDE, PDF_IMAGES));
        flattenedFieldByContainer.put("tableContainer", Arrays.asList(ID, CONTENT_ID, TITLE, DESCRIPTION, "tableLabel"));
        flattenedFieldByContainer.put("toolContainer", Arrays.asList(ID, CONTENT_ID, "toolType", "downloadableTool", EMBED_CODE));
        
		// Fields map for bodyjson
		flattenedFieldByType.put("paragraph", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("heading", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("list", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("figure", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("audio", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("video", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("table", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("embed", Arrays.asList(ID, DATA));
		flattenedFieldByType.put("quote", Arrays.asList(ID, DATA));
    }

	private List<Map<String, Object>> getArchiveAssets(JsonNode node) {
		List<Map<String, Object>> result = new ArrayList<>();
		Map<String, Object> value = new LinkedHashMap<>();
		if (node != null) {
			if (node.isArray()) {
				node.forEach(childNode -> result.addAll(getArchiveAssets(childNode)));
			} else {
				value.put(ID, node.get(ID).asText());
				value.put(CONTENT_TYPE, getPropertyValue(node.get(CONTENT_TYPE), "sys", ID));
				if (null != node.get(TYPE) && StringUtils.isNotEmpty(node.get(TYPE).asText())) {
					value.put(TYPE, node.get(TYPE).asText());
				}
				if (null != node.get(URL) && StringUtils.isNotEmpty(node.get(URL).asText())) {
					value.put(URL, node.get(URL).asText());
				}
				result.add(value);
			}
		}
		return result;
	}

	private Map<String, Object> getEmbedCode(JsonNode node) {
		Map<String, Object> value = new LinkedHashMap<>();
		if (node != null) {
			value.put(CONTENT_TYPE, getPropertyValue(node.get(CONTENT_TYPE), "sys", ID));
			flattenedContainer(node, "embedContainer", value);
		}
		boolean isDisplayTitle = "true".equals(getPropertyValue(node, "displayTitle"));
		if (value.containsKey(TITLE) && !isDisplayTitle) {
            value.remove(TITLE);
        }
		return value;
	}

    public Map<String, Object> getFormattedResearch(Object research) {
        JsonNode rootNode = objectMapper.valueToTree(research);
        Map<String, Object> result = new LinkedHashMap<>();

		for (Map.Entry<String, Function<JsonNode, Object>> field : researchContentTransformers.entrySet()) {
			JsonNode node = rootNode.findValue(field.getKey());
			if("downloadableAssets".equalsIgnoreCase(field.getKey()) || "publishedDate".equalsIgnoreCase(field.getKey())
					|| TITLE.equalsIgnoreCase(field.getKey()) || CONTENT_ID.equalsIgnoreCase(field.getKey())) {
				node = rootNode.get(field.getKey());
			}
			if (node != null) {
				result.put(field.getKey(), field.getValue().apply(node));
			}
		}

        return result;
    }

    private String fetchCombinedText(JsonNode node, List<String> textFieldNames) {
        List<String> values = new ArrayList<>();
        if (node.isArray()) {
            node.forEach(child -> values.add(fetchCombinedText(child, textFieldNames)));
        } else {
            node.fieldNames().forEachRemaining(childField -> {
                if (textFieldNames.contains(childField)) {
                    values.add(node.findValue(childField).asText(StringUtils.EMPTY));
                } else {
                    values.add(fetchCombinedText(node.findValue(childField), textFieldNames));
                }
            });
        }
        return values.stream()
                .map(SANITIZE)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(StringUtils.SPACE));
    }

	private List<Map<String, Object>> getResearchBody(JsonNode researchBody) {
		List<JsonNode> containerNodes = new ArrayList<>();
		if (researchBody != null && researchBody.isArray()) {
			getContainers(researchBody, containerNodes);
		}

		List<Map<String, Object>> result = new ArrayList<>();
		for (JsonNode container : containerNodes) {
			String containerType = getPropertyValue(container, CONTENT_TYPE, "sys", ID);
			boolean isDisplayTitle = "true".equals(getPropertyValue(container, "displayTitle"));
			getResearchCollection(result, container);

			if (flattenedFieldByContainer.containsKey(containerType)) {
				Map<String, Object> formattedContainer = new LinkedHashMap<>();
				formattedContainer.put("type", containerType);
				flattenedContainer(container, containerType, formattedContainer);
				if (formattedContainer.containsKey(TITLE) && !isDisplayTitle) {
                    formattedContainer.remove(TITLE);
                }
				result.add(formattedContainer);
			}
		}
		return result;
	}

	private void flattenedContainer(JsonNode container, String containerType, Map<String, Object> formattedContainer) {
		for (String fieldName : flattenedFieldByContainer.get(containerType)) {
			if (container.get(fieldName) != null && container.get(fieldName).isContainerNode()) {
				populateContainerNodes(container, fieldName, formattedContainer);
			} else {
				populateValueNodes(container, fieldName, formattedContainer);
			}
		}
	}

	private void populateValueNodes(JsonNode container, String fieldName, Map<String, Object> formattedContainer) {
		String value = getPropertyValue(container, fieldName);
		if (StringUtils.isNotEmpty(value)) {
			formattedContainer.put(fieldName, value);
		}
	}

	private void populateContainerNodes(JsonNode container, String fieldName, Map<String, Object> formattedContainer) {
		if (fieldName.equalsIgnoreCase(PDF_IMAGES) || fieldName.equalsIgnoreCase("downloadableTool")) {
			Object fieldValue = KEEP_ORIGINAL_VALUE.apply(container.get(fieldName));
			if (null != fieldValue) {
				formattedContainer.put(fieldName, fieldValue);
			}
		} else if (fieldName.equalsIgnoreCase(EMBED_CODE)) {
			Object embedCode = getEmbedCode(container.get(fieldName));
            formattedContainer.put(fieldName, embedCode);
		} else {
			String combinedText = GET_COMBINED_TEXT.apply(container.get(fieldName)).toString();
			if (StringUtils.isNotEmpty(combinedText)) {
				formattedContainer.put(fieldName, combinedText);
			}
		}
	}

	private void getContainers(JsonNode researchBody, List<JsonNode> containerNodes) {
		researchBody.forEach(container -> {
			JsonNode node = container.findValue("containers");
			if (node != null) {
				node.forEach(containerNodes::add);
			} else {
				containerNodes.add(container);
			}
		});
	}

	private void getResearchCollection(List<Map<String, Object>> result, JsonNode container) {
		if(container.get(RESEARCH_CARD)!=null) {
			List <Map<String, Object>> formattedResearch = new ArrayList<>();
			Map<String, Object> researchCollectionContainerNode = new LinkedHashMap<>();
			researchCollectionContainerNode.put(TITLE,container.get(TITLE));
			researchCollectionContainerNode.put(DESCRIPTION, fetchCombinedText(container.get(DESCRIPTION), Collections.singletonList("value")));
			for(int i=0; i< container.get(RESEARCH_CARD).size(); i++) {
				Map<String, Object> researchCard = getFormattedResearch(container.get(RESEARCH_CARD).get(i));
				formattedResearch.add(getFormattedResearch(researchCard));
			}
			researchCollectionContainerNode.put(RESEARCH_CARD, formattedResearch);
			result.add(researchCollectionContainerNode);
		}
	}

    private List<Map<String, Object>> getAuthor(JsonNode authorsRoot) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (authorsRoot != null && authorsRoot.isArray()) {
            authorsRoot.forEach(authorNode -> {
                Map<String, Object> formattedAuthor = new LinkedHashMap<>();
                formattedAuthor.put(ID, getPropertyValue(authorNode, CONTENT_ID));
                formattedAuthor.put(TITLE, getPropertyValue(authorNode, TITLE));
                formattedAuthor.put("fullName", String.join(SPACE,
                        getPropertyValue(authorNode, FIRST_NAME), getPropertyValue(authorNode, LAST_NAME)));
                formattedAuthor.put("active", authorNode.findValue("active"));
                formattedAuthor.put(FIRST_NAME, getPropertyValue(authorNode, FIRST_NAME));
                formattedAuthor.put(LAST_NAME, getPropertyValue(authorNode, LAST_NAME));
                if(null!=authorNode.findValue(IMAGE_URL)) {
                	formattedAuthor.put(IMAGE_URL, authorNode.findValue(IMAGE_URL));
                }
                result.add(formattedAuthor);
            });
        }
        return result;
    }


    private String getPropertyValue(JsonNode parent, String... fieldsChain) {
        Optional<JsonNode> dest = Optional.ofNullable(parent);
        for (String field : fieldsChain) {
            dest = dest.map(node -> node.findValue(field));
        }
        return dest.map(JsonNode::asText).orElse(EMPTY);
    }
    
	private String getPropertyValueForType(JsonNode parent, String... fieldsChain) {
		Optional<JsonNode> dest = Optional.ofNullable(parent);
		for (String field : fieldsChain) {
			dest = dest.map(node -> node.get(field));
		}
		return dest.map(JsonNode::asText).orElse(EMPTY);
	}
    
    private List<Map<String, Object>> getBodyJson(JsonNode bodyJson) {
		List<JsonNode> blockNodes = new ArrayList<>();
		if (bodyJson != null && bodyJson.get("blocks")!=null) {
			getContainers(bodyJson.get("blocks"), blockNodes);
		}

		List<Map<String, Object>> result = new ArrayList<>();
		for (JsonNode block : blockNodes) {
			String blockType = getPropertyValueForType(block, "type");
			if (flattenedFieldByType.containsKey(blockType)) {
				Map<String, Object> formattedBlock = new LinkedHashMap<>();
				formattedBlock.put("type", blockType);
				flattenedField(block, blockType, formattedBlock);
				result.add(formattedBlock);
			}
		}
		return result;
	}
    
    private void flattenedField(JsonNode block, String blockType, Map<String, Object> formattedBlock) {
		for (String fieldName : flattenedFieldByType.get(blockType)) {
			if (block.get(fieldName) != null && block.get(fieldName).isContainerNode()) {
				populateNodes(block, formattedBlock, blockType);
			} else {
				populateValueNodes(block, fieldName, formattedBlock);
			}
		}
	}
    
	private void populateNodes(JsonNode block, Map<String, Object> formattedBlock, String blockType) {
		JsonNode dataBlock = block.get(DATA);
		if (blockType.equalsIgnoreCase("list")) {
			formattedBlock.putAll(getListData(dataBlock));
		} else {
			dataBlock.fields().forEachRemaining(entry -> {
				String field = entry.getKey();
				JsonNode node = entry.getValue();
				if (node != null) {
					if (node.isContainerNode()) {
						populateBlockData(node, field, formattedBlock);
					} else {
						if (!field.equalsIgnoreCase("level")) {
							Object fieldValue = node.asText();
							formattedBlock.put(field, fieldValue);
						}
					}
				}
			});
		}
	}
	
	private void populateBlockData(JsonNode node, String field, Map<String, Object> formattedBlock) {
		if (node.isArray()) {
			// populate block node here for type pdfImages and downloadableAssets
			populateArrayNode(node, field, formattedBlock);
		} else {
			// populate block node here for type=image
			Map<String, Object> images = new LinkedHashMap<>();
			String value = node.get("sys").get(ID).asText();
			images.put(ID, value);
			formattedBlock.put(field, images);
		}
	}
	
	private void populateArrayNode(JsonNode node, String field, Map<String, Object> formattedBlock) {
		if (field.equalsIgnoreCase("table")) {
			formattedBlock.put(field, node);
		} else {
			// pdfImages and downloadableAssets
			List<Map<String, Object>> result = new ArrayList<>();
			node.forEach(eachEntry -> {
				Map<String, Object> formattedImages = new LinkedHashMap<>();
				String value = eachEntry.get("sys").get(ID).asText();
				formattedImages.put(ID, value);
				result.add(formattedImages);
			});
			formattedBlock.put(field, result);
		}
	}
    
	private Map<String, Object> getListData(JsonNode node) {
		Map<String, Object> value = new LinkedHashMap<>();
		if (node != null) {
			List<Map<String, Object>> result = getItemsDetails(node.get(ITEMS));
			value.put(ITEMS, result);
		}
		return value;
	}

	private List<Map<String, Object>> getItemsDetails(JsonNode itemsNode) {
		List<Map<String, Object>> result = new ArrayList<>();
		if (itemsNode != null && itemsNode.isArray()) {
			itemsNode.forEach(item -> {
				Map<String, Object> itemMap = new LinkedHashMap<>();
				itemMap.put("content", KEEP_ORIGINAL_VALUE.apply(item.get("content")));
				if (item.get(ITEMS) != null && item.get(ITEMS).isArray()) {
					result.addAll(getItemsDetails(item.get(ITEMS)));
				}
				result.add(itemMap);
			});
		}
		return result;
	}
}