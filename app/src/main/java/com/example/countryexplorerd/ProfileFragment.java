package com.example.countryexplorerd;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryNote;
// УДАЛИЛА импорт .models.FavoriteCountry, так как он теперь в основном пакете
import com.example.countryexplorerd.viewmodel.CountryViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private CountryViewModel viewModel;
    private TextView tvStatsCountries, tvStatsCapitals, tvStatsFlags, tvStatsCurrency;
    private RecyclerView rvFavorites, rvNotes;
    private TextView tvEmptyFavorites, tvEmptyNotes, tvFavCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Используем твой XML файл
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Связываем UI (IDs из твоего XML)
        tvStatsCountries = view.findViewById(R.id.tvStatsCountries);
        tvStatsCapitals = view.findViewById(R.id.tvStatsCapitals);
        tvStatsFlags = view.findViewById(R.id.tvStatsFlags);
        tvStatsCurrency = view.findViewById(R.id.tvStatsCurrency);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        rvNotes = view.findViewById(R.id.rvNotes); // Убедись, что добавила этот ID в XML, если его там нет

        tvEmptyFavorites = view.findViewById(R.id.tvEmptyFavorites);
        tvFavCount = view.findViewById(R.id.tvFavCount);

        // 2. Настройка списков
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (rvNotes != null) {
            rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        // 3. MVVM
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // 4. Загрузка данных
        loadStatistics();
        loadFavorites();
        loadNotes();

        // 5. Обработчик настроек
        LinearLayout btnSettings = view.findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            });
        }

        return view;
    }

    private void loadStatistics() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);

        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null && !countries.isEmpty()) {
                int total = countries.size();
                int caps = 0, flags = 0, curs = 0;

                for (Country c : countries) {
                    if (prefs.getBoolean("capitals_" + c.getName(), false)) caps++;
                    if (prefs.getBoolean("flags_" + c.getName(), false)) flags++;
                    if (prefs.getBoolean("currency_" + c.getName(), false)) curs++;
                }

                tvStatsCountries.setText(total + " стран");
                tvStatsCapitals.setText(caps + "/" + total);
                tvStatsFlags.setText(flags + "/" + total);
                tvStatsCurrency.setText(curs + "/" + total);
            }
        });
    }

    private void loadFavorites() {
        viewModel.loadFavorites();
        viewModel.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites == null || favorites.isEmpty()) {
                tvEmptyFavorites.setVisibility(View.VISIBLE);
                rvFavorites.setVisibility(View.GONE);
                tvFavCount.setText("(0)");
            } else {
                tvEmptyFavorites.setVisibility(View.GONE);
                rvFavorites.setVisibility(View.VISIBLE);
                tvFavCount.setText("(" + favorites.size() + ")");

                viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
                    if (allCountries != null) {
                        List<Country> favList = new ArrayList<>();
                        for (FavoriteCountry f : favorites) {
                            for (Country c : allCountries) {
                                if (c.getName().equals(f.getCountryName())) {
                                    favList.add(c);
                                    break;
                                }
                            }
                        }
                        rvFavorites.setAdapter(new FavoritesAdapter(favList));
                    }
                });
            }
        });
    }

    private void loadNotes() {
        viewModel.loadNotes();
        viewModel.getNotes().observe(getViewLifecycleOwner(), notes -> {
            if (rvNotes == null) return;

            if (notes == null || notes.isEmpty()) {
                rvNotes.setVisibility(View.GONE);
            } else {
                rvNotes.setVisibility(View.VISIBLE);
                rvNotes.setAdapter(new NotesAdapter(notes));
            }
        });
    }

    // --- АДАПТЕРЫ (Внутренние классы) ---

    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private final List<Country> list;
        FavoritesAdapter(List<Country> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_favorite_mini, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            Country c = list.get(pos);
            h.flag.setText(c.getFlag());
            h.name.setText(c.getName());
            h.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).openDetails(c);
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView flag, name;
            ViewHolder(View v) { super(v); flag = v.findViewById(R.id.tvFavFlag); name = v.findViewById(R.id.tvFavName); }
        }
    }

    private class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
        private final List<CountryNote> notes;
        NotesAdapter(List<CountryNote> notes) { this.notes = notes; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_note, p, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            CountryNote n = notes.get(pos);
            h.country.setText(n.getCountryName());
            h.text.setText(n.getNoteText());
        }

        @Override
        public int getItemCount() { return notes.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView country, text;
            ViewHolder(View v) { super(v); country = v.findViewById(R.id.tvNoteCountry); text = v.findViewById(R.id.tvNoteText); }
        }
    }
}