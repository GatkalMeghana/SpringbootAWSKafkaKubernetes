package com.forrester.index.utils;

public enum AutocompleteType {
    BUSINESS_TOPICS("Business Topics", "24", new int[]{TaxonomyLevelEnum.LEVEL_3.getLevel()}),
    COUNTRY("Country", "26", new int[]{TaxonomyLevelEnum.LEVEL_3.getLevel(), TaxonomyLevelEnum.LEVEL_4.getLevel()}),
    INDUSTRY("Industry", "27", new int[]{TaxonomyLevelEnum.LEVEL_3.getLevel(), TaxonomyLevelEnum.LEVEL_4.getLevel()}),
    ANALYST("Analyst", "1009", null);

    private String type;
    private String taxonomyID;
    private int[] level;

    AutocompleteType(String type, String taxonomyID, int[] level) {
        this.type = type;
        this.taxonomyID = taxonomyID;
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public String getTaxonomyID() {
        return taxonomyID;
    }

	public int[] getLevel() {
		return level;
	}
    
}

