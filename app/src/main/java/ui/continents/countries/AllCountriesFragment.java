package ui.continents.countries; // Твой новый пакет

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider; // Для MVVM
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countryexplorerd.MainActivity;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Country; // Новая модель
import com.example.countryexplorerd.viewmodel.CountryViewModel; // Наша ViewModel
import java.util.ArrayList;
import java.util.List;

public class AllCountriesFragment extends Fragment {

    private final List<Country> fullList = new ArrayList<>();
    private final List<Country> displayList = new ArrayList<>();
    private AllAdapter adapter;
    private CountryViewModel viewModel; // "Мозг" фрагмента

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_countries, container, false);

        // Настраиваем список
        RecyclerView recyclerView = view.findViewById(R.id.rvAllCountries);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AllAdapter(displayList);
        recyclerView.setAdapter(adapter);

        // Поиск (фильтрация)
        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // MVVM В ДЕЙСТВИИ:
        // Получаем ту же ViewModel, что и в MainActivity
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Подписываемся на "живые данные"
        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null) {
                fullList.clear();
                fullList.addAll(countries);
                displayList.clear();
                displayList.addAll(countries);
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    private void filter(String text) {
        displayList.clear();
        String query = text.toLowerCase().trim();
        for (Country c : fullList) {
            if (c.getName().toLowerCase().contains(query)) {
                displayList.add(c);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // АДАПТЕР (Внутренний класс)
    private class AllAdapter extends RecyclerView.Adapter<AllAdapter.ViewHolder> {
        private final List<Country> list;
        AllAdapter(List<Country> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_country_row, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Country c = list.get(position);
            holder.name.setText(c.getName());
            holder.capital.setText(c.getCapital());
            holder.flagTxt.setText(c.getFlag());

            holder.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openDetails(c);
                }
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView flagTxt, name, capital;
            ViewHolder(View v) {
                super(v);
                flagTxt = v.findViewById(R.id.tvRowFlag);
                name = v.findViewById(R.id.tvRowName);
                capital = v.findViewById(R.id.tvRowCapital);
            }
        }
    }
}