package com.example.countryexplorerd.models; // Лежит в той же папке, что и Country

public class CountryDetail {
    private String currency;
    private String language;
    private String facts;

    public CountryDetail() {}

    // Геттеры с защитой от пустых данных
    public String getCurrency() {
        return (currency != null && !currency.isEmpty()) ? currency : "Нет данных";
    }

    public String getLanguage() {
        return (language != null && !language.isEmpty()) ? language : "Не указан";
    }

    public String getFacts() {
        return (facts != null && !facts.isEmpty()) ? facts : "Интересные факты скоро появятся!";
    }
}