package com.example.countryexplorerd;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegionQuizFragment extends Fragment {

    private static final String[] REGIONS = {
            "Europe", "Asia", "Africa", "North America", "South America", "Oceania"
    };
    private static final String[] REGIONS_RU = {
            "Европа 🇪🇺", "Азия 🌏", "Африка 🌍", "Сев. Америка 🇨🇦", "Юж. Америка 🇧🇷", "Океания 🇦🇺"
    };

    private TextView tvFlag, tvCountryName, tvScore, tvLives, tvCounter, tvTimer;
    private Button[] btnRegions = new Button[6];
    private ImageButton btnBack, btnPause;
    private ProgressBar progressBar;

    private List<Country> countryList = new ArrayList<>();
    private Country currentCountry;
    private int score = 0;
    private int lives = 3;
    private int questionNumber = 0;
    private int maxStreak = 0;
    private int streak = 0;
    private static final int MAX_LIVES = 3;

    private CountDownTimer timer;
    private long timeLeftInMillis = 15000;
    private boolean isPaused = false;
    private String currentCorrectRegion = "";

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
        tvTimer       = view.findViewById(R.id.tvRegionTimer);
        progressBar   = view.findViewById(R.id.progressBarRegion);
        btnBack       = view.findViewById(R.id.btnRegionBack);
        btnPause      = view.findViewById(R.id.btnRegionPause);

        btnRegions[0] = view.findViewById(R.id.btnRegion1);
        btnRegions[1] = view.findViewById(R.id.btnRegion2);
        btnRegions[2] = view.findViewById(R.id.btnRegion3);
        btnRegions[3] = view.findViewById(R.id.btnRegion4);
        btnRegions[4] = view.findViewById(R.id.btnRegion5);
        btnRegions[5] = view.findViewById(R.id.btnRegion6);

        btnBack.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            getParentFragmentManager().popBackStack();
        });

        btnPause.setOnClickListener(v -> togglePause());

        for (int i = 0; i < 6; i++) {
            final int idx = i;
            btnRegions[i].setOnClickListener(v -> onRegionSelected(idx));
        }

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);
        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null && countryList.isEmpty()) {
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

        isPaused = false;
        questionNumber++;
        timeLeftInMillis = 15000;

        int index = (questionNumber - 1) % countryList.size();
        if (index == 0 && questionNumber > 1) Collections.shuffle(countryList);

        currentCountry = countryList.get(index);
        currentCorrectRegion = currentCountry.getRegion();

        tvFlag.setText(currentCountry.getFlag());
        tvCountryName.setText(currentCountry.getName());
        tvCounter.setText(questionNumber + " / ∞");

        progressBar.setMax(MAX_LIVES);
        progressBar.setProgress(lives);

        updateLivesUI();
        updateScoreUI();
        resetButtons();
        startTimer(timeLeftInMillis);
    }

    // ─── ОТВЕТ ───────────────────────────────────────────────────────────────

    private void onRegionSelected(int selectedIndex) {
        if (isPaused) return;
        if (timer != null) timer.cancel();

        String selected = REGIONS[selectedIndex];
        boolean isCorrect = selected.equalsIgnoreCase(currentCorrectRegion);

        for (Button b : btnRegions) b.setEnabled(false);

        if (isCorrect) {
            btnRegions[selectedIndex].setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#2ECC71")));
            btnRegions[selectedIndex].setTextColor(Color.WHITE);
            score++;
            streak++;
            if (streak > maxStreak) maxStreak = streak;
            updateScoreUI();
            new Handler().postDelayed(this::nextQuestion, 800);
        } else {
            btnRegions[selectedIndex].setBackgroundTintList(
                    ColorStateList.valueOf(Color.parseColor("#E74C3C")));
            btnRegions[selectedIndex].setTextColor(Color.WHITE);
            highlightCorrect(currentCorrectRegion);

            streak = 0;
            Vibrator vib = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
            if (vib != null) vib.vibrate(VibrationEffect.createOneShot(200, 255));

            lives--;
            updateLivesUI();
            progressBar.setProgress(lives);

            if (lives <= 0) {
                new Handler().postDelayed(this::showResult, 1200);
            } else {
                new Handler().postDelayed(this::nextQuestion, 1200);
            }
        }
    }

    private void highlightCorrect(String correctRegion) {
        for (int i = 0; i < REGIONS.length; i++) {
            if (REGIONS[i].equalsIgnoreCase(correctRegion)) {
                btnRegions[i].setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#2ECC71")));
                btnRegions[i].setTextColor(Color.WHITE);
                break;
            }
        }
    }

    // ─── ТАЙМЕР ──────────────────────────────────────────────────────────────

    private void startTimer(long duration) {
        if (timer != null) timer.cancel();
        timer = new CountDownTimer(duration, 100) {
            public void onTick(long m) {
                timeLeftInMillis = m;
                int seconds = (int) (m / 1000);
                tvTimer.setText(seconds + "s");
                tvTimer.setTextColor(seconds <= 5
                        ? Color.parseColor("#E74C3C")
                        : Color.parseColor("#2ECC71"));
            }
            public void onFinish() {
                if (!isPaused) {
                    streak = 0;
                    lives--;
                    updateLivesUI();
                    progressBar.setProgress(lives);

                    Vibrator vib = (Vibrator) requireContext().getSystemService(android.content.Context.VIBRATOR_SERVICE);
                    if (vib != null) vib.vibrate(VibrationEffect.createOneShot(300, 255));

                    highlightCorrect(currentCorrectRegion);
                    for (Button b : btnRegions) b.setEnabled(false);

                    if (lives <= 0) {
                        new Handler().postDelayed(() -> showResult(), 1200);
                    } else {
                        new Handler().postDelayed(() -> nextQuestion(), 1200);
                    }
                }
            }
        }.start();
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

    // ─── UI ──────────────────────────────────────────────────────────────────

    private void resetButtons() {
        for (int i = 0; i < 6; i++) {
            btnRegions[i].setEnabled(true);
            btnRegions[i].setBackgroundTintList(null);
            btnRegions[i].setBackground(requireContext().getDrawable(R.drawable.quiz_button_answer));
            btnRegions[i].setTextColor(Color.parseColor("#2C3E50"));
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

    // ─── ДИАЛОГ РЕЗУЛЬТАТА (как в QuizGameFragment) ───────────────────────────

    private void showResult() {
        if (timer != null) timer.cancel();
        if (!isAdded()) return;

        // Используем тот же dialog_quiz_result что и QuizGameFragment
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View v = getLayoutInflater().inflate(R.layout.dialog_quiz_result, null);
        builder.setView(v);
        builder.setCancelable(false);
        AlertDialog d = builder.create();

        int safe = questionNumber > 0 ? questionNumber : 1;
        int percentage = (int) ((score * 100.0) / safe);

        ((TextView) v.findViewById(R.id.tvResultCorrect)).setText("Правильно: " + score + "/" + questionNumber);
        ((TextView) v.findViewById(R.id.tvResultPercentage)).setText(percentage + "%");
        ((TextView) v.findViewById(R.id.tvResultStreak)).setText("Лучшая серия: " + maxStreak + " 🔥");

        v.findViewById(R.id.btnRetry).setOnClickListener(view -> {
            d.dismiss();
            score = 0;
            lives = MAX_LIVES;
            questionNumber = 0;
            streak = 0;
            maxStreak = 0;
            Collections.shuffle(countryList);
            nextQuestion();
        });

        v.findViewById(R.id.btnToMenu).setOnClickListener(view -> {
            d.dismiss();
            getParentFragmentManager().popBackStack();
        });

        d.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}