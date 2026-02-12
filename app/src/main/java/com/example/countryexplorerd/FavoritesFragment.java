package com.example.countryexplorerd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private final List<Country> favoriteList = new ArrayList<>();
    private CountryAdapter adapter;
    private CountryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Используем ту же разметку, что и для всех стран (это удобно!)
        View view = inflater.inflate(R.layout.fragment_all_countries, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rvAllCountries);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CountryAdapter(favoriteList);
        recyclerView.setAdapter(adapter);

        // Инициализируем ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Слушаем данные
        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null) {
                updateFavorites(countries);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Когда возвращаемся на экран, обновляем список (вдруг что-то удалили из избранного)
        if (viewModel.getCountries().getValue() != null) {
            updateFavorites(viewModel.getCountries().getValue());
        }
    }

    private void updateFavorites(List<Country> allCountries) {
        favoriteList.clear();
        // 1. Получаем список имен из Room
        List<FavoriteCountry> dbFavs = MainActivity.db.favoriteDao().getAllFavorites();

        // 2. Ищем данные этих стран в общем списке из Postman
        for (FavoriteCountry f : dbFavs) {
            for (Country c : allCountries) {
                if (c.getName().equals(f.getCountryName())) { // Используй getter, если поле private
                    favoriteList.add(c);
                    break;
                }
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}