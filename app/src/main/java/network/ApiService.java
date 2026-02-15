package com.example.countryexplorerd.network;

import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.models.Sightseeing;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // Запрос списка стран
    @GET("Get data")
    Call<List<Country>> getAllCountries();

    // Запрос деталей стран
    @GET("details")
    Call<Map<String, CountryDetail>> getCountryDetails();

    // НОВЫЙ ЗАПРОС: Достопримечательности для нашей карусели
    // Обновили путь на "top", так как твоя ссылка ведет именно туда
    @GET("top")
    Call<List<Sightseeing>> getSightseeingData();
}