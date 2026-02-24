package com.example.countryexplorerd.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import database.FavoriteCountry;
import com.example.countryexplorerd.MainActivity;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import database.CountryNote;
import database.VisitedCountry;
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
    private MutableLiveData<List<FavoriteCountry>> favoritesLiveData;
    private MutableLiveData<List<CountryNote>> notesLiveData;

    public CountryRepository() {
        apiService = RetrofitClient.getApiService();
        countriesLiveData = new MutableLiveData<>();
        detailsLiveData = new MutableLiveData<>();
        favoritesLiveData = new MutableLiveData<>();
        notesLiveData = new MutableLiveData<>();
    }

    // Методы для стран
    public MutableLiveData<List<Country>> getCountries() {
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

    // Методы для деталей
    public MutableLiveData<Map<String, CountryDetail>> getDetails() {
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
                    detailsLiveData.postValue(response.body());
                } else {
                    detailsLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Map<String, CountryDetail>> call, Throwable t) {
                detailsLiveData.postValue(null);
            }
        });
    }

    // Методы для избранного
    public MutableLiveData<List<FavoriteCountry>> getFavorites() {
        return favoritesLiveData;
    }

    public void loadFavorites() {
        new Thread(() -> {
            try {
                List<FavoriteCountry> favorites = MainActivity.db.favoriteDao().getAllFavorites();
                favoritesLiveData.postValue(favorites);
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to load favorites: " + e.getMessage());
                favoritesLiveData.postValue(null);
            }
        }).start();
    }

    public void addFavorite(String countryName) {
        new Thread(() -> {
            try {
                MainActivity.db.favoriteDao().insert(new FavoriteCountry(countryName));
                loadFavorites();
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to add favorite: " + e.getMessage());
            }
        }).start();
    }

    public void removeFavorite(String countryName) {
        new Thread(() -> {
            try {
                MainActivity.db.favoriteDao().delete(new FavoriteCountry(countryName));
                loadFavorites();
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to remove favorite: " + e.getMessage());
            }
        }).start();
    }

    // Методы для заметок
    public MutableLiveData<List<CountryNote>> getNotes() {
        return notesLiveData;
    }

    public void loadNotes() {
        new Thread(() -> {
            try {
                List<CountryNote> notes = MainActivity.db.noteDao().getAllNotes();
                notesLiveData.postValue(notes);
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to load notes: " + e.getMessage());
                notesLiveData.postValue(null);
            }
        }).start();
    }

    public void saveNote(String countryName, String noteText) {
        new Thread(() -> {
            try {
                MainActivity.db.noteDao().insert(new CountryNote(countryName, noteText));
                loadNotes();
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to save note: " + e.getMessage());
            }
        }).start();
    }

    public void deleteNote(String countryName) {
        new Thread(() -> {
            try {
                MainActivity.db.noteDao().deleteByCountry(countryName);
                loadNotes();
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to delete note: " + e.getMessage());
            }
        }).start();
    }

    // НОВОЕ: Методы для посещённых стран
    public LiveData<List<VisitedCountry>> getVisited() {
        return MainActivity.db.visitedDao().getAll();
    }

    public LiveData<Integer> getVisitedCount() {
        return MainActivity.db.visitedDao().getCount();
    }

    public LiveData<Integer> getVisitedContinentsCount() {
        return MainActivity.db.visitedDao().getContinentsCount();
    }

    public void addVisited(String countryName, String region) {
        new Thread(() -> {
            try {
                VisitedCountry visited = new VisitedCountry(countryName, region, System.currentTimeMillis());
                MainActivity.db.visitedDao().insert(visited);
                Log.d("CountryRepository", "Added visited: " + countryName);
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to add visited: " + e.getMessage());
            }
        }).start();
    }

    public void removeVisited(String countryName) {
        new Thread(() -> {
            try {
                VisitedCountry visited = new VisitedCountry(countryName, "", 0);
                MainActivity.db.visitedDao().delete(visited);
                Log.d("CountryRepository", "Removed visited: " + countryName);
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to remove visited: " + e.getMessage());
            }
        }).start();
    }

    public boolean isVisited(String countryName) {
        try {
            return MainActivity.db.visitedDao().isVisited(countryName) > 0;
        } catch (Exception e) {
            Log.e("CountryRepository", "Failed to check visited: " + e.getMessage());
            return false;
        }
    }
}