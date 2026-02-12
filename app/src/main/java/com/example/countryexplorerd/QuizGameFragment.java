package com.example.countryexplorerd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizGameFragment extends Fragment {

    private String gameType, region;
    private List<Country> filteredCountries = new ArrayList<>();
    private int lives = 3, correctCount = 0, totalWrong = 0;
    private CountDownTimer timer;
    private long timeLeftInMillis = 10000;
    private boolean isPaused = false;
    private TextView tvCountry, tvTimer, tvLives;
    private Button[] btns = new Button[4];
    private ImageButton btnPause;
    private CountryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Убедись, что этот XML файл существует!
        View view = inflater.inflate(R.layout.fragment_quiz_game, container, false);

        tvCountry = view.findViewById(R.id.tvQuizCountryName);
        tvTimer = view.findViewById(R.id.tvQuizTimer);
        tvLives = view.findViewById(R.id.tvQuizLives);
        btnPause = view.findViewById(R.id.btnQuizPause);
        btns[0] = view.findViewById(R.id.btnAnswer1);
        btns[1] = view.findViewById(R.id.btnAnswer2);
        btns[2] = view.findViewById(R.id.btnAnswer3);
        btns[3] = view.findViewById(R.id.btnAnswer4);

        view.findViewById(R.id.btnQuizBack).setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            getParentFragmentManager().popBackStack();
        });

        btnPause.setOnClickListener(v -> togglePause());

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        if (getArguments() != null) {
            gameType = getArguments().getString("game_type");
            region = getArguments().getString("region");

            viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
                if (countries != null && filteredCountries.isEmpty()) {
                    filterCountries(countries);
                    if (filteredCountries.size() < 4) {
                        Toast.makeText(getContext(), "Нужно минимум 4 страны!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        nextQuestion();
                    }
                }
            });
        }

        return view;
    }

    private void filterCountries(List<Country> allCountries) {
        for (Country c : allCountries) {
            if ("All".equals(region) || (c.getRegion() != null && c.getRegion().equalsIgnoreCase(region))) {
                filteredCountries.add(c);
            }
        }
    }

    private void nextQuestion() {
        if (lives <= 0) { showResultDialog(); return; }

        isPaused = false;
        timeLeftInMillis = 10000;
        startTimer(timeLeftInMillis);

        Country correct = filteredCountries.get(new Random().nextInt(filteredCountries.size()));
        String question = "", answer = "";

        // Логика типов игры
        if ("Capitals".equals(gameType)) {
            question = "Столица страны:\n" + correct.getName();
            answer = correct.getCapital();
        } else if ("Flags".equals(gameType)) {
            question = "Чей это флаг?\n" + correct.getFlag();
            answer = correct.getName();
        } else {
            question = "Валюта страны:\n" + correct.getName();
            answer = correct.getCurrency();
        }

        tvCountry.setText(question);
        final String finalAnswer = (answer != null) ? answer : "---";
        List<String> options = generateOptions(finalAnswer);
        Collections.shuffle(options);

        for (int i = 0; i < 4; i++) {
            btns[i].setText(options.get(i));
            btns[i].setEnabled(true);
            btns[i].setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            final String sel = options.get(i);
            final Button currentBtn = btns[i];

            btns[i].setOnClickListener(v -> {
                if (isPaused) return;
                boolean isCorrect = sel.equals(finalAnswer);
                if (isCorrect) {
                    for (Button b : btns) b.setEnabled(false);
                    if (timer != null) timer.cancel();
                }

                animateButton(currentBtn, isCorrect, () -> {
                    if (isCorrect) {
                        correctCount++;
                        nextQuestion();
                    } else {
                        totalWrong++;
                        handleWrongAnswer(currentBtn);
                    }
                });
            });
        }
    }

    private void togglePause() {
        if (!isPaused) {
            isPaused = true;
            if (timer != null) timer.cancel();
            btnPause.setImageResource(android.R.drawable.ic_media_play);
        } else {
            isPaused = false;
            startTimer(timeLeftInMillis);
            btnPause.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void startTimer(long duration) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(duration, 1000) {
            public void onTick(long m) {
                timeLeftInMillis = m;
                tvTimer.setText("⏱ " + m / 1000);
            }
            public void onFinish() {
                if (!isPaused) { lives--; updateLivesUI(); nextQuestion(); }
            }
        }.start();
    }

    private void handleWrongAnswer(Button wrongBtn) {
        lives--;
        updateLivesUI();
        if (lives <= 0) showResultDialog();
        else {
            wrongBtn.setEnabled(false);
            wrongBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DCDDE1")));
        }
    }

    private void animateButton(Button button, boolean isCorrect, Runnable onEnd) {
        int color = isCorrect ? Color.parseColor("#2ECC71") : Color.parseColor("#E74C3C");
        button.setBackgroundTintList(ColorStateList.valueOf(color));

        // Убедись, что файлы zoom_in.xml и shake.xml созданы!
        try {
            Animation anim = AnimationUtils.loadAnimation(getContext(), isCorrect ? R.anim.zoom_in : R.anim.shake);
            button.startAnimation(anim);
        } catch (Exception e) {
            // Если анимации нет, просто идем дальше
        }

        if (!isCorrect) {
            Vibrator v = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) v.vibrate(VibrationEffect.createOneShot(200, 255));
        }
        new Handler().postDelayed(onEnd, 600);
    }

    private List<String> generateOptions(String correct) {
        List<String> options = new ArrayList<>();
        options.add(correct);
        while (options.size() < 4) {
            Country r = filteredCountries.get(new Random().nextInt(filteredCountries.size()));
            String cand = "Capitals".equals(gameType) ? r.getCapital() : r.getName();
            if (cand != null && !options.contains(cand)) options.add(cand);
        }
        return options;
    }

    private void updateLivesUI() {
        StringBuilder h = new StringBuilder();
        for (int i = 0; i < lives; i++) h.append("❤️");
        tvLives.setText(h.toString());
    }

    private void showResultDialog() {
        if (timer != null) timer.cancel();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View v = getLayoutInflater().inflate(R.layout.dialog_quiz_result, null);
        builder.setView(v);
        builder.setCancelable(false);
        AlertDialog d = builder.create();

        ((TextView)v.findViewById(R.id.tvResultCorrect)).setText("Правильно: " + correctCount);
        ((TextView)v.findViewById(R.id.tvResultWrong)).setText("Ошибок: " + totalWrong);

        v.findViewById(R.id.btnRetry).setOnClickListener(view -> {
            d.dismiss(); lives = 3; correctCount = 0; totalWrong = 0; updateLivesUI(); nextQuestion();
        });
        v.findViewById(R.id.btnToMenu).setOnClickListener(view -> {
            d.dismiss(); getParentFragmentManager().popBackStack();
        });
        d.show();
    }

    @Override
    public void onDestroy() { super.onDestroy(); if (timer != null) timer.cancel(); }
}