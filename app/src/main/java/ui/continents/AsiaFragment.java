package ui.continents; // Твой новый пакет

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Для MVVM
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ui.continents.adapters.CountryAdapter;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class AsiaFragment extends Fragment {

    private final List<Country> countries = new ArrayList<>();
    private CountryAdapter adapter; // Наш универсальный адаптер
    private CountryViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asia, container, false);

        // Кнопка назад
        ImageButton btnBack = view.findViewById(R.id.btnBackAsia);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Настройка сетки (2 колонки)
        RecyclerView recyclerView = view.findViewById(R.id.rvAsia);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Создаем адаптер (код адаптера создадим чуть ниже)
        adapter = new CountryAdapter(countries);
        recyclerView.setAdapter(adapter);

        // MVVM: Подключаемся к ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Слушаем данные
        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            if (allCountries != null) {
                countries.clear();
                // Фильтруем только страны Азии
                for (Country c : allCountries) {
                    if ("Asia".equalsIgnoreCase(c.getRegion())) {
                        countries.add(c);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}