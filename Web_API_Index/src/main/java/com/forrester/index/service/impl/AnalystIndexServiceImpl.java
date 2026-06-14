package com.forrester.index.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forrester.index.activemq.listeners.AnalystBioProcessor;
import com.forrester.index.clients.analyst.AnalystClient;
import com.forrester.index.clients.analyst.response.AnalystResponse;
import com.forrester.index.clients.analyst.response.MultiAnalystResponse;
import com.forrester.index.clients.content.ContentServiceClient;
import com.forrester.index.clients.taxonomy.TaxonomyClient;
import com.forrester.index.clients.taxonomy.response.TaxonomyResponse;
import com.forrester.index.elasticsearch.data.ESAnalyst;
import com.forrester.index.exception.DataNotFoundException;
import com.forrester.index.exception.InvalidContentException;
import com.forrester.index.exception.ServiceException;
import com.forrester.index.service.AnalystIndexService;
import com.forrester.index.utils.*;
import com.forrester.index.view.BulkUploadView;
import com.google.common.collect.Iterables;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.BulkFailureException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ResourceUtil;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.forrester.index.utils.Constants.ANALYST_PREFIX;
import static com.forrester.index.utils.Constants.API_RECORDS_LIMIT;

@Service
public class AnalystIndexServiceImpl implements AnalystIndexService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnalystIndexServiceImpl.class);
    public static final String ANALYST_TYPE = "analyst";

    @Autowired
    private ElasticsearchOperations elasticSearchTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaxonomyClient taxonomyClient;

    @Autowired
    private AnalystClient analystClient;

    @Autowired
    private ContentServiceClient contentServiceClient;

    @Autowired
    private AnalystBioProcessor analystBioProcessor;

    @Value("${forr.elasticsearch.package.synonym}")
    private String synonymFilePath;

    private static final String ALIAS_NAME = "readAliasAnalystIndex";

    @Autowired
    private IndexAliasesBuilder indexAliasesBuilder;


    /*    *
     * This method retrieves the data for the received contentId from contentful using analyst service  and then pushes it into
     * Elasticsearch to be indexed.
     *
     * @param entryId is the contentId to be indexed
     * @throws ServiceException      if was not possible to retrieve data from LSCS
     * @throws DataNotFoundException if the received entryId does not exists in LSCS*/

    @LogThis
    @Override
    public ESAnalyst indexById(String analystId) throws ServiceException, DataNotFoundException, InvalidContentException {
        LOGGER.info("Indexing analyst with id: {}", analystId);
        analystId = StringUtils.startsWith(analystId.toUpperCase(), ANALYST_PREFIX) ? analystId
                : StringUtils.join(ANALYST_PREFIX, analystId);
        Optional<AnalystResponse> analystResponse = analystClient.getAnalystById(analystId);
        if (!analystResponse.isPresent()) {
            throw new DataNotFoundException(String.format("No analyst found with ID: %s", analystId));
        }
        checkAndDeindexInactiveAnalysts(analystResponse.get());
        return parseAndIndexAnalyst(analystResponse.get());
    }

    private ESAnalyst parseAndIndexAnalyst(AnalystResponse analystResponse) throws ServiceException {
        try {
            indexAliasesBuilder.checkAndSetIndexSuffix(ESAnalyst.class);
            ESAnalyst esAnalyst = doParsing(analystResponse);
            LOGGER.info("Parsing successful for analyst with ID: {}", esAnalyst.getAnalystBioID());
            ESAnalyst indexedAnalyst = elasticSearchTemplate.save(esAnalyst);
            LOGGER.info("Successfully indexed analyst with ID: {}", indexedAnalyst.getAnalystBioID());
            return indexedAnalyst;
        } catch (JsonProcessingException e) {
            LOGGER.debug("Parsing failed for analyst with ID: {} with cause: {}",
                    analystResponse.getAnalystBioId(), e.getLocalizedMessage());
            throw new ServiceException(String.format("Parsing failed for analyst with ID: %s",
                    analystResponse.getAnalystBioId()), e);
        } catch (BulkFailureException ese) {
            LOGGER.debug("Indexing failed for analyst with ID: {} with cause: {}",
                    analystResponse.getAnalystBioId(), ese.getMessage());
            throw new ServiceException(String.format("Indexing failed for analyst with ID: %s",
                    analystResponse.getAnalystBioId()), ese);
        } catch (ElasticsearchStatusException esse) {
            LOGGER.debug("Indexing failed for analyst with ID: {} with cause: {}",
                    analystResponse.getAnalystBioId(), esse.getLocalizedMessage());
            throw new ServiceException(String.format("Indexing failed for analyst with ID: %s",
                    analystResponse.getAnalystBioId()), esse);
        }
    }

    private void checkAndDeindexInactiveAnalysts(AnalystResponse analystResponse) throws ServiceException, InvalidContentException {
        if (!analystResponse.getExternallyActive()) {
            this.deIndexById(analystResponse.getAnalystBioId());
            LOGGER.info("Analyst with analystId {} is inactive, removed from openSearch", analystResponse.getAnalystBioId());
            throw new InvalidContentException(String.format("Analyst with bioId %s is removed", analystResponse.getAnalystBioId()));
        }
    }

    private ESAnalyst doParsing(AnalystResponse analystResponse) throws JsonProcessingException {
        ESAnalyst esAnalyst = objectMapper.readValue(objectMapper.writeValueAsString(analystResponse), ESAnalyst.class);
        esAnalyst.setAnalystBioID(analystResponse.getAnalystBioId().split(ANALYST_PREFIX)[1]);
        esAnalyst.setContentId(analystResponse.getAnalystBioId());
        esAnalyst.setEntryId(analystResponse.getId());
        TaxonomyResponse taxonomyData = taxonomyClient.getTaxonomy(ContentType.ANALYST, esAnalyst.getAnalystBioID());
        esAnalyst.addTaxonomyData(taxonomyData);
        addContentUrl(esAnalyst);
        return esAnalyst;
    }

    private void addContentUrl(ESAnalyst esAnalyst) {
        try {
            if (StringUtils.isNotBlank(esAnalyst.getFirstName())
                    && StringUtils.isNotBlank(esAnalyst.getLastName())
                    && StringUtils.isNotBlank(esAnalyst.getContentId())) {
                esAnalyst.setContentURL(
                        contentServiceClient.buildContentUrl(esAnalyst.getContentId(),
                                null,
                                null,
                                esAnalyst.getFirstName(),
                                esAnalyst.getLastName(),
                                ANALYST_TYPE));
            }
        } catch (ServiceException | JsonProcessingException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @LogThis
    @Override
    public BulkUploadView indexAll(int chunkSize) throws ServiceException {
        LOGGER.info("Flushing index and recreating...");
        Optional<IndexCoordinates> existingIndexCo = indexAliasesBuilder.createIndex(ESAnalyst.class);
        configElasticsearchTemplate();
        LOGGER.info("Indexing all existing entries.");
        List<String> allAnalystIds = this.fetchAllAnalysts();
        long total = allAnalystIds.size();
        LOGGER.info("Processing total {} analysts with chunk size of {}", total, chunkSize);
        BulkUploadView.BulkUploadBuilder builder = BulkUploadView.builder(total);
        long nextStartPosition = 0;
        while (nextStartPosition < total) {
            LOGGER.info("Indexing records from {} with batch size of : {}", nextStartPosition, chunkSize);
            List<String> chunkedAnalystBioIds = allAnalystIds.stream().skip(nextStartPosition).limit(chunkSize).collect(Collectors.toList());
            if (!chunkedAnalystBioIds.isEmpty()) {
                List<String> analystBioIds = chunkedAnalystBioIds.stream().distinct().collect(Collectors.toList());
                builder.withDuplicateRecords((List<String>) CollectionUtils.subtract(chunkedAnalystBioIds, analystBioIds));
                List<AnalystResponse> retrieved = fetchChunkedAnalysts(analystBioIds);
                if (!retrieved.isEmpty()) {
                    LOGGER.info("Processing {} analysts out of {} record ids", retrieved.size(), analystBioIds.size());
                    if (retrieved.size() != analystBioIds.size()) {
                        builder.withRetrievalFailures(analystBioIds.stream()
                                .filter(id -> !Iterables.tryFind(retrieved, analystResponse -> analystResponse.getAnalystBioId().equals(id)).isPresent())
                                .collect(Collectors.toMap(key -> key, value -> String.format("Failed to retrieve analyst with id: %s", value))));
                    }
                    BulkUploadView bulkUploadView = indexAnalystBios(retrieved);
                    builder.withParsingFailures(bulkUploadView.getParsingFailures());
                    builder.withIndexingFailures(bulkUploadView.getIndexingFailures());
                } else {
                    LOGGER.debug("Failed to retrieve {} analysts with IDs {}", analystBioIds.size(), analystBioIds);
                    builder.withRetrievalFailures(analystBioIds.stream()
                            .collect(Collectors.toMap(key -> key, value -> String.format("Failed to retrieve analyst with id: %s", value))));
                }
            } else {
                LOGGER.info("Failed to retrieve the analysts from start position: {}", nextStartPosition);
            }
            nextStartPosition += chunkSize;
        }
        indexAliasesBuilder.createAliases(ESAnalyst.class, ALIAS_NAME, existingIndexCo);
        return builder.build();
    }

    private List<AnalystResponse> fetchChunkedAnalysts(List<String> analystBioIds) {

        return analystBioIds.stream().map(analystId -> {
            try {
                return analystClient.getAnalystById(analystId);
            } catch (ServiceException e) {
                LOGGER.info("Exception in fetching analyst info by analyst service");
                return Optional.empty();
            }
        })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(AnalystResponse.class::cast)
                .collect(Collectors.toList());
    }

    private List<String> fetchAllAnalysts() throws ServiceException {
        long skip = 0;
        List<String> analystIds = new ArrayList<>();
        while (true) {
            List<MultiAnalystResponse> analysts = analystClient.getAllAnalysts(skip, true);
            if (!analysts.isEmpty()) {
                analysts.stream().distinct().forEach(analystBio -> analystIds.add(analystBio.getContentId()));
                skip += API_RECORDS_LIMIT;
            } else {
                break;
            }
        }
        return analystIds;
    }

    private void configElasticsearchTemplate() {
        Document indexConfig = Document
                .parse(StringUtils.replace(ResourceUtil.readFileFromClasspath(Constants.SYNONYMS_ANALYZER_SETTING_JSON),
                        Constants.SYNONYM_FILE_PATH, synonymFilePath));
        elasticSearchTemplate.indexOps(ESAnalyst.class).create(indexConfig);
        elasticSearchTemplate.indexOps(ESAnalyst.class)
                .putMapping(elasticSearchTemplate.indexOps(ESAnalyst.class).createMapping());
    }

    private BulkUploadView indexAnalystBios(List<AnalystResponse> retrieved) {
        BulkUploadView.BulkUploadBuilder builder = BulkUploadView.builder(retrieved.size());
        List<ESAnalyst> parsed = retrieved.stream().parallel().map(analystBio -> {
            try {
                return doParsing(analystBio);
            } catch (Exception e) {
                builder.addParsingFailure(analystBio.getAnalystBioId(), e.getMessage());
                LOGGER.debug("Parsing failed for analysts with ID: {} with cause: {}", analystBio.getAnalystBioId(), e.getLocalizedMessage());
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        LOGGER.info("Parsing successful for {} out of {} records.", parsed.size(), retrieved.size());
        try {
            Iterable<ESAnalyst> indexedAnalysts = elasticSearchTemplate.save(parsed);
            LOGGER.info("Indexing successful for {} out of {} records.", IterableUtils.size(indexedAnalysts), parsed.size());
        } catch (BulkFailureException ese) {
            LOGGER.debug("Indexing failed for {} analysts with ID: {} with cause: {}", ese.getFailedDocuments().size(), ese.getFailedDocuments().keySet(), ese.getFailedDocuments().values());
            builder.withIndexingFailures(ese.getFailedDocuments());
        } catch (ElasticsearchStatusException esse) {
            LOGGER.debug("Indexing failed for {} analysts with ID: {} with cause: {}", parsed.size(), esse.getResourceId(), esse.getDetailedMessage());
            builder.withIndexingFailures(parsed.stream().collect(Collectors.toMap(ESAnalyst::getAnalystBioID, esAnalystBio -> esse.getMessage())));
        }
        return builder.build();
    }

    @LogThis
    @Override
    public void deIndexById(String id) throws ServiceException {
        LOGGER.info("Removing analyst id: {}", id);
        indexAliasesBuilder.checkAndSetIndexSuffix(ESAnalyst.class);
        String analystId = null;
        try {
            if (StringUtils.startsWith(id, ANALYST_PREFIX)) {
                LOGGER.info("Removing analyst with analyst id: {}", id);
                elasticSearchTemplate.delete(id.split(ANALYST_PREFIX)[1], ESAnalyst.class);
                analystId=id.split(ANALYST_PREFIX)[1];
            } else {
                NativeSearchQuery analystIdQuery = DeIndexQueryBuilder.buildSearchQueryWithEntryId("entryId", id);
                SearchHit<ESAnalyst> searchResult = elasticSearchTemplate.searchOne(analystIdQuery, ESAnalyst.class);
                if (Objects.nonNull(searchResult) && Objects.nonNull(searchResult.getContent())){
                    analystId = searchResult.getContent().getAnalystBioID();
                }
                LOGGER.info("Removing analyst with entry id: {}", id);
                NativeSearchQuery deleteQuery = DeIndexQueryBuilder.buildDeleteQueryByField("entryId", id);
                elasticSearchTemplate.delete(deleteQuery, ESAnalyst.class);
            }
            if(null!=analystId) {
                analystBioProcessor.updateAnalystInContentTypes(analystId);
            }

        } catch (ElasticsearchStatusException e) {
            LOGGER.debug("De-Indexing failed for analyst with ID: {} with cause: {}", id, e.getDetailedMessage());
            throw new ServiceException(String.format("De-Indexing failed for analyst with ID: %s", id), e);
        }
    }
}
