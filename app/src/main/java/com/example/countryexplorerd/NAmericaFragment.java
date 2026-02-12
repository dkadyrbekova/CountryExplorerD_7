package com.example.countryexplorerd; // Твой новый пакет

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class NAmericaFragment extends Fragment {

    private final List<Country> countries = new ArrayList<>();
    private CountryAdapter adapter;
    private CountryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Убедись, что файл fragment_north_america.xml перенесен в res/layout
        View view = inflater.inflate(R.layout.fragment_north_america, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackNA);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        RecyclerView recyclerView = view.findViewById(R.id.rvNAmerica);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CountryAdapter(countries);
        recyclerView.setAdapter(adapter);

        // MVVM: Подключаемся к ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Слушаем данные из Postman
        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            if (allCountries != null) {
                countries.clear();
                // Фильтруем страны Северной Америки
                for (Country c : allCountries) {
                    if ("North America".equalsIgnoreCase(c.getRegion())) {
                        countries.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}