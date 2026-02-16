package com.example.countryexplorerd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class QuizRegionSelectFragment extends Fragment {

    private String gameType;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_region_select, container, false);

        if (getArguments() != null) {
            gameType = getArguments().getString("game_type");
        }

        // Кнопка назад
        ImageButton btnBack = view.findViewById(R.id.btnRegionBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Настройка кликов по регионам
        setupClick(view, R.id.quizEurope, "Europe");
        setupClick(view, R.id.quizAsia, "Asia");
        setupClick(view, R.id.quizAfrica, "Africa");
        setupClick(view, R.id.quizNAmerica, "North America");
        setupClick(view, R.id.quizSAmerica, "South America");
        setupClick(view, R.id.quizAustralia, "Oceania");
        setupClick(view, R.id.quizAllWorld, "All");

        return view;
    }

    private void setupClick(View root, int viewId, String region) {
        CardView card = root.findViewById(viewId);
        if (card != null) {
            card.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("game_type", gameType);
                args.putString("region", region);

                QuizGameFragment fragment = new QuizGameFragment();
                fragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }
}