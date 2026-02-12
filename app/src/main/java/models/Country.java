package com.example.countryexplorerd.models; // Указываем правильный путь в новом проекте

import com.google.gson.annotations.SerializedName;

public class Country {
    // Поля соответствуют твоему JSON из Postman
    private String name;
    private String capital;
    private String flag;
    private String region;

    @SerializedName("currency") // Помогает GSON найти поле, если в JSON оно называется так же
    private String currency;

    // Пустой конструктор нужен для библиотек
    public Country() {}

    // Геттеры (через них адаптер берет данные для отрисовки)
    public String getName() { return name != null ? name : "Unknown"; }
    public String getCapital() { return capital != null ? capital : "No Capital"; }
    public String getFlag() { return flag; } // Здесь будет ссылка на картинку или эмодзи
    public String getRegion() { return region; }
    public String getCurrency() { return currency != null ? currency : "N/A"; }
}