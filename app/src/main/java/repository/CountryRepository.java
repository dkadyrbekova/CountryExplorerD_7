package com.example.countryexplorerd.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import database.DailyChallenge;
import database.FavoriteCountry;
import com.example.countryexplorerd.MainActivity;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import database.CountryNote;
import database.VisitedCountry;
import com.example.countryexplorerd.network.ApiService;
import com.example.countryexplorerd.network.RetrofitClient;
import java.util.Calendar;
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

    // ════════════════════════════════════════════════════════
    // СТРАНЫ
    // ════════════════════════════════════════════════════════

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

    // ════════════════════════════════════════════════════════
    // ДЕТАЛИ / ФАКТЫ
    // ════════════════════════════════════════════════════════

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

    // ════════════════════════════════════════════════════════
    // ИЗБРАННОЕ
    // ════════════════════════════════════════════════════════

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

    // ════════════════════════════════════════════════════════
    // ЗАМЕТКИ
    // ════════════════════════════════════════════════════════

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

    // ════════════════════════════════════════════════════════
    // ПОСЕЩЁННЫЕ СТРАНЫ
    // ════════════════════════════════════════════════════════

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

    // ════════════════════════════════════════════════════════
    // ЧЕЛЛЕНДЖ ДНЯ (30 заданий, без сохранения прогресса)
    // ════════════════════════════════════════════════════════

    public LiveData<DailyChallenge> getTodayChallenge() {
        Calendar cal = Calendar.getInstance();
        int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);

        // В фоне проверяем — если записи на сегодня нет, создаём
        new Thread(() -> {
            try {
                DailyChallenge existing = MainActivity.db.challengeDao()
                        .getTodayChallengeSync(dayOfYear, year);

                if (existing == null) {
                    DailyChallenge newChallenge = generateChallenge(dayOfYear, year);
                    MainActivity.db.challengeDao().insert(newChallenge);
                    Log.d("CountryRepository", "Created challenge for day " + dayOfYear + ": " + newChallenge.getTitle());
                }

                // Чистим старые записи (старше 30 дней)
                int minDay = dayOfYear - 30;
                if (minDay > 0) {
                    MainActivity.db.challengeDao().deleteOld(minDay, year);
                }
            } catch (Exception e) {
                Log.e("CountryRepository", "Failed to init challenge: " + e.getMessage());
            }
        }).start();

        // Room сам уведомит UI через LiveData когда запись появится
        return MainActivity.db.challengeDao().getTodayChallenge(dayOfYear, year);
    }

    // Метод больше не нужен, но оставляем для совместимости
    public void updateChallengeProgress(DailyChallenge challenge) {
        // Ничего не делаем - прогресс не сохраняем
        Log.d("CountryRepository", "Challenge progress not saved (simple mode)");
    }

    // Генерируем задание по номеру дня — 30 разных заданий
    private DailyChallenge generateChallenge(int dayOfYear, int year) {
        // { type, continent, title, description }
        String[][] challenges = {
                // День 1-5: Флаги
                {"flags", "world", "Флаги мира", "Угадай флаги разных стран мира"},
                {"flags", "Europe", "Флаги Европы", "Угадай флаги европейских стран"},
                {"flags", "Asia", "Флаги Азии", "Угадай флаги азиатских стран"},
                {"flags", "Africa", "Флаги Африки", "Угадай флаги африканских стран"},
                {"flags", "Americas", "Флаги Америки", "Угадай флаги стран Северной и Южной Америки"},

                // День 6-10: Столицы
                {"capitals", "world", "Столицы мира", "Угадай столицы разных стран"},
                {"capitals", "Europe", "Столицы Европы", "Угадай столицы европейских стран"},
                {"capitals", "Asia", "Столицы Азии", "Угадай столицы азиатских стран"},
                {"capitals", "Africa", "Столицы Африки", "Угадай столицы африканских стран"},
                {"capitals", "Americas", "Столицы Америки", "Угадай столицы стран Америки"},

                // День 11-15: Валюты
                {"currency", "world", "Валюты мира", "Угадай валюты разных стран"},
                {"currency", "Europe", "Валюты Европы", "Угадай валюты европейских стран"},
                {"currency", "Asia", "Валюты Азии", "Угадай валюты азиатских стран"},
                {"currency", "Africa", "Валюты Африки", "Угадай валюты африканских стран"},
                {"currency", "Americas", "Валюты Америки", "Угадай валюты стран Америки"},

                // День 16-20: Смешанные
                {"flags", "Oceania", "Флаги Океании", "Угадай флаги стран Океании"},
                {"capitals", "Oceania", "Столицы Океании", "Угадай столицы стран Океании"},
                {"flags", "Asia", "Флаги Азии (сложные)", "Угадай сложные флаги азиатских стран"},
                {"capitals", "Europe", "Столицы Европы (сложные)", "Угадай сложные столицы Европы"},
                {"currency", "world", "Валюты мира (редкие)", "Угадай редкие валюты стран"},

                // День 21-25: По континентам
                {"flags", "Africa", "Флаги Африки (сложные)", "Угадай сложные флаги африканских стран"},
                {"capitals", "Asia", "Столицы Азии (сложные)", "Угадай сложные столицы Азии"},
                {"flags", "South America", "Флаги Южной Америки", "Угадай флаги стран Южной Америки"},
                {"capitals", "South America", "Столицы Южной Америки", "Угадай столицы стран Южной Америки"},
                {"flags", "North America", "Флаги Северной Америки", "Угадай флаги стран Северной Америки"},

                // День 26-30: Финальные
                {"capitals", "North America", "Столицы Северной Америки", "Угадай столицы стран Северной Америки"},
                {"flags", "Islands", "Флаги островных государств", "Угадай флаги островных стран"},
                {"capitals", "Islands", "Столицы островных государств", "Угадай столицы островных стран"},
                {"currency", "exotic", "Экзотические валюты", "Угадай валюты экзотических стран"},
                {"flags", "world", "Флаги мира (финальный)", "Угадай все флаги мира"}
        };

        int index = (dayOfYear - 1) % challenges.length;
        String[] c = challenges[index];

        return new DailyChallenge(
                dayOfYear,
                year,
                c[2], // title
                c[3], // description
                c[0], // type (flags/capitals/currency)
                c[1], // continent
                0     // targetCount больше не нужен
        );
    }
}