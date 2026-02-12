package com.example.countryexplorerd; // Твой новый пакет

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QuizMenuFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Убедись, что файл fragment_quiz_menu.xml перенесен в res/layout
        View view = inflater.inflate(R.layout.fragment_quiz_menu, container, false);

        // Привязываем клики к карточкам (проверь ID в твоем XML)
        setupCard(view, R.id.cardQuizCapitals, "Capitals");
        setupCard(view, R.id.cardQuizFlags, "Flags");
        setupCard(view, R.id.cardQuizCurrency, "Currencies");

        // Логика для Избранного (запуск викторины по флагам только из избранных стран)
        View favCard = view.findViewById(R.id.cardQuizFavorites);
        if (favCard != null) {
            favCard.setOnClickListener(v -> launchFavoriteQuiz());
        }

        return view;
    }

    private void setupCard(View root, int viewId, String gameType) {
        View card = root.findViewById(viewId);
        if (card != null) {
            card.setOnClickListener(v -> openRegionSelect(gameType));
        }
    }

    private void openRegionSelect(String gameType) {
        // Здесь мы переходим к выбору региона (Европа, Азия и т.д.)
        QuizRegionSelectFragment fragment = new QuizRegionSelectFragment();
        Bundle args = new Bundle();
        args.putString("game_type", gameType);
        fragment.setArguments(args);

        switchFragment(fragment);
    }

    private void launchFavoriteQuiz() {
        // Запуск игры напрямую для избранных стран
        QuizGameFragment fragment = new QuizGameFragment();
        Bundle args = new Bundle();
        args.putString("game_type", "Flags"); // По умолчанию для избранного ставим Флаги
        args.putString("region", "Favorites");
        fragment.setArguments(args);

        switchFragment(fragment);
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}