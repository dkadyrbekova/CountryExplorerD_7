package com.example.countryexplorerd;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegionQuizFragment extends Fragment {

    // Все 6 континентов с эмодзи — показываются как варианты ответа
    private static final String[] REGIONS = {
            "Europe", "Asia", "Africa", "North America", "South America", "Oceania"
    };
    private static final String[] REGIONS_RU = {
            "Европа 🇪🇺", "Азия 🌏", "Африка 🌍", "Северная Америка 🇨🇦", "Южная Америка 🇧🇷", "Океания 🇦🇺"
    };

    private TextView tvFlag, tvCountryName, tvScore, tvLives, tvCounter, tvResult;
    private Button[] btnRegions = new Button[6];

    private List<Country> countryList = new ArrayList<>();
    private Country currentCountry;
    private int score = 0;
    private int lives = 3;
    private int questionNumber = 0;
    private static final int MAX_LIVES = 3;

    private CountryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_region_quiz, container, false);

        tvFlag        = view.findViewById(R.id.tvRegionFlag);
        tvCountryName = view.findViewById(R.id.tvRegionCountryName);
        tvScore       = view.findViewById(R.id.tvRegionScore);
        tvLives       = view.findViewById(R.id.tvRegionLives);
        tvCounter     = view.findViewById(R.id.tvRegionCounter);
        tvResult      = view.findViewById(R.id.tvRegionResult);

        btnRegions[0] = view.findViewById(R.id.btnRegion1);
        btnRegions[1] = view.findViewById(R.id.btnRegion2);
        btnRegions[2] = view.findViewById(R.id.btnRegion3);
        btnRegions[3] = view.findViewById(R.id.btnRegion4);
        btnRegions[4] = view.findViewById(R.id.btnRegion5);
        btnRegions[5] = view.findViewById(R.id.btnRegion6);

        view.findViewById(R.id.btnRegionBack).setOnClickListener(v ->
                getParentFragmentManager().popBackStack());

        // Назначаем текст кнопок — всегда одни и те же 6 континентов
        for (int i = 0; i < 6; i++) {
            btnRegions[i].setText(REGIONS_RU[i]);
            final int idx = i;
            btnRegions[i].setOnClickListener(v -> onRegionSelected(idx));
        }

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);
        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null && countryList.isEmpty()) {
                // Берём только страны у которых есть регион из нашего списка
                for (Country c : countries) {
                    if (c.getRegion() != null && isKnownRegion(c.getRegion())) {
                        countryList.add(c);
                    }
                }
                Collections.shuffle(countryList);
                nextQuestion();
            }
        });

        return view;
    }

    private boolean isKnownRegion(String region) {
        for (String r : REGIONS) {
            if (r.equalsIgnoreCase(region)) return true;
        }
        return false;
    }

    // ─── ВОПРОС ──────────────────────────────────────────────────────────────

    private void nextQuestion() {
        if (lives <= 0) {
            showResult();
            return;
        }

        questionNumber++;
        tvResult.setVisibility(View.INVISIBLE);

        int index = (questionNumber - 1) % countryList.size();
        if (index == 0 && questionNumber > 1) Collections.shuffle(countryList);

        currentCountry = countryList.get(index);

        tvFlag.setText(currentCountry.getFlag());
        tvCountryName.setText(currentCountry.getName());
        tvCounter.setText("Вопрос " + questionNumber);

        updateLivesUI();
        updateScoreUI();
        resetButtons();
    }

    private void onRegionSelected(int selectedIndex) {
        String selected = REGIONS[selectedIndex];
        String correct  = currentCountry.getRegion();
        boolean isCorrect = selected.equalsIgnoreCase(correct);

        // Блокируем все кнопки
        for (Button b : btnRegions) b.setEnabled(false);

        if (isCorrect) {
            // ✅ Правильно — зелёная
            btnRegions[selectedIndex].setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#2ECC71")));
            score++;
            tvResult.setText("✅ Правильно!");
            tvResult.setTextColor(Color.parseColor("#2ECC71"));
            tvResult.setVisibility(View.VISIBLE);
            updateScoreUI();
        } else {
            // ❌ Неправильно — красная нажатая, зелёная правильная
            btnRegions[selectedIndex].setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#E74C3C")));
            highlightCorrect(correct);

            String correctRu = getRegionRu(correct);
            tvResult.setText("❌ Правильный ответ: " + correctRu);
            tvResult.setTextColor(Color.parseColor("#E74C3C"));
            tvResult.setVisibility(View.VISIBLE);

            lives--;
            updateLivesUI();

            Vibrator vib = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vib != null) vib.vibrate(VibrationEffect.createOneShot(200, 255));
        }

        // Переходим к следующему вопросу через 1.2 сек
        new Handler().postDelayed(() -> {
            if (!isAdded()) return;
            if (lives <= 0) {
                showResult();
            } else {
                nextQuestion();
            }
        }, 1200);
    }

    private void highlightCorrect(String correctRegion) {
        for (int i = 0; i < REGIONS.length; i++) {
            if (REGIONS[i].equalsIgnoreCase(correctRegion)) {
                btnRegions[i].setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#2ECC71")));
                break;
            }
        }
    }

    private String getRegionRu(String region) {
        for (int i = 0; i < REGIONS.length; i++) {
            if (REGIONS[i].equalsIgnoreCase(region)) return REGIONS_RU[i];
        }
        return region;
    }

    // ─── UI ──────────────────────────────────────────────────────────────────

    private void resetButtons() {
        for (Button b : btnRegions) {
            b.setEnabled(true);
            b.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6366F1")));
            b.setTextColor(Color.WHITE);
        }
    }

    private void updateLivesUI() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lives; i++) sb.append("❤️");
        for (int i = lives; i < MAX_LIVES; i++) sb.append("🖤");
        tvLives.setText(sb.toString());
    }

    private void updateScoreUI() {
        tvScore.setText("⭐ " + score);
    }

    private void showResult() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("🌍 Игра окончена!")
                .setMessage("Правильных ответов: " + score + "\nВопросов сыграно: " + questionNumber)
                .setCancelable(false)
                .setPositiveButton("Играть снова", (d, w) -> {
                    score = 0;
                    lives = MAX_LIVES;
                    questionNumber = 0;
                    Collections.shuffle(countryList);
                    nextQuestion();
                })
                .setNegativeButton("В меню", (d, w) ->
                        getParentFragmentManager().popBackStack())
                .show();
    }
}