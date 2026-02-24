package com.example.countryexplorerd;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.AppDatabase;
import ui.continents.ContinentsFragment;
import ui.continents.countries.AllCountriesFragment;
import ui.continents.quiz.QuizMenuFragment;
import ui.details.DetailsFragment;
import ui.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private List<Country> regionCountries = new ArrayList<>();
    private Map<String, CountryDetail> detailedMap = new HashMap<>();
    public static AppDatabase db;
    private CountryViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // ✅ Загружаем тему перед super.onCreate
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("dark_theme", false);

        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "world_guide_db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        viewModel = new ViewModelProvider(this).get(CountryViewModel.class);
        loadDataFromPostman();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_continents) {
                selectedFragment = new ContinentsFragment();
            } else if (id == R.id.nav_all_countries) {
                selectedFragment = new AllCountriesFragment();
            } else if (id == R.id.nav_learn) {
                selectedFragment = new QuizMenuFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
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

        viewModel.getCountries().observe(this, countries -> {
            if (countries != null) {
                regionCountries.clear();
                regionCountries.addAll(countries);
            }
        });

        viewModel.getDetails().observe(this, details -> {
            if (details != null) {
                detailedMap.clear();
                detailedMap.putAll(details);
            }
        });
    }

    public List<Country> getAllCountries() {
        return regionCountries;
    }

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