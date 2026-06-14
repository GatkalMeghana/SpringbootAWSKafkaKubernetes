package com.forrester.index.utils;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

public class DeIndexQueryBuilder {

    /**
     * Creates a NativeSearchQuery with a Match Query for the fieldName and the specified value
     * @param fieldName: name of the Field to match
     * @param value: value that has to match to be included in the results
     * @return NativeSearchQuery
     */
    public static NativeSearchQuery buildDeleteQueryByField(String fieldName, String value) {
        // Create match query to match fieldName with the exact same value that we are sending
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(fieldName, value);
        matchQueryBuilder.fuzziness(Fuzziness.ZERO);
        matchQueryBuilder.fuzzyTranspositions(false);
        matchQueryBuilder.maxExpansions(1);

        // Create NativeSearchQuery with our match query
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(matchQueryBuilder);

        return nativeSearchQueryBuilder.build();
    }

    /**
     * This method is used for building query to fetch search result basis the entryId
     * @param fieldName entryId
     * @param value Id value
     * @return instance of NativeSearchQuery
     */
    public static NativeSearchQuery buildSearchQueryWithEntryId(String fieldName, String value) {
        // Create match query to match fieldName with the exact same value that we are sending
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(fieldName, value);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(matchQueryBuilder);

        return nativeSearchQueryBuilder.build();
    }
}
