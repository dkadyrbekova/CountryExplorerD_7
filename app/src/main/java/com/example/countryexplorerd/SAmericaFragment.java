package com.example.countryexplorerd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
        // Используем fragment_quiz_region_select.xml (твой существующий файл)
        View view = inflater.inflate(R.layout.fragment_quiz_region_select, container, false);

        Button btnBack = view.findViewById(R.id.btnRegionBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Используем существующую карточку quizSAmerica как контейнер для RecyclerView
        // Или создаем отдельный layout файл fragment_south_america.xml с RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.rvSAmerica);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

            adapter = new CountryAdapter(countries);
            recyclerView.setAdapter(adapter);
        }

        // MVVM: Подключаемся к общей ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Слушаем данные из Postman
        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            if (allCountries != null) {
                countries.clear();
                // Фильтруем страны Южной Америки
                for (Country c : allCountries) {
                    if ("South America".equalsIgnoreCase(c.getRegion())) {
                        countries.add(c);
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }
}