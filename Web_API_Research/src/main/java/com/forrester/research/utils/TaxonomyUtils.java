package com.forrester.research.utils;

import com.forrester.research.clients.taxonomy.response.TaxonomyTags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaxonomyUtils {

    static Logger logger = LoggerFactory.getLogger(TaxonomyUtils.class);

    private static final int TEAM_SITE_PRODUCT_OFFSET = 978287400;

    public static Integer decodeProductTag(String tag) {
        try {
            return Integer.parseInt(tag, 16) - TEAM_SITE_PRODUCT_OFFSET;
        } catch (NumberFormatException e) {
            logger.error("Failed to parse long for {}", tag, e);
            return null;
        }
    }

    /**
     * Checks if there is ISG tag on taxonomy info.
     *
     * @param taxonomyTags TaxonomyTags
     * @param isgId String
     * @return TRUE if there is ISG tag.
     */
    public static boolean isISGData(TaxonomyTags taxonomyTags, String isgId) {
        return taxonomyTags.getTags()
                .stream()
                .anyMatch(tag -> tag.getId().equals(isgId));
    }
    
    // Internal
    
    private TaxonomyUtils() {
        // private constructor to prevent instantiation
    }
}
