package com.example.countryexplorerd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class AfricaFragment extends Fragment {

    private RecyclerView rvAfrica;
    private CountryAdapter adapter;
    private CountryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_africa, container, false);

        // Кнопка Назад (ID должен быть в fragment_africa.xml)
        ImageButton btnBack = view.findViewById(R.id.btnBackAfrica);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Настройка списка
        rvAfrica = view.findViewById(R.id.rvAfrica);
        if (rvAfrica != null) {
            rvAfrica.setLayoutManager(new LinearLayoutManager(getContext()));

            // ПРАВИЛЬНО: Передаем только список, без getContext()
            List<Country> emptyList = new ArrayList<>();
            adapter = new CountryAdapter(emptyList);
            rvAfrica.setAdapter(adapter);
        }

        // Загрузка данных
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);
        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null && adapter != null) {
                List<Country> filteredList = new ArrayList<>();
                for (Country c : countries) {
                    if ("Africa".equalsIgnoreCase(c.getRegion())) {
                        filteredList.add(c);
                    }
                }
                adapter.updateData(filteredList);
            }
        });

        return view;
    }
}