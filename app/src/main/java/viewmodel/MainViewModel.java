package com.example.countryexplorerd.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.countryexplorerd.models.Sightseeing;
import com.example.countryexplorerd.network.ApiService;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private final MutableLiveData<List<Sightseeing>> sightseeingList = new MutableLiveData<>();

    public LiveData<List<Sightseeing>> getSightseeingList() {
        return sightseeingList;
    }

    public void fetchSightseeing(ApiService apiService) {
        // Если данные уже загружены, не делаем запрос повторно
        if (sightseeingList.getValue() != null && !sightseeingList.getValue().isEmpty()) {
            return;
        }

        apiService.getSightseeingData().enqueue(new Callback<List<Sightseeing>>() {
            @Override
            public void onResponse(Call<List<Sightseeing>> call, Response<List<Sightseeing>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // postValue безопаснее для фоновых потоков Retrofit
                    sightseeingList.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Sightseeing>> call, Throwable t) {
                // Если сюда попали — значит нет интернета или ошибка в URL
                t.printStackTrace();
            }
        });
    }
}