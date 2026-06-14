package com.forrester.index.utils;

/**
 * This class implementation provides IndexName for ElasticSearchData.
 * 
 * @author meghanag
 *
 */
public class IndexNameProvider {

	private String indexSuffix;

	/**
	 * This method return indexSuffix.
	 * 
	 * @return String
	 */
	public String getIndexSuffix() {
		return indexSuffix;
	}

	/**
	 * This method sets indexSuffix.
	 * 
	 * @param indexSuffix
	 */
	public void setIndexSuffix(String indexSuffix) {
		this.indexSuffix = indexSuffix;
	}
}