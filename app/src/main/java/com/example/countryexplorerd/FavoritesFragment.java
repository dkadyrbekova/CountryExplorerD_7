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
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_countries, container, false);

        recyclerView = view.findViewById(R.id.rvAllCountries);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CountryAdapter(favoriteList);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

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
        if (viewModel.getCountries().getValue() != null) {
            updateFavorites(viewModel.getCountries().getValue());
        }
    }

    private void updateFavorites(List<Country> allCountries) {
        favoriteList.clear();
        List<FavoriteCountry> dbFavs = MainActivity.db.favoriteDao().getAllFavorites();

        for (FavoriteCountry f : dbFavs) {
            for (Country c : allCountries) {
                if (c.getName().equals(f.getCountryName())) {
                    favoriteList.add(c);
                    break;
                }
            }
        }

        if (adapter != null && recyclerView != null) {
            recyclerView.post(() -> adapter.notifyDataSetChanged());
        }
    }
}