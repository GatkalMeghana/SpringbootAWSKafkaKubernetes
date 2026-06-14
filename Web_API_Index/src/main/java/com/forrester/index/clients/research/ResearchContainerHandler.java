package com.forrester.index.clients.research;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.forrester.index.clients.research.response.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class bind the research container response to ResearchContainerDTO.
 * 
 * @author meghanag
 *
 */
public class ResearchContainerHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResearchContainerHandler.class);

    public static final DateTimeFormatter RESEARCH_PUBLISH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'");
    public static final String TOOL_CONTAINER = "toolContainer";
    public static final String TABLE_TYPE = "table";
    public static final String FIGURE = "figure";
    public static final String RESEARCH_GRAPHIC_CONTAINER = "researchGraphicContainer";
    public static final int ID_POSITION = 3;
    public static final String SEPERATOR = "/";

    /**
	 * This method get Research service reponse and bind it to ResearchContainerDTO
	 * with the help of builder for index into elastic.
	 * 
	 * @param researchResponse
	 * @return ResearchContainerDTO
	 */
    public static ResearchContainerDTO from(ResearchResponse researchResponse) {
        ResearchContainerDTO.Builder dtoBuilder = ResearchContainerDTO.builder();
        String researchID = researchResponse.getContentId();

        dtoBuilder.withDocumentID(StringUtils.isNotEmpty(researchID) && researchID.startsWith("RES") ? researchID.substring(3) : researchID)
                .withTitle(researchResponse.getTitle())
                .withSubTitle(researchResponse.getSubtitle())
                .withResearchAbstract(researchResponse.getResearchAbstract())
                .withAbstractTitle(researchResponse.getAbstractTitle())
                .withPrice(researchResponse.getPrice() != null ? Math.round(researchResponse.getPrice()) : null)
                .withPriceType(researchResponse.getPriceType())
                .withSlug(researchResponse.getSlug())
                .withRecommendedResearch(researchResponse.getRecommendedResearch())
                .withBody(getResearchBodyText(researchResponse))
                .withBodyJson(getResearchBodyJsonText(researchResponse))
                .withFigures(handleFigures(researchResponse))
                .withKeyTakeAways(singletonList(singletonMap("description", researchResponse.getKeyTakeaways())))
                .withTags(researchResponse.getTags())
                .withImageCount(getResearchImageCount(researchResponse))
                .withIpType(researchResponse.getIpType())
                .withEditorialImage(researchResponse.getEditorialImage())
                .withArchiveHtml(StringUtils.isNotBlank(researchResponse.getArchiveHtml()) ? researchResponse.getArchiveHtml()
                                : StringUtils.EMPTY)
                .withArchiveJson(StringUtils.isNotBlank(researchResponse.getArchiveJson()) ? researchResponse.getArchiveJson()
                                : StringUtils.EMPTY)
                .withSubType(researchResponse.getSubtype())
                .withAbstractPreview(researchResponse.getAbstractPreview())
                .withLegacyContentType(StringUtils.isNotBlank(researchResponse.getLegacyContentType())
                        ? researchResponse.getLegacyContentType()
                        : StringUtils.EMPTY)
                .withIsArchived(researchResponse.getIsArchived())
                .withLegacyContentId(researchResponse.getLegacyContentId())
                .withContentId(researchID)
                .withTimeToRead(researchResponse.getTimeToRead())
                .withEntryId(researchResponse.getId())
                .withDownloadableAssets(researchResponse.getDownloadableAssets())
                .withBodyJsonAssets(researchResponse.getBodyJsonAssets());


        String publishedDate = researchResponse.getPublishedDate();
        if(StringUtils.isNotBlank(publishedDate)){
            try {
                dtoBuilder.withPublishedDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(RESEARCH_PUBLISH_DATE_FORMATTER.parse(publishedDate)));
            }catch (DateTimeParseException e){
                LOGGER.error("Failed to parse published date for research container ID : {}, published date received as : {}",
                        researchID, publishedDate, e);
            }
        }
        
        String updatedDate = researchResponse.getUpdatedDate();
        if(StringUtils.isNotBlank(updatedDate)){
            try {
                dtoBuilder.withUpdatedDate(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(RESEARCH_PUBLISH_DATE_FORMATTER.parse(updatedDate)));
            }catch (DateTimeParseException e){
                LOGGER.error("Failed to parse updated date for research container ID : {}, updated date received as : {}",
                        researchID, updatedDate, e);
            }
        }

        if (CollectionUtils.isNotEmpty(researchResponse.getAuthors())) {
            dtoBuilder.withAuthors(researchResponse.getAuthors()
                    .stream().map(ResearchContainerHandler::mapAuthor)
                    .collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(researchResponse.getContributors())) {
            dtoBuilder.withContributors(researchResponse.getContributors()
                    .stream().map(ResearchContainerHandler::mapAuthor)
                    .collect(Collectors.toList()));
        }
        return dtoBuilder.build();
    }

    private static List<Figure> handleFigures(ResearchResponse researchResponse) {
        if(researchResponse.getBodyJson() != null && !researchResponse.getBodyJson().isEmpty()) {
            return researchResponse.getBodyJson().stream()
                    .filter(x -> x.getType().equals(FIGURE))
                    .map(bodyJson -> {
                        if(bodyJson.getImage() != null && bodyJson.getImage().get("id")!=null) {
                            Asset asset = new Asset();
                            asset.setId(bodyJson.getImage().get("id").asText());
                            return getFigure(bodyJson, asset);
                        }else{
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        if(researchResponse.getBody() != null && !researchResponse.getBody().isEmpty()){
            return researchResponse.getBody().stream()
                    .filter(body -> body.getType().equals(RESEARCH_GRAPHIC_CONTAINER))
                    .map(body -> {
                        Asset asset = new Asset();
                        asset.setId(getImageIdFromBody(body));
                        return getFigure(body, asset);
                    })
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    private static String getImageIdFromBody(ResearchContainer body) {
        try {
            return body.getImage().asText().split(SEPERATOR)[ID_POSITION];
        } catch (Exception e) {
            LOGGER.error("Image url is missing from research container for id {}", body.getContentId());
        }
        return StringUtils.EMPTY;
    }

    private static Figure getFigure(ResearchContainer x, Asset asset) {
        Figure figure = new Figure();
        figure.setFigureLabel(x.getFigureLabel());
        figure.setFigureTitle(x.getFigureTitle());
        figure.setAltText(x.getAltText());
        figure.setCaption(x.getCaption());
        figure.setId(x.getId());
        figure.setType(x.getType());
        figure.setAsset(asset);
        return figure;
    }

    /**
	 * This method maps author details.
	 * 
	 * @param author
	 * @return Map<String, Object>
	 */
    private static Map<String, Object> mapAuthor(Author author) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", author.getId() != null && author.getId().startsWith("BIO") ? author.getId().substring(3) : author.getId());
        result.put("fullName", StringUtils.isNotBlank(author.getFullName()) ? author.getFullName() : StringUtils.EMPTY);
        result.put("authorTitle", StringUtils.isNotBlank(author.getTitle()) ? author.getTitle() : StringUtils.EMPTY);
        result.put("active", StringUtils.isNotBlank(author.getActive()) ? author.getActive() : StringUtils.EMPTY);
        result.put("imageUrl", StringUtils.isNotBlank(author.getImageUrl()) ? author.getImageUrl() : StringUtils.EMPTY);
        result.put("firstName", StringUtils.isNotBlank(author.getFirstName()) ? author.getFirstName() : StringUtils.EMPTY);
        result.put("lastName", StringUtils.isNotBlank(author.getLastName()) ? author.getLastName() : StringUtils.EMPTY);
        return result;
    }

	/**
	 * This method get response body from ResearchResponse.
	 * 
	 * @param researchResponse
	 * @return String
	 */
    private static String getResearchBodyText(ResearchResponse researchResponse) {
        List<ResearchContainer> containers = Optional.ofNullable(researchResponse.getBody()).orElse(Collections.emptyList());
        return containers.stream()
                .map(ResearchContainerHandler::getContainerText)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(StringUtils.SPACE));
    }

    /**
     * This method get response body from ResearchResponse.
     *
     * @param researchResponse
     * @return String
     */
    private static String getResearchBodyJsonText(ResearchResponse researchResponse) {
        List<ResearchContainer> containers = Optional.ofNullable(researchResponse.getBodyJson()).orElse(Collections.emptyList());
        return containers.stream()
                .map(ResearchContainerHandler::getContainerText)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(StringUtils.SPACE));
    }

	/**
	 * This method gets container texts from ResearchContainer.
	 * 
	 * @param container
	 * @return String
	 */
    private static String getContainerText(ResearchContainer container) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> textValues = new ArrayList<>();
        textValues.addAll(Arrays.asList(container.getTitle(), container.getText(),
                container.getFigureTitle(), container.getDescription(),
                container.getCaption(), container.getAltText(),
                container.getSupportingText(), container.getFigureLabel(), container.getEmbedLabel(),
                container.getEmbedTitle(), container.getVideoLabel(), container.getAudioLabel(),
                container.getTableLabel(), container.getTableTitle(), container.getIndexText(),
                container.getToolType(), container.getDownloadableTool(), container.getMediaTitle(),
                container.getMediaLabel(), container.getQuote(), container. getQuoteLabel()));
        if (StringUtils.isNotBlank(container.getType()) && container.getType().equalsIgnoreCase(TOOL_CONTAINER)
                && StringUtils.isNotBlank(container.getEmbedCode())) {
            try {
                ResearchContainer embedContainer = objectMapper.readValue(container.getEmbedCode(), ResearchContainer.class);
                if(Objects.nonNull(embedContainer)) {
                    textValues.add(embedContainer.getTitle());
                    textValues.add(embedContainer.getIndexText());
                    textValues.add(embedContainer.getSupportingText());
                }
            } catch (JsonProcessingException e) {
                LOGGER.error("Error converting container.getEmbedCode() from JsonString To ResearchContainer", e);
            }
        }
        if(container.getItems()!=null && !container.getItems().isEmpty()) {
            textValues.add(container.getItems().stream().map(Item::getContent).collect(Collectors.joining(" ")));
        }
        if(container.getEmbedCode()!=null){
            textValues.add(Jsoup.parse(container.getEmbedCode()).text().replace("\"",""));
        }
        if (container.getType().equalsIgnoreCase(TABLE_TYPE) && container.getTable() != null) {
            textValues.add(container.getTable().stream().flatMap(List::stream).collect(Collectors.joining(" ")));
        }

        return textValues.stream().filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(StringUtils.SPACE));
    }
    
	/**
	 * This method calculate the image count from research response.
	 * 
	 * @param researchResponse
	 * @return long
	 */
    private static long getResearchImageCount(ResearchResponse researchResponse) {
        List<ResearchContainer> containers;
        if (researchResponse.getBodyJson() != null && researchResponse.getBodyJson().size() != 0) {
            containers = Optional.ofNullable(researchResponse.getBodyJson()).orElse(Collections.emptyList());
        } else {
            containers = Optional.ofNullable(researchResponse.getBody()).orElse(Collections.emptyList());
        }
        long imageCount = containers.stream().filter(container -> container.getImage() != null).count();
        if (StringUtils.isNotEmpty(researchResponse.getEditorialImage())) {
            imageCount++;
        }
        return imageCount;
    }
}
