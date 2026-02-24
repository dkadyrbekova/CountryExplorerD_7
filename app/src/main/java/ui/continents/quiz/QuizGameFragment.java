package ui.continents.quiz;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.countryexplorerd.FavoriteCountry;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class QuizGameFragment extends Fragment {

    private String gameType, region;
    private List<Country> filteredCountries = new ArrayList<>();
    private int lives = 3, correctCount = 0, totalQuestions = 0, streak = 0, maxStreak = 0;
    private CountDownTimer timer;
    private long timeLeftInMillis = 15000;
    private boolean isPaused = false;

    // Храним правильный ответ текущего вопроса
    private String currentCorrectAnswer = "";
    // Сколько раз ошибся на текущем вопросе
    private int wrongAttemptsOnCurrentQuestion = 0;

    private TextView tvQuestion, tvTimer, tvLives, tvScore, tvProgress, tvStreak;
    private Button[] btns = new Button[4];
    private ImageButton btnPause, btnBack;
    private ProgressBar progressBar;
    private CountryViewModel viewModel;
    private Map<String, CountryDetail> detailsMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_game, container, false);

        tvQuestion = view.findViewById(R.id.tvQuizQuestion);
        tvTimer    = view.findViewById(R.id.tvQuizTimer);
        tvLives    = view.findViewById(R.id.tvQuizLives);
        tvScore    = view.findViewById(R.id.tvQuizScore);
        tvProgress = view.findViewById(R.id.tvQuizProgress);
        tvStreak   = view.findViewById(R.id.tvQuizStreak);
        progressBar = view.findViewById(R.id.progressBarQuiz);

        btnPause = view.findViewById(R.id.btnQuizPause);
        btnBack  = view.findViewById(R.id.btnQuizBack);

        btns[0] = view.findViewById(R.id.btnAnswer1);
        btns[1] = view.findViewById(R.id.btnAnswer2);
        btns[2] = view.findViewById(R.id.btnAnswer3);
        btns[3] = view.findViewById(R.id.btnAnswer4);

        btnBack.setOnClickListener(v -> {
            if (timer != null) timer.cancel();
            getParentFragmentManager().popBackStack();
        });

        btnPause.setOnClickListener(v -> togglePause());

        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        if (getArguments() != null) {
            gameType = getArguments().getString("game_type");
            region   = getArguments().getString("region");

            viewModel.getDetails().observe(getViewLifecycleOwner(), details -> {
                if (details != null) detailsMap = details;
            });

            viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
                if (countries != null && filteredCountries.isEmpty()) {
                    filterCountries(countries);
                    if (filteredCountries.size() < 4) {
                        Toast.makeText(getContext(), "Нужно минимум 4 страны!", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        updateLivesUI();
                        updateScoreUI();
                        nextQuestion();
                    }
                }
            });
        }

        return view;
    }

    private void filterCountries(List<Country> allCountries) {
        if ("Favorites".equals(region)) {
            viewModel.loadFavorites();
            viewModel.getFavorites().observe(getViewLifecycleOwner(), favorites -> {
                if (favorites != null) {
                    for (FavoriteCountry fav : favorites) {
                        for (Country c : allCountries) {
                            if (c.getName().equals(fav.getCountryName())) {
                                filteredCountries.add(c);
                                break;
                            }
                        }
                    }
                }
            });
        } else {
            for (Country c : allCountries) {
                if ("All".equals(region) || (c.getRegion() != null && c.getRegion().equalsIgnoreCase(region))) {
                    filteredCountries.add(c);
                }
            }
        }
    }

    private void nextQuestion() {
        // Проверяем окончание игры — только по количеству вопросов или 0 жизней
        if (totalQuestions >= 10) {
            showResultDialog();
            return;
        }

        isPaused = false;
        wrongAttemptsOnCurrentQuestion = 0;
        timeLeftInMillis = 15000;
        totalQuestions++;

        updateProgressUI();
        startTimer(timeLeftInMillis);

        Country correct = filteredCountries.get(new Random().nextInt(filteredCountries.size()));
        String question = "", answer = "";

        if ("Capitals".equals(gameType)) {
            question = correct.getName();
            answer   = correct.getCapital();
        } else if ("Flags".equals(gameType)) {
            question = correct.getFlag();
            answer   = correct.getName();
        } else {
            question = correct.getName();
            if (detailsMap != null && detailsMap.containsKey(correct.getName())) {
                answer = detailsMap.get(correct.getName()).getCurrency();
            } else {
                answer = "???";
            }
        }

        tvQuestion.setText(question);
        final String finalAnswer = (answer != null) ? answer : "---";
        currentCorrectAnswer = finalAnswer;

        List<String> options = generateOptions(correct, finalAnswer);
        Collections.shuffle(options);

        // Сброс кнопок
        for (int i = 0; i < 4; i++) {
            btns[i].setEnabled(true);
            btns[i].setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            btns[i].setTextColor(Color.parseColor("#2C3E50"));
            btns[i].setText(options.get(i));

            final String selectedAnswer = options.get(i);
            final Button currentBtn = btns[i];

            btns[i].setOnClickListener(v -> {
                if (isPaused) return;
                handleAnswer(currentBtn, selectedAnswer, finalAnswer);
            });
        }
    }

    // ─── ГЛАВНАЯ ЛОГИКА ОТВЕТА ────────────────────────────────────────────────

    private void handleAnswer(Button selectedBtn, String selected, String correct) {
        boolean isCorrect = selected.equals(correct);

        if (isCorrect) {
            // ✅ Правильный ответ — подсвечиваем зелёным и переходим дальше
            if (timer != null) timer.cancel();
            for (Button b : btns) b.setEnabled(false);

            selectedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2ECC71")));
            selectedBtn.setTextColor(Color.WHITE);

            correctCount++;
            streak++;
            if (streak > maxStreak) maxStreak = streak;
            updateScoreUI();
            saveProgress(correct);

            new Handler().postDelayed(this::nextQuestion, 800);

        } else {
            // ❌ Неправильный ответ — подсвечиваем красным, снимаем жизнь
            selectedBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E74C3C")));
            selectedBtn.setTextColor(Color.WHITE);
            selectedBtn.setEnabled(false); // эту кнопку больше нельзя нажать

            try {
                Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
                selectedBtn.startAnimation(shake);
            } catch (Exception ignored) {}

            Vibrator vib = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
            if (vib != null) vib.vibrate(VibrationEffect.createOneShot(200, 255));

            streak = 0;
            lives--;
            wrongAttemptsOnCurrentQuestion++;
            updateLivesUI();
            updateScoreUI();

            if (lives <= 0) {
                // ☠️ Жизни кончились — показываем правильный ответ зелёным и заканчиваем
                if (timer != null) timer.cancel();
                for (Button b : btns) b.setEnabled(false);
                highlightCorrectAnswer(correct);

                new Handler().postDelayed(this::showResultDialog, 1500);
            }
            // Если жизни ещё есть — кнопки остальные активны, игрок продолжает отвечать на ЭТОТ вопрос
        }
    }

    // Подсвечивает правильный ответ зелёным (когда жизни кончились)
    private void highlightCorrectAnswer(String correctAnswer) {
        for (Button b : btns) {
            if (b.getText().toString().equals(correctAnswer)) {
                b.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2ECC71")));
                b.setTextColor(Color.WHITE);
                break;
            }
        }
    }

    // ─── ТАЙМЕР ───────────────────────────────────────────────────────────────

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
                    // Таймер вышел = неправильный ответ
                    streak = 0;
                    lives--;
                    updateLivesUI();
                    updateScoreUI();

                    Vibrator vib = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
                    if (vib != null) vib.vibrate(VibrationEffect.createOneShot(300, 255));

                    if (lives <= 0) {
                        for (Button b : btns) b.setEnabled(false);
                        highlightCorrectAnswer(currentCorrectAnswer);
                        new Handler().postDelayed(() -> showResultDialog(), 1500);
                    } else {
                        // Жизни есть — перезапускаем таймер на этот же вопрос
                        timeLeftInMillis = 15000;
                        startTimer(timeLeftInMillis);
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

    // ─── ВСПОМОГАТЕЛЬНЫЕ ─────────────────────────────────────────────────────

    private List<String> generateOptions(Country correct, String correctAnswer) {
        List<String> options = new ArrayList<>();
        options.add(correctAnswer);

        int attempts = 0;
        while (options.size() < 4 && attempts < 100) {
            attempts++;
            Country r = filteredCountries.get(new Random().nextInt(filteredCountries.size()));
            String cand;

            if ("Capitals".equals(gameType)) {
                cand = r.getCapital();
            } else if ("Flags".equals(gameType)) {
                cand = r.getName();
            } else {
                if (detailsMap != null && detailsMap.containsKey(r.getName())) {
                    cand = detailsMap.get(r.getName()).getCurrency();
                } else continue;
            }

            if (cand != null && !options.contains(cand)) {
                options.add(cand);
            }
        }
        return options;
    }

    private void saveProgress(String correctAnswer) {
        SharedPreferences prefs = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
        // Ищем страну по правильному ответу
        for (Country c : filteredCountries) {
            String name = c.getName();
            if ("Capitals".equals(gameType) && correctAnswer.equals(c.getCapital())) {
                prefs.edit().putBoolean("capitals_" + name, true).apply();
                break;
            } else if ("Flags".equals(gameType) && correctAnswer.equals(c.getName())) {
                prefs.edit().putBoolean("flags_" + name, true).apply();
                break;
            } else if (correctAnswer.equals(name)) {
                prefs.edit().putBoolean("currency_" + name, true).apply();
                break;
            }
        }
    }

    private void updateLivesUI() {
        StringBuilder h = new StringBuilder();
        for (int i = 0; i < lives; i++) h.append("❤️");
        for (int i = lives; i < 3; i++) h.append("🖤");
        tvLives.setText(h.toString());
    }

    private void updateScoreUI() {
        tvScore.setText("⭐ " + correctCount);
        if (streak > 0) {
            tvStreak.setVisibility(View.VISIBLE);
            tvStreak.setText("🔥 " + streak);
        } else {
            tvStreak.setVisibility(View.GONE);
        }
    }

    private void updateProgressUI() {
        tvProgress.setText(totalQuestions + "/10");
        progressBar.setMax(10);
        progressBar.setProgress(totalQuestions);
    }

    private void showResultDialog() {
        if (timer != null) timer.cancel();
        if (!isAdded()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View v = getLayoutInflater().inflate(R.layout.dialog_quiz_result, null);
        builder.setView(v);
        builder.setCancelable(false);
        AlertDialog d = builder.create();

        int safe = totalQuestions > 0 ? totalQuestions : 1;
        int percentage = (int) ((correctCount * 100.0) / safe);

        ((TextView) v.findViewById(R.id.tvResultCorrect)).setText("Правильно: " + correctCount + "/" + totalQuestions);
        ((TextView) v.findViewById(R.id.tvResultPercentage)).setText(percentage + "%");
        ((TextView) v.findViewById(R.id.tvResultStreak)).setText("Лучшая серия: " + maxStreak + " 🔥");

        v.findViewById(R.id.btnRetry).setOnClickListener(view -> {
            d.dismiss();
            resetGame();
        });

        v.findViewById(R.id.btnToMenu).setOnClickListener(view -> {
            d.dismiss();
            getParentFragmentManager().popBackStack();
        });

        d.show();
    }

    private void resetGame() {
        lives = 3;
        correctCount = 0;
        totalQuestions = 0;
        streak = 0;
        maxStreak = 0;
        wrongAttemptsOnCurrentQuestion = 0;
        updateLivesUI();
        updateScoreUI();
        nextQuestion();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) timer.cancel();
    }
}