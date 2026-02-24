package ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.countryexplorerd.MainActivity;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Country;
import database.CountryNote;
import database.VisitedCountry;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.List;

import database.FavoriteCountry;

public class ProfileFragment extends Fragment {

    private CountryViewModel viewModel;
    private TextView tvStatsCountries, tvStatsCapitals, tvStatsFlags, tvStatsCurrency;
    private RecyclerView rvFavorites, rvNotes, rvVisited;
    private TextView tvEmptyFavorites, tvEmptyNotes, tvFavCount;
    private TextView tvEmptyVisited, tvVisitedCount, tvVisitedCountries, tvVisitedContinents, tvVisitedPercent;
    private EditText etUserName;

    // Всего стран в мире для подсчёта %
    private static final int TOTAL_WORLD_COUNTRIES = 195;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        etUserName = view.findViewById(R.id.etUserName);
        tvStatsCountries = view.findViewById(R.id.tvStatsCountries);
        tvStatsCapitals = view.findViewById(R.id.tvStatsCapitals);
        tvStatsFlags = view.findViewById(R.id.tvStatsFlags);
        tvStatsCurrency = view.findViewById(R.id.tvStatsCurrency);

        rvFavorites = view.findViewById(R.id.rvFavorites);
        rvNotes = view.findViewById(R.id.rvNotes);
        rvVisited = view.findViewById(R.id.rvVisited);

        tvEmptyFavorites = view.findViewById(R.id.tvEmptyFavorites);
        tvEmptyNotes = view.findViewById(R.id.tvEmptyNotes);
        tvFavCount = view.findViewById(R.id.tvFavCount);

        tvEmptyVisited = view.findViewById(R.id.tvEmptyVisited);
        tvVisitedCount = view.findViewById(R.id.tvVisitedCount);
        tvVisitedCountries = view.findViewById(R.id.tvVisitedCountries);
        tvVisitedContinents = view.findViewById(R.id.tvVisitedContinents);
        tvVisitedPercent = view.findViewById(R.id.tvVisitedPercent);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVisited.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        loadUserName();
        loadStatistics();
        loadFavorites();
        loadNotes();
        loadVisited();

        etUserName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences prefs = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
                prefs.edit().putString("user_name", s.toString().trim()).apply();
            }
        });

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

    private void loadUserName() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");
        etUserName.setText(userName);
    }

    private void loadStatistics() {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);

        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null) {
                int totalCountries = countries.size();
                int learnedCapitals = 0, learnedFlags = 0, learnedCurrency = 0;

                for (Country c : countries) {
                    if (prefs.getBoolean("capitals_" + c.getName(), false)) learnedCapitals++;
                    if (prefs.getBoolean("flags_" + c.getName(), false)) learnedFlags++;
                    if (prefs.getBoolean("currency_" + c.getName(), false)) learnedCurrency++;
                }

                int percentCapitals = (totalCountries > 0) ? (learnedCapitals * 100) / totalCountries : 0;
                int percentFlags = (totalCountries > 0) ? (learnedFlags * 100) / totalCountries : 0;
                int percentCurrency = (totalCountries > 0) ? (learnedCurrency * 100) / totalCountries : 0;
                int averagePercent = (percentCapitals + percentFlags + percentCurrency) / 3;

                tvStatsCountries.setText(averagePercent + "%");
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
                        rvFavorites.setAdapter(new FavoritesAdapter(favCountries));
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
                rvNotes.setAdapter(new NotesAdapter(notes));
            }
        });
    }

    // НОВОЕ: загрузка посещённых стран
    private void loadVisited() {
        viewModel.getVisited().observe(getViewLifecycleOwner(), visited -> {
            if (visited == null || visited.isEmpty()) {
                tvEmptyVisited.setVisibility(View.VISIBLE);
                rvVisited.setVisibility(View.GONE);
                tvVisitedCount.setText("(0)");
                tvVisitedCountries.setText("0");
                tvVisitedPercent.setText("0%");
            } else {
                tvEmptyVisited.setVisibility(View.GONE);
                rvVisited.setVisibility(View.VISIBLE);
                tvVisitedCount.setText("(" + visited.size() + ")");
                tvVisitedCountries.setText(String.valueOf(visited.size()));

                int percent = (visited.size() * 100) / TOTAL_WORLD_COUNTRIES;
                tvVisitedPercent.setText(percent + "%");

                rvVisited.setAdapter(new VisitedAdapter(visited));
            }
        });

        // Количество континентов
        viewModel.getVisitedContinentsCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvVisitedContinents.setText(String.valueOf(count));
            }
        });
    }

    // Адаптер для посещённых стран
    private class VisitedAdapter extends RecyclerView.Adapter<VisitedAdapter.ViewHolder> {
        private List<VisitedCountry> visited;

        VisitedAdapter(List<VisitedCountry> visited) {
            this.visited = visited;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_visited_mini, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VisitedCountry visited = this.visited.get(position);
            holder.name.setText(visited.getCountryName());

            // Находим страну чтобы показать флаг
            viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
                if (countries != null) {
                    for (Country c : countries) {
                        if (c.getName().equals(visited.getCountryName())) {
                            holder.flag.setText(c.getFlag());
                            break;
                        }
                    }
                }
            });

            // Долгий тап — удалить из посещённых
            holder.itemView.setOnLongClickListener(v -> {
                viewModel.removeVisited(visited.getCountryName());
                return true;
            });

            // Тап — открыть страну
            holder.itemView.setOnClickListener(v -> {
                viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
                    if (countries != null) {
                        for (Country c : countries) {
                            if (c.getName().equals(visited.getCountryName())) {
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
        public int getItemCount() { return visited.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView flag, name;
            ViewHolder(View v) {
                super(v);
                flag = v.findViewById(R.id.tvVisitedFlag);
                name = v.findViewById(R.id.tvVisitedName);
            }
        }
    }

    // Адаптер для избранных
    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private List<Country> countries;

        FavoritesAdapter(List<Country> countries) { this.countries = countries; }

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
        public int getItemCount() { return countries.size(); }

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

        NotesAdapter(List<CountryNote> notes) { this.notes = notes; }

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
        public int getItemCount() { return notes.size(); }

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