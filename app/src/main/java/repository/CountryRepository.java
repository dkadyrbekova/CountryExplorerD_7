package com.example.countryexplorerd.repository;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import com.example.countryexplorerd.FavoriteCountry;
import com.example.countryexplorerd.MainActivity;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.models.CountryNote;
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

    // НОВОЕ: LiveData для избранного и заметок
    private MutableLiveData<List<FavoriteCountry>> favoritesLiveData;
    private MutableLiveData<List<CountryNote>> notesLiveData;

    public CountryRepository() {
        apiService = RetrofitClient.getApiService();
        countriesLiveData = new MutableLiveData<>();
        detailsLiveData = new MutableLiveData<>();
        favoritesLiveData = new MutableLiveData<>();
        notesLiveData = new MutableLiveData<>();
    }

    // Метод для загрузки списка всех стран
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

    // Метод для загрузки деталей (фактов)
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

    // НОВОЕ: Методы для избранного
    public MutableLiveData<List<FavoriteCountry>> getFavorites() {
        return favoritesLiveData;
    }

    public void loadFavorites() {
        new Thread(() -> {
            try {
                List<FavoriteCountry> favorites = MainActivity.db.favoriteDao().getAllFavorites();
                Log.d("CountryRepository", "Loaded " + favorites.size() + " favorites");
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
                FavoriteCountry fav = new FavoriteCountry(countryName);
                MainActivity.db.favoriteDao().insert(fav);
                Log.d("CountryRepository", "Added favorite: " + countryName);
                loadFavorites(); // Обновляем список
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to add favorite: " + e.getMessage());
            }
        }).start();
    }

    public void removeFavorite(String countryName) {
        new Thread(() -> {
            try {
                FavoriteCountry fav = new FavoriteCountry(countryName);
                MainActivity.db.favoriteDao().delete(fav);
                Log.d("CountryRepository", "Removed favorite: " + countryName);
                loadFavorites(); // Обновляем список
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to remove favorite: " + e.getMessage());
            }
        }).start();
    }

    // НОВОЕ: Методы для заметок
    public MutableLiveData<List<CountryNote>> getNotes() {
        return notesLiveData;
    }

    public void loadNotes() {
        new Thread(() -> {
            try {
                List<CountryNote> notes = MainActivity.db.noteDao().getAllNotes();
                Log.d("CountryRepository", "Loaded " + notes.size() + " notes");
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
                CountryNote note = new CountryNote(countryName, noteText);
                MainActivity.db.noteDao().insert(note);
                Log.d("CountryRepository", "Saved note for: " + countryName);
                loadNotes(); // Обновляем список
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to save note: " + e.getMessage());
            }
        }).start();
    }

    public void deleteNote(String countryName) {
        new Thread(() -> {
            try {
                MainActivity.db.noteDao().deleteByCountry(countryName);
                Log.d("CountryRepository", "Deleted note for: " + countryName);
                loadNotes(); // Обновляем список
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to delete note: " + e.getMessage());
            }
        }).start();
    }
}