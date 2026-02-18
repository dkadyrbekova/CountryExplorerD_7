package com.example.countryexplorerd;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Кнопка назад
        view.findViewById(R.id.btnBackSettings).setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        // Темная тема
        Switch switchTheme = view.findViewById(R.id.switchDarkTheme);
        SharedPreferences prefs = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean isDarkTheme = prefs.getBoolean("dark_theme", false);
        switchTheme.setChecked(isDarkTheme);

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_theme", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // ✅ ПЕРЕЗАПУСКАЕМ АКТИВИТИ ДЛЯ ПРИМЕНЕНИЯ ТЕМЫ
            requireActivity().recreate();
        });

        // Сбросить прогресс
        LinearLayout btnResetProgress = view.findViewById(R.id.btnResetProgress);
        btnResetProgress.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Сбросить прогресс?")
                    .setMessage("Весь прогресс обучения будет удалён.")
                    .setPositiveButton("Сбросить", (dialog, which) -> {
                        SharedPreferences progress = requireContext().getSharedPreferences("UserProgress", Context.MODE_PRIVATE);
                        progress.edit().clear().apply();
                        Toast.makeText(getContext(), "Прогресс сброшен", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        // Удалить все заметки
        LinearLayout btnDeleteNotes = view.findViewById(R.id.btnDeleteNotes);
        btnDeleteNotes.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить все заметки?")
                    .setMessage("Все ваши заметки будут удалены.")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                MainActivity.db.noteDao().deleteAll();
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(getContext(), "Заметки удалены", Toast.LENGTH_SHORT).show()
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        // Удалить избранное
        LinearLayout btnDeleteFavorites = view.findViewById(R.id.btnDeleteFavorites);
        btnDeleteFavorites.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Удалить избранное?")
                    .setMessage("Все избранные страны будут удалены.")
                    .setPositiveButton("Удалить", (dialog, which) -> {
                        new Thread(() -> {
                            try {
                                MainActivity.db.favoriteDao().deleteAll();
                                requireActivity().runOnUiThread(() ->
                                        Toast.makeText(getContext(), "Избранное удалено", Toast.LENGTH_SHORT).show()
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
        });

        // О приложении
        LinearLayout btnAbout = view.findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("О приложении")
                    .setMessage("Country Explorer\nВерсия 1.0\n\nПриложение для изучения стран мира")
                    .setPositiveButton("ОК", null)
                    .show();
        });

        return view;
    }
}