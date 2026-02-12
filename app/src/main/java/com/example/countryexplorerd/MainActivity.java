package com.example.countryexplorerd; // Проверь название своего пакета!

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Добавили
import androidx.room.Room;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.viewmodel.CountryViewModel; // Добавили
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<Country> regionCountries = new ArrayList<>();
    private Map<String, CountryDetail> detailedMap = new HashMap<>();
    public static AppDatabase db;
    private CountryViewModel viewModel; // Наш новый "мозг"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Настройка базы данных Room (оставляем как было)
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "world_guide_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        // 2. Инициализируем ViewModel
        viewModel = new ViewModelProvider(this).get(CountryViewModel.class);

        // 3. Загружаем данные из Postman вместо файлов assets
        loadDataFromPostman();

        // 4. Навигация (оставляем твой код)
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_continents) {
                selectedFragment = new ContinentsFragment();
            } else if (id == R.id.nav_all_countries) {
                selectedFragment = new AllCountriesFragment();
            } else if (id == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (id == R.id.nav_quiz) {
                selectedFragment = new QuizMenuFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_continents);
        }
    }

    private void loadDataFromPostman() {
        // Подписываемся на список стран
        viewModel.getCountries().observe(this, countries -> {
            if (countries != null) {
                regionCountries.clear();
                regionCountries.addAll(countries);
                // Тут можно обновить текущий фрагмент, если он уже открыт
            }
        });

        // Подписываемся на детали
        viewModel.getDetails().observe(this, details -> {
            if (details != null) {
                detailedMap.clear();
                detailedMap.putAll(details);
            }
        });
    }

    // Твой метод для получения списка (теперь из переменной)
    public List<Country> getAllCountries() { return regionCountries; }

    // Твой метод открытия деталей (немного подправил под новые классы)
    public void openDetails(Country countryFromList) {
        Bundle b = new Bundle();
        b.putString("country_name", countryFromList.getName());
        b.putString("country_capital", countryFromList.getCapital());
        b.putString("country_flag", countryFromList.getFlag());

        CountryDetail details = detailedMap.get(countryFromList.getName());
        if (details != null) {
            b.putString("country_currency", details.getCurrency());
            b.putString("country_language", details.getLanguage());
            b.putString("country_info", details.getFacts());
        }

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}