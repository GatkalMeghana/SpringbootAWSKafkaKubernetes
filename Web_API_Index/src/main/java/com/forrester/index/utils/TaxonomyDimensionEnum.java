package com.forrester.index.utils;

public enum TaxonomyDimensionEnum {
    BUSINESS_TOPICS("Business Topics"),
    COUNTRY("Country"),
    INDUSTRY("Industry"),
    LEGACY("Legacy"),
    SERVICES("Services"),
    TECHNOLOGY_AND_SERVICES("Technology and Services Categories"),
    VENDOR("Vendor"),
    VISION("Vision");

    private String name;

    TaxonomyDimensionEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
