package com.example.countryexplorerd;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class AfricaFragment extends Fragment {

    private final List<Country> countries = new ArrayList<>();
    private CountryAdapter adapter;
    private CountryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_africa, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackAfrica);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        RecyclerView recyclerView = view.findViewById(R.id.rvAfrica);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            adapter = new CountryAdapter(countries);
            recyclerView.setAdapter(adapter);
        }

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            Log.d("AfricaFragment", "Data received: " + (allCountries != null ? allCountries.size() : "null"));

            if (allCountries != null && !allCountries.isEmpty()) {
                countries.clear();
                for (Country c : allCountries) {
                    if ("Africa".equalsIgnoreCase(c.getRegion())) {
                        countries.add(c);
                    }
                }
                Log.d("AfricaFragment", "Africa countries: " + countries.size());

                if (adapter != null && recyclerView != null) {
                    recyclerView.post(() -> adapter.notifyDataSetChanged());
                }
            }
        });

        return view;
    }
}