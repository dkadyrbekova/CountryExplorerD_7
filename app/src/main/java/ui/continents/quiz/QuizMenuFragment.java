package ui.continents.quiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.countryexplorerd.R;
import com.example.countryexplorerd.viewmodel.CountryViewModel;

public class QuizMenuFragment extends Fragment {

    private CountryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_menu, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        setupCard(view, R.id.cardQuizCapitals, "Capitals");
        setupCard(view, R.id.cardQuizFlags, "Flags");
        setupCard(view, R.id.cardQuizCurrency, "Currencies");

        CardView cardFavorites = view.findViewById(R.id.cardQuizFavorites);
        if (cardFavorites != null) {
            cardFavorites.setOnClickListener(v -> launchFavoriteQuiz());
        }

        // 5-й режим — Угадай регион
        CardView cardRegion = view.findViewById(R.id.cardQuizRegion);
        if (cardRegion != null) {
            cardRegion.setOnClickListener(v -> launchRegionQuiz());
        }

        return view;
    }

    private void setupCard(View root, int viewId, String gameType) {
        CardView card = root.findViewById(viewId);
        if (card != null) {
            card.setOnClickListener(v -> openRegionSelect(gameType));
        }
    }

    private void openRegionSelect(String gameType) {
        QuizRegionSelectFragment fragment = new QuizRegionSelectFragment();
        Bundle args = new Bundle();
        args.putString("game_type", gameType);
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void launchFavoriteQuiz() {
        viewModel.loadFavorites();
        viewModel.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites == null || favorites.isEmpty()) {
                Toast.makeText(getContext(), "Добавьте страны в избранное!", Toast.LENGTH_SHORT).show();
            } else {
                QuizGameFragment fragment = new QuizGameFragment();
                Bundle args = new Bundle();
                args.putString("game_type", "Flags");
                args.putString("region", "Favorites");
                fragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void launchRegionQuiz() {
        RegionQuizFragment fragment = new RegionQuizFragment();
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}