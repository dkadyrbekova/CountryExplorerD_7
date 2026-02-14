package com.example.countryexplorerd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class SAmericaFragment extends Fragment {

    private final List<Country> countries = new ArrayList<>();
    private CountryAdapter adapter;
    private CountryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_south_america, container, false);

        Button btnBack = view.findViewById(R.id.btnRegionBack);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        TextView emptyText = view.findViewById(R.id.emptyText);
        RecyclerView recyclerView = view.findViewById(R.id.rvSAmerica);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            adapter = new CountryAdapter(countries);
            recyclerView.setAdapter(adapter);
        }

        // Показываем ProgressBar, скрываем список
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        if (emptyText != null) emptyText.setVisibility(View.GONE);

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            // Скрываем ProgressBar
            if (progressBar != null) progressBar.setVisibility(View.GONE);

            if (allCountries != null && !allCountries.isEmpty()) {
                countries.clear();
                for (Country c : allCountries) {
                    if ("South America".equalsIgnoreCase(c.getRegion())) {
                        countries.add(c);
                    }
                }

                if (countries.isEmpty()) {
                    // Нет стран для этого региона
                    if (emptyText != null) emptyText.setVisibility(View.VISIBLE);
                    if (recyclerView != null) recyclerView.setVisibility(View.GONE);
                } else {
                    // Есть страны - показываем список
                    if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
                    if (emptyText != null) emptyText.setVisibility(View.GONE);
                    if (adapter != null && recyclerView != null) {
                        recyclerView.post(() -> adapter.notifyDataSetChanged());
                    }
                }
            } else {
                // Данные не пришли
                if (emptyText != null) {
                    emptyText.setText("Не удалось загрузить данные");
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }
}