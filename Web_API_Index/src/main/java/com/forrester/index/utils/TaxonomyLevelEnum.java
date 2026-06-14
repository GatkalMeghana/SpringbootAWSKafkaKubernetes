package com.forrester.index.utils;

public enum TaxonomyLevelEnum {
    LEVEL_1(0),
    LEVEL_2(1),
    LEVEL_3(2),
    LEVEL_4(3);

    private int level;

    TaxonomyLevelEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
