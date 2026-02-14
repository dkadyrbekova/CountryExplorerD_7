package com.example.countryexplorerd;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardFragment extends Fragment {

    private List<Country> countryList = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isFront = true;
    private String mode, continent;

    private TextView tvCounter, tvCardContent, tvHeader, tvPercent;
    private CardView flashcard;
    private Button btnLearned;
    private CountryViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Получаем данные, переданные из CategorySelectFragment
            mode = getArguments().getString("mode", "capitals");
            continent = getArguments().getString("continent", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard, container, false);

        tvCounter = view.findViewById(R.id.tvCounter);
        tvPercent = view.findViewById(R.id.tvPercent);
        tvCardContent = view.findViewById(R.id.tvCardContent);
        tvHeader = view.findViewById(R.id.tvHeader);
        flashcard = view.findViewById(R.id.flashcard);
        btnLearned = view.findViewById(R.id.btnLearned);

        // Кнопка назад в меню континентов
        view.findViewById(R.id.btnBackToMenu).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        view.findViewById(R.id.btnNext).setOnClickListener(v -> {
            if (!countryList.isEmpty() && currentIndex < countryList.size() - 1) {
                currentIndex++;
                isFront = true;
                updateUI();
            }
        });

        view.findViewById(R.id.btnPrev).setOnClickListener(v -> {
            if (!countryList.isEmpty() && currentIndex > 0) {
                currentIndex--;
                isFront = true;
                updateUI();
            }
        });

        flashcard.setOnClickListener(v -> flipCard());

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);
        viewModel.getCountries().observe(getViewLifecycleOwner(), allCountries -> {
            if (allCountries != null && countryList.isEmpty()) {
                filterData(allCountries);
                updateUI();
                updateProgressPercent();
            }
        });

        return view;
    }

    // ИСПРАВЛЕННЫЙ МЕТОД ФИЛЬТРАЦИИ
    private void filterData(List<Country> all) {
        countryList.clear();
        for (Country c : all) {
            boolean matchesContinent = false;

            // Если континент не выбран или выбраны "Все страны"
            if (continent == null || continent.isEmpty() || "All".equalsIgnoreCase(continent)) {
                matchesContinent = true;
            }
            // Иначе проверяем совпадение региона (игнорируя регистр)
            else if (c.getRegion() != null && c.getRegion().equalsIgnoreCase(continent)) {
                matchesContinent = true;
            }

            if (matchesContinent) {
                countryList.add(c);
            }
        }
        // Перемешиваем список для обучения
        Collections.shuffle(countryList);
    }

    private void updateUI() {
        if (countryList.isEmpty()) {
            tvCardContent.setText("Стран не найдено");
            return;
        }
        Country c = countryList.get(currentIndex);
        tvCounter.setText((currentIndex + 1) + " / " + countryList.size());
        tvCardContent.setText(c.getName());
        tvCardContent.setTextSize(34);
        tvHeader.setText("СТРАНА");
        flashcard.setCardBackgroundColor(Color.WHITE);
        btnLearned.setVisibility(View.GONE);
    }

    private void flipCard() {
        if (countryList.isEmpty()) return;
        Country c = countryList.get(currentIndex);

        flashcard.animate().rotationY(90).setDuration(150).withEndAction(() -> {
            if (isFront) {
                if ("capitals".equalsIgnoreCase(mode)) {
                    tvCardContent.setText(c.getCapital());
                    tvHeader.setText("СТОЛИЦА");
                    tvCardContent.setTextSize(34);
                } else if ("flags".equalsIgnoreCase(mode)) {
                    tvCardContent.setText(c.getFlag());
                    tvHeader.setText("ФЛАГ");
                    tvCardContent.setTextSize(120);
                } else if ("currency".equalsIgnoreCase(mode)) {
                    String val = c.getCurrency();
                    tvCardContent.setText(val != null ? val : "—");
                    tvHeader.setText("ВАЛЮТА");
                    tvCardContent.setTextSize(34);
                }

                flashcard.setCardBackgroundColor(Color.parseColor("#FFFDE7"));
                btnLearned.setVisibility(View.VISIBLE);
                updateLearnedButtonDesign(c.getName());
            } else {
                updateUI();
            }
            isFront = !isFront;

            flashcard.setRotationY(-90);
            flashcard.animate().rotationY(0).setDuration(150).start();
        }).start();
    }

    private void updateLearnedButtonDesign(String countryName) {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        String key = mode + "_" + countryName;
        boolean isLearned = prefs.getBoolean(key, false);

        if (isLearned) {
            btnLearned.setText("Выучено ⭐");
            btnLearned.setTextColor(Color.parseColor("#BDC3C7"));
            btnLearned.setOnClickListener(v -> {
                prefs.edit().putBoolean(key, false).apply();
                updateLearnedButtonDesign(countryName);
                updateProgressPercent();
            });
        } else {
            btnLearned.setText("Выучить");
            btnLearned.setTextColor(Color.parseColor("#636E72"));
            btnLearned.setOnClickListener(v -> {
                prefs.edit().putBoolean(key, true).apply();
                updateLearnedButtonDesign(countryName);
                updateProgressPercent();
            });
        }
    }

    private void updateProgressPercent() {
        if (countryList.isEmpty()) return;
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        int learnedCount = 0;
        for (Country c : countryList) {
            if (prefs.getBoolean(mode + "_" + c.getName(), false)) {
                learnedCount++;
            }
        }
        int percent = (learnedCount * 100) / countryList.size();
        tvPercent.setText("Изучено: " + percent + "%");
    }
}