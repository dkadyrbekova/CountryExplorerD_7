package com.example.countryexplorerd.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.countryexplorerd.models.Sightseeing;
import com.example.countryexplorerd.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SightseeingRepository {
    private ApiService apiService;

    public SightseeingRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public LiveData<List<Sightseeing>> getSights() {
        MutableLiveData<List<Sightseeing>> data = new MutableLiveData<>();
        apiService.getSightseeingData().enqueue(new Callback<List<Sightseeing>>() {
            @Override
            public void onResponse(Call<List<Sightseeing>> call, Response<List<Sightseeing>> response) {
                if (response.isSuccessful()) data.setValue(response.body());
            }
            @Override
            public void onFailure(Call<List<Sightseeing>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}