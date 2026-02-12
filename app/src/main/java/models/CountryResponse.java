package com.example.countryexplorerd.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class CountryResponse {
    @SerializedName("name")
    private Map<String, String> nameMap;

    @SerializedName("capital")
    private List<String> capitalList;

    @SerializedName("flags")
    private Map<String, String> flagsMap;

    public String getName() {
        return nameMap != null ? nameMap.get("common") : "Unknown";
    }

    public String getCapital() {
        return (capitalList != null && !capitalList.isEmpty()) ? capitalList.get(0) : "No Capital";
    }

    public String getFlagUrl() {
        return flagsMap != null ? flagsMap.get("png") : "";
    }
}