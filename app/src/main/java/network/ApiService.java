package com.example.countryexplorerd.network;

import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.models.Sightseeing;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // Запрос списка стран - исправлено
    @GET("countries")  // было "Get data"
    Call<List<Country>> getAllCountries();

    // Запрос деталей стран - исправлено
    @GET("details")    // было "details" (правильно)
    Call<Map<String, CountryDetail>> getCountryDetails();

    // Запрос достопримечательностей - исправлено
    @GET("sightseeing")  // было "top"
    Call<List<Sightseeing>> getSightseeingData();
}