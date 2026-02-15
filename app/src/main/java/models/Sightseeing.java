package com.example.countryexplorerd.models;

import com.google.gson.annotations.SerializedName;

public class Sightseeing {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("country") // В JSON именно "country"
    private String country;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("fact")
    private String fact;

    @SerializedName("description")
    private String description;

    // Геттеры
    public String getTitle() { return title; }
    public String getCountry() { return country; } // Изменили с getLocation
    public String getImageUrl() { return imageUrl; }
    public String getFact() { return fact; }
    public String getDescription() { return description; }
}