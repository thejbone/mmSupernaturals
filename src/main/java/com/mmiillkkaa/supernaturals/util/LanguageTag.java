package com.mmiillkkaa.supernaturals.util;

public enum LanguageTag {

    AMOUNT("<AMOUNT>"),
    CLASS("<CLASS>"),
    CMD("<CMD>"),
    DELTA("<DELTA>"),
    POWER("<POWER>"),
    PLAYER("<PLAYER>"),
    REASON("<REASON>"),
    TYPE("<TYPE>"),
    MATERIAL("<MATERIAL>"),
    MATERIAL_SURROUND("<MATERIAL_SURROUND>"),
    MSG("MSG"),
    ;

    private String tag;

    LanguageTag(String tag) {
        this.tag = tag;
    }

    public String toString() {
        return this.tag;
    }
}
