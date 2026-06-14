package com.forrester.index.activemq.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.forrester.index.clients.analyst.AnalystClient;
import com.forrester.index.clients.analyst.response.AnalystResponse;
import com.forrester.index.clients.search.SearchClient;
import com.forrester.index.clients.search.response.SearchResult;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.InvalidContentException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.AnalystIndexService;
import com.forrester.index.service.ResearchContainerIndexService;
import com.forrester.index.service.WebinarIndexService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Consumer;

import static com.forrester.index.utils.Constants.ANALYST_PREFIX;

@Component
public class AnalystBioProcessor implements Consumer<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalystBioProcessor.class);

    @Autowired
    private AnalystIndexService analystIndexService;

    @Autowired
    private SearchClient searchClient;

    @Autowired
    private WebinarIndexService webinarIndexService;

    @Autowired
    private AnalystClient analystClient;

    @Autowired
    private ResearchContainerIndexService researchContainerIndexService;

    private static final String FIELD_NAME = "name";
    private static final String FIELD_VALUE = "value";
    private static final String TYPE_WEBINAR_CLASS = "ForresterWebinar";
    private static final String TYPE_RESEARCH_CLASS = "ForresterResearch";
    private static final String TYPES_TO_FIND = "forrester-webinar,forrester-research";

    @Override
    public void accept(String metaID) {
        try {
            String analystId = StringUtils.startsWith(metaID.toUpperCase(), ANALYST_PREFIX) ? metaID
                    : StringUtils.join(ANALYST_PREFIX, metaID);
            Optional<AnalystResponse> analystResponse = analystClient.getAnalystById(analystId);
            if(analystResponse.isPresent()) {
                if (analystResponse.get().getExternallyActive()) {
                    analystIndexService.indexById(analystResponse.get().getAnalystBioId());
                } else {
                    analystIndexService.deIndexById(analystResponse.get().getAnalystBioId());
                }
                updateAnalystInContentTypes(analystId);
            }

        } catch (ServiceException | DataNotFoundException | InvalidContentException e) {
            LOGGER.debug("Exception while getting analyst bio with ID : {}, possible cause: {} and message {}",
                    metaID, e.getCause(), e.getMessage());
        }
    }

    /**
     * This method find All the documents of contentType = TYPES_TO_FIND
     * where analystId is analyst (webinar) or author (fd-Research), and reIndex all of them.
     *
     * @param analystId
     */
    public void updateAnalystInContentTypes(String analystId){

        List<SearchResult> searchResults = findWebinarAndResearchByAnalyst(analystId);
        searchResults.forEach( content -> {
            switch (content.getContentType()) {
                case TYPE_WEBINAR_CLASS:
                    updateWebinar(content.getContentId());
                    break;
                case TYPE_RESEARCH_CLASS:
                    updateResearch(content.getContentId());
                    break;
            }
        });
    }

    private List<SearchResult> findWebinarAndResearchByAnalyst(String analystId){
        Map<String, String> termFilter = new HashMap<>();
        termFilter.put(FIELD_NAME, "analysts.id,authors.id");
        termFilter.put(FIELD_VALUE, analystId);

        List<Map<String, String>> termFilters = new ArrayList<>();
        termFilters.add(termFilter);
        List<SearchResult> searchResults = new ArrayList<>();
        int pageNumber = 1;

        Page<SearchResult> searchResult = searchClient.searchByTermFilter(termFilters, false, TYPES_TO_FIND, PageRequest.of(pageNumber, 25));
        searchResults.addAll(searchResult.getContent());

        for (++pageNumber; pageNumber <= searchResult.getTotalPages(); pageNumber++) {
            searchResult = searchClient.searchByTermFilter(termFilters, false, TYPES_TO_FIND, PageRequest.of(pageNumber, 25));
            searchResults.addAll(searchResult.getContent());
        }

        return searchResults;
    }

    private void updateWebinar(String contentId) {
        try {
            webinarIndexService.indexById(contentId);
        } catch (ServiceException | DataNotFoundException e) {
            LOGGER.error("Exception while webinar was reindexed with ID: {}, cause: {}", contentId, e.getCause());
        }
    }

    private void updateResearch(String contentId){
        try {
            researchContainerIndexService.indexById(contentId);
        } catch (ServiceException | DataNotFoundException | JsonProcessingException e) {
            LOGGER.error("Exception while researchContainer was reindexed with ID: {}, cause: {}", contentId, e.getCause());
        }
    }
}
