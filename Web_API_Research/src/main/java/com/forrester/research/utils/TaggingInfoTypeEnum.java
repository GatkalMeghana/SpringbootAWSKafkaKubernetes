package com.forrester.research.utils;


public enum TaggingInfoTypeEnum {
    TYPE_SERVICES("services", "21"),
    TYPE_VISION("vision", "20"),
    TYPE_OLD_FORRESTER_PRODUCTS("Old Forrester Product", "30"),
    TYPE_OLD_ROLE("Old Role", "31"),
    TYPE_SD_SERVICES("SD Services", "5201");


    private final String typeName;
    private final String typeId;

    TaggingInfoTypeEnum(String typeName, String typeId) {
        this.typeName = typeName;
        this.typeId = typeId;
    }

    public static TaggingInfoTypeEnum fromTypeId(String typeId) {

        for (TaggingInfoTypeEnum taggingInfoContentTypeEnum : TaggingInfoTypeEnum.values()) {
            if (taggingInfoContentTypeEnum.getTypeId().equalsIgnoreCase(typeId)) {
                return taggingInfoContentTypeEnum;
            }
        }

        return null;
    }

    public String getTypeId() {
        return typeId;
    }
}