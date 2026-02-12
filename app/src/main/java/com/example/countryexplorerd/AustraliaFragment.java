package com.example.countryexplorerd; // Твой новый пакет

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Для связи с данными
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class AustraliaFragment extends Fragment {

    private final List<Country> countries = new ArrayList<>();
    private CountryAdapter adapter;
    private CountryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Убедись, что fragment_australia.xml лежит в res/layout
        View view = inflater.inflate(R.layout.fragment_australia, container, false);

        // Кнопка назад
        ImageButton btnBack = view.findViewById(R.id.btnBackAus);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Настройка списка стран
        RecyclerView recyclerView = view.findViewById(R.id.rvAustralia);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Наш новый универсальный адаптер
        adapter = new CountryAdapter(countries);
        recyclerView.setAdapter(adapter);

        // MVVM: Получаем доступ к "мозгам" приложения
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Следим за изменениями (когда Postman отдаст данные)
        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            if (allCountries != null) {
                countries.clear();
                // Фильтруем по региону Oceania
                for (Country c : allCountries) {
                    if ("Oceania".equalsIgnoreCase(c.getRegion())) {
                        countries.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}