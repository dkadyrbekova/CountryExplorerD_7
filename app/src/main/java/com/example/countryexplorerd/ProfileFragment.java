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
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private CountryViewModel viewModel;
    private TextView tvStatsCapitals, tvStatsFlags, tvStatsCurrency;  // ← УБРАЛ tvStatsCountries
    private RecyclerView rvFavorites, rvNotes;
    private TextView tvEmptyFavorites, tvEmptyNotes, tvFavCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Инициализация элементов (БЕЗ tvStatsCountries)
        tvStatsCapitals = view.findViewById(R.id.tvStatsCapitals);
        tvStatsFlags = view.findViewById(R.id.tvStatsFlags);
        tvStatsCurrency = view.findViewById(R.id.tvStatsCurrency);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        rvNotes = view.findViewById(R.id.rvNotes);

        tvEmptyFavorites = view.findViewById(R.id.tvEmptyFavorites);
        tvEmptyNotes = view.findViewById(R.id.tvEmptyNotes);
        tvFavCount = view.findViewById(R.id.tvFavCount);

        // Настройка списков
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));

        // MVVM: Получаем ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Загружаем данные
        loadStatistics();
        loadFavorites();
        loadNotes();

        // Обработчик перехода в настройки
        LinearLayout btnSettings = view.findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                SettingsFragment settingsFragment = new SettingsFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, settingsFragment)
                        .addToBackStack(null)
                        .commit();
            });
        }

        return view;
    }

    private void loadStatistics() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);

        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null) {
                int totalCountries = countries.size();

                int learnedCapitals = 0;
                int learnedFlags = 0;
                int learnedCurrency = 0;

                for (Country c : countries) {
                    if (prefs.getBoolean("capitals_" + c.getName(), false)) learnedCapitals++;
                    if (prefs.getBoolean("flags_" + c.getName(), false)) learnedFlags++;
                    if (prefs.getBoolean("currency_" + c.getName(), false)) learnedCurrency++;
                }

                // ✅ ПОКАЗЫВАЕМ ПРОЦЕНТЫ
                int percentCapitals = (totalCountries > 0) ? (learnedCapitals * 100) / totalCountries : 0;
                int percentFlags = (totalCountries > 0) ? (learnedFlags * 100) / totalCountries : 0;
                int percentCurrency = (totalCountries > 0) ? (learnedCurrency * 100) / totalCountries : 0;

                // БЕЗ tvStatsCountries
                tvStatsCapitals.setText(percentCapitals + "%");
                tvStatsFlags.setText(percentFlags + "%");
                tvStatsCurrency.setText(percentCurrency + "%");
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

                viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
                    if (countries != null) {
                        List<Country> favCountries = new ArrayList<>();
                        for (FavoriteCountry fav : favorites) {
                            for (Country c : countries) {
                                if (c.getName().equals(fav.getCountryName())) {
                                    favCountries.add(c);
                                    break;
                                }
                            }
                        }

                        FavoritesAdapter adapter = new FavoritesAdapter(favCountries);
                        rvFavorites.setAdapter(adapter);
                    }
                });
            }
        });
    }

    private void loadNotes() {
        viewModel.loadNotes();

        viewModel.getNotes().observe(getViewLifecycleOwner(), notes -> {
            if (notes == null || notes.isEmpty()) {
                tvEmptyNotes.setVisibility(View.VISIBLE);
                rvNotes.setVisibility(View.GONE);
            } else {
                tvEmptyNotes.setVisibility(View.GONE);
                rvNotes.setVisibility(View.VISIBLE);

                NotesAdapter adapter = new NotesAdapter(notes);
                rvNotes.setAdapter(adapter);
            }
        });
    }

    // Адаптер для избранных
    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private List<Country> countries;

        FavoritesAdapter(List<Country> countries) {
            this.countries = countries;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_favorite_mini, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Country country = countries.get(position);
            holder.flag.setText(country.getFlag());
            holder.name.setText(country.getName());

            holder.itemView.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openDetails(country);
                }
            });
        }

        @Override
        public int getItemCount() {
            return countries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView flag, name;

            ViewHolder(View v) {
                super(v);
                flag = v.findViewById(R.id.tvFavFlag);
                name = v.findViewById(R.id.tvFavName);
            }
        }
    }

    // Адаптер для заметок
    private class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
        private List<CountryNote> notes;

        NotesAdapter(List<CountryNote> notes) {
            this.notes = notes;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_note, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CountryNote note = notes.get(position);
            holder.countryName.setText(note.getCountryName());
            holder.noteText.setText(note.getNoteText());

            holder.itemView.setOnClickListener(v -> {
                viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
                    if (countries != null) {
                        for (Country c : countries) {
                            if (c.getName().equals(note.getCountryName())) {
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).openDetails(c);
                                }
                                break;
                            }
                        }
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return notes.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView countryName, noteText;

            ViewHolder(View v) {
                super(v);
                countryName = v.findViewById(R.id.tvNoteCountry);
                noteText = v.findViewById(R.id.tvNoteText);
            }
        }
    }
}