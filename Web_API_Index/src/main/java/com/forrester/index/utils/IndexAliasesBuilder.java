package com.forrester.index.utils;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.index.AliasAction;
import org.springframework.data.elasticsearch.core.index.AliasActionParameters;
import org.springframework.data.elasticsearch.core.index.AliasActions;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

/**
 * This class builds index and aliases for given ElasticSearch Class.
 * 
 * @author meghanag
 *
 */
public class IndexAliasesBuilder {
	private static final String INDEX1 = "index1";

	private static final String INDEX2 = "index2";

	@Autowired
	private ElasticsearchOperations elasticSearchTemplate;

	@Autowired
	private IndexNameProvider indexNameProvider;

	/**
	 * This method create index as per ElasticSearchData class and IndexName.
	 * 
	 * @param esClass
	 * @param existingIndexCo
	 * @return existingIndexCo Optional<IndexCoordinates>
	 */
	public Optional<IndexCoordinates> createIndex(Class esClass) {
		Optional<IndexCoordinates> existingIndexCo = Optional.empty();
		indexNameProvider.setIndexSuffix(INDEX1);
		if (elasticSearchTemplate.indexOps(esClass).exists()) {
			existingIndexCo = Optional.of(elasticSearchTemplate.getIndexCoordinatesFor(esClass));
			indexNameProvider.setIndexSuffix(INDEX2);
		}else {
			indexNameProvider.setIndexSuffix(INDEX2);
			if (elasticSearchTemplate.indexOps(esClass).exists()) {
				existingIndexCo = Optional.of(elasticSearchTemplate.getIndexCoordinatesFor(esClass));
				indexNameProvider.setIndexSuffix(INDEX1);
			} else {
				indexNameProvider.setIndexSuffix(INDEX1);
			}
		}
		return existingIndexCo;
	}

	/**
	 * This method create Aliases as per ElasticSearchData class and aliasName and
	 * remove alias from existing index.
	 * 
	 * @param esClass
	 * @param aliasName
	 * @param existingIndexCo
	 */
	public boolean createAliases(Class esClass, String aliasName, Optional<IndexCoordinates> existingIndexCo) {
		AliasActions aliasActions = new AliasActions();
		if (existingIndexCo.isPresent()) {
			aliasActions.add(new AliasAction.Add(AliasActionParameters.builder()
					.withIndices(elasticSearchTemplate.getIndexCoordinatesFor(esClass).getIndexName())
					.withAliases(aliasName).build()));
			elasticSearchTemplate.indexOps(esClass).alias(aliasActions);
			return elasticSearchTemplate.indexOps(existingIndexCo.get()).delete();
		} else {
			aliasActions.add(new AliasAction.Add(AliasActionParameters.builder()
					.withIndices(elasticSearchTemplate.getIndexCoordinatesFor(esClass).getIndexName())
					.withAliases(aliasName).build()));

			return elasticSearchTemplate.indexOps(esClass).alias(aliasActions);
		}
	}

	/**
	 * This method check and sets indexname exists on elasticsearch used for
	 * indexById and Deindex.
	 * 
	 * @param esClass
	 */
	public void checkAndSetIndexSuffix(Class esClass) {
		indexNameProvider.setIndexSuffix(INDEX1);
		if (!elasticSearchTemplate.indexOps(esClass).exists()) {
			indexNameProvider.setIndexSuffix(INDEX2);
		}
	}
	
	/**
	 * This method delete the index for specified class.
	 * @param esClass
	 * @return boolean
	 */
	public boolean deleteIndex(Class esClass) {
		IndexCoordinates indexCo = elasticSearchTemplate.getIndexCoordinatesFor(esClass);
		return elasticSearchTemplate.indexOps(indexCo).delete();
	}
}
