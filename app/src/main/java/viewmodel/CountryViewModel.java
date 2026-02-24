package com.example.countryexplorerd.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import database.FavoriteCountry;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import database.CountryNote;
import com.example.countryexplorerd.repository.CountryRepository;
import java.util.List;
import java.util.Map;

public class CountryViewModel extends ViewModel {
    private CountryRepository repository;
    private LiveData<List<Country>> countriesLiveData;
    private LiveData<Map<String, CountryDetail>> detailsLiveData;
    private LiveData<List<FavoriteCountry>> favoritesLiveData;
    private LiveData<List<CountryNote>> notesLiveData;

    public CountryViewModel() {
        repository = new CountryRepository();
    }

    // Методы для стран
    public LiveData<List<Country>> getCountries() {
        if (countriesLiveData == null) {
            countriesLiveData = repository.getCountries();
        }
        return countriesLiveData;
    }

    // Методы для деталей (фактов)
    public LiveData<Map<String, CountryDetail>> getDetails() {
        if (detailsLiveData == null) {
            detailsLiveData = repository.getDetails();
        }
        return detailsLiveData;
    }

    // НОВОЕ: Методы для избранного
    public LiveData<List<FavoriteCountry>> getFavorites() {
        if (favoritesLiveData == null) {
            favoritesLiveData = repository.getFavorites();
        }
        return favoritesLiveData;
    }

    public void loadFavorites() {
        repository.loadFavorites();
    }

    public void addFavorite(String countryName) {
        repository.addFavorite(countryName);
    }

    public void removeFavorite(String countryName) {
        repository.removeFavorite(countryName);
    }

    // НОВОЕ: Методы для заметок
    public LiveData<List<CountryNote>> getNotes() {
        if (notesLiveData == null) {
            notesLiveData = repository.getNotes();
        }
        return notesLiveData;
    }

    public void loadNotes() {
        repository.loadNotes();
    }

    public void saveNote(String countryName, String noteText) {
        repository.saveNote(countryName, noteText);
    }

    public void deleteNote(String countryName) {
        repository.deleteNote(countryName);
    }
}