package com.example.countryexplorerd.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.repository.CountryRepository;

import java.util.List;
import java.util.Map;

public class CountryViewModel extends ViewModel {
    private CountryRepository repository;
    private LiveData<List<Country>> countriesLiveData;
    private LiveData<Map<String, CountryDetail>> detailsLiveData;

    public CountryViewModel() {
        repository = new CountryRepository();
    }

    // Этот метод вызовет экран, чтобы получить список стран
    public LiveData<List<Country>> getCountries() {
        if (countriesLiveData == null) {
            countriesLiveData = repository.getCountries();
        }
        return countriesLiveData;
    }

    // Этот метод вызовет экран, чтобы получить детали (факты)
    public LiveData<Map<String, CountryDetail>> getDetails() {
        if (detailsLiveData == null) {
            detailsLiveData = repository.getDetails();
        }
        return detailsLiveData;
    }
}