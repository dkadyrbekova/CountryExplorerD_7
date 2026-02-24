package ui.continents;

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

import ui.continents.adapters.CountryAdapter;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class AfricaFragment extends Fragment {

    private final List<Country> countries = new ArrayList<>();
    private CountryAdapter adapter;
    private CountryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_africa, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackAfrica);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        RecyclerView recyclerView = view.findViewById(R.id.rvAfrica);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new CountryAdapter(countries);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            if (allCountries != null) {
                countries.clear();
                for (Country c : allCountries) {
                    if ("Africa".equalsIgnoreCase(c.getRegion())) {
                        countries.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}