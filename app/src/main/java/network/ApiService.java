package com.example.countryexplorerd.network;

import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // Запрос списка стран (твой первый JSON)
    @GET("Get data")
    Call<List<Country>> getAllCountries();

    // Запрос деталей (твой новый большой JSON)
    // Используем Map, так как в JSON страны идут по именам
    @GET("details")
    Call<Map<String, CountryDetail>> getCountryDetails();
}