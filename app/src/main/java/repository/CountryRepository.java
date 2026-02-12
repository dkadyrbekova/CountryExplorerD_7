package com.example.countryexplorerd.repository;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.network.ApiService;
import com.example.countryexplorerd.network.RetrofitClient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryRepository {
    private ApiService apiService;
    private MutableLiveData<List<Country>> countriesLiveData;
    private MutableLiveData<Map<String, CountryDetail>> detailsLiveData;

    public CountryRepository() {
        apiService = RetrofitClient.getApiService();
        countriesLiveData = new MutableLiveData<>();
        detailsLiveData = new MutableLiveData<>();
    }

    // Метод для загрузки списка всех стран
    public MutableLiveData<List<Country>> getCountries() {
        // Загружаем данные только если еще не загружены
        if (countriesLiveData.getValue() == null) {
            loadCountries();
        }
        return countriesLiveData;
    }

    private void loadCountries() {
        apiService.getAllCountries().enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CountryRepository", "Loaded " + response.body().size() + " countries");
                    countriesLiveData.postValue(response.body());
                } else {
                    Log.e("CountryRepository", "Response not successful: " + response.code());
                    countriesLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                Log.e("CountryRepository", "Failed to load countries: " + t.getMessage());
                countriesLiveData.postValue(null);
            }
        });
    }

    // Метод для загрузки деталей (фактов)
    public MutableLiveData<Map<String, CountryDetail>> getDetails() {
        // Загружаем данные только если еще не загружены
        if (detailsLiveData.getValue() == null) {
            loadDetails();
        }
        return detailsLiveData;
    }

    private void loadDetails() {
        apiService.getCountryDetails().enqueue(new Callback<Map<String, CountryDetail>>() {
            @Override
            public void onResponse(Call<Map<String, CountryDetail>> call, Response<Map<String, CountryDetail>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CountryRepository", "Loaded details for " + response.body().size() + " countries");
                    detailsLiveData.postValue(response.body());
                } else {
                    Log.e("CountryRepository", "Details response not successful: " + response.code());
                    detailsLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Map<String, CountryDetail>> call, Throwable t) {
                Log.e("CountryRepository", "Failed to load details: " + t.getMessage());
                detailsLiveData.postValue(null);
            }
        });
    }
}