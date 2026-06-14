package com.forrester.index.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.*;

@JsonPropertyOrder({
        "total",
        "duplicates",
        "duplicateRecords",
        "retrieved",
        "retrievalFailures",
        "parsed",
        "parsingFailures",
        "indexed",
        "indexingFailures"
})
public class BulkUploadView {
    private long total;
    private List<String> duplicateRecords;
    private Map<String, String> retrievalFailures;
    private Map<String, String> parsingFailures;
    private Map<String, String> indexingFailures;

    private BulkUploadView(long total, List<String> duplicateRecords, Map<String, String> retrievalFailures, Map<String, String> parsingFailures, Map<String, String> indexingFailures) {
        this.total = total;
        this.duplicateRecords = duplicateRecords;
        this.retrievalFailures = retrievalFailures;
        this.parsingFailures = parsingFailures;
        this.indexingFailures = indexingFailures;
    }

    public static BulkUploadBuilder builder() {
        return new BulkUploadBuilder();
    }

    public static BulkUploadBuilder builder(long total) {
        return new BulkUploadBuilder(total);
    }

    public long getTotal() {
        return total;
    }

    public long getDuplicates() {
        return duplicateRecords.size();
    }

    public long getRetrieved() {
        return total - (duplicateRecords.size() + retrievalFailures.size());
    }

    public List<String> getDuplicateRecords() {
        return duplicateRecords;
    }

    public Map<String, String> getRetrievalFailures() {
        return retrievalFailures;
    }

    public long getParsed() {
        return total - (duplicateRecords.size() + retrievalFailures.size() + parsingFailures.size());
    }

    public Map<String, String> getParsingFailures() {
        return parsingFailures;
    }

    public long getIndexed() {
        return total - (duplicateRecords.size() + retrievalFailures.size() + parsingFailures.size() + indexingFailures.size());
    }

    public Map<String, String> getIndexingFailures() {
        return indexingFailures;
    }

    public static final class BulkUploadBuilder {
        private long total;
        private List<String> duplicateRecords = new ArrayList<>();
        private Map<String, String> retrievalFailures = new HashMap<>();
        private Map<String, String> parsingFailures = new HashMap<>();
        private Map<String, String> indexingFailures = new HashMap<>();

        private BulkUploadBuilder() {
        	super();
        	this.total = 0;
        }
        
        private BulkUploadBuilder(long total) {
            this.total = total;
        }

        public synchronized BulkUploadBuilder addRetrievalFailure(String key, String value) {
            this.retrievalFailures.put(key, value);
            return this;
        }

        public synchronized BulkUploadBuilder withRetrievalFailures(Map<String, String> retrievalFailures) {
            this.retrievalFailures.putAll(retrievalFailures);
            return this;
        }

        public synchronized BulkUploadBuilder addDuplicateRecord(String record) {
            this.duplicateRecords.add(record);
            return this;
        }

        public synchronized BulkUploadBuilder withDuplicateRecords(List<String> duplicateFailures) {
            this.duplicateRecords.addAll(duplicateFailures);
            return this;
        }

        public synchronized BulkUploadBuilder addParsingFailure(String key, String value) {
            this.parsingFailures.put(key, value);
            return this;
        }

        public synchronized BulkUploadBuilder withParsingFailures(Map<String, String> parsingFailures) {
            this.parsingFailures.putAll(parsingFailures);
            return this;
        }

        public synchronized BulkUploadBuilder addIndexingFailure(String key, String value) {
            this.indexingFailures.put(key, value);
            return this;
        }

        public synchronized BulkUploadBuilder withIndexingFailures(Map<String, String> indexingFailures) {
            this.indexingFailures.putAll(indexingFailures);
            return this;
        }

        public synchronized BulkUploadBuilder addToTotal(long records) {
        	this.total += records;
        	return this;
        }

        public BulkUploadView build() {
            return new BulkUploadView(total, duplicateRecords, retrievalFailures, parsingFailures, indexingFailures);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BulkUploadView.class.getSimpleName() + "[", "]")
                .add("total=" + total)
                .add("duplicateRecords=" + duplicateRecords)
                .add("retrievalFailures=" + retrievalFailures)
                .add("parsingFailures=" + parsingFailures)
                .add("indexingFailures=" + indexingFailures)
                .toString();
    }
}