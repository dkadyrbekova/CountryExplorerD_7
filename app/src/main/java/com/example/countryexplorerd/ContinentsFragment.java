package com.example.countryexplorerd; // Твой пакет

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ContinentsFragment extends Fragment {

    public ContinentsFragment() {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Убедись, что fragment_continents.xml перенесен в res/layout
        View view = inflater.inflate(R.layout.fragment_continents, container, false);

        // 1. Инициализация карты
        ImageView mapImage = view.findViewById(R.id.imgWorldMap);

        // 2. Инициализация кнопок
        CardView cardCapitals = view.findViewById(R.id.cardCapitals);
        CardView cardFlags = view.findViewById(R.id.cardFlags);
        CardView cardCurrency = view.findViewById(R.id.cardCurrency);

        // Кнопки пока просто выдают Toast, так как фрагментов игры еще нет
        if (cardCapitals != null) {
            cardCapitals.setOnClickListener(v -> Toast.makeText(getContext(), "Режим: Столицы", Toast.LENGTH_SHORT).show());
        }
        if (cardFlags != null) {
            cardFlags.setOnClickListener(v -> Toast.makeText(getContext(), "Режим: Флаги", Toast.LENGTH_SHORT).show());
        }
        if (cardCurrency != null) {
            cardCurrency.setOnClickListener(v -> Toast.makeText(getContext(), "Режим: Валюты", Toast.LENGTH_SHORT).show());
        }

        // --- ЛОГИКА КЛИКОВ ПО КАРТЕ ---
        if (mapImage != null) {
            mapImage.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.performClick();

                    if (mapImage.getDrawable() == null) return false;

                    Bitmap bitmap = ((BitmapDrawable) mapImage.getDrawable()).getBitmap();

                    int x = (int) (event.getX() * bitmap.getWidth() / mapImage.getWidth());
                    int y = (int) (event.getY() * bitmap.getHeight() / mapImage.getHeight());

                    if (x >= 0 && y >= 0 && x < bitmap.getWidth() && y < bitmap.getHeight()) {
                        int pixel = bitmap.getPixel(x, y);
                        int r = Color.red(pixel);
                        int g = Color.green(pixel);
                        int b = Color.blue(pixel);

                        if (r < 250 || g < 250 || b < 250) {
                            openContinent(r, g, b);
                        }
                    }
                }
                return true;
            });
        }

        return view;
    }

    private void openContinent(int r, int g, int b) {
        // ВНИМАНИЕ: Здесь будут ошибки, пока мы не перенесем фрагменты континентов
        // Если хочешь просто проверить карту, пока закомментируй switchFragment
        String message = "";

        if (r > 150 && g < 120 && b < 120) message = "Азия";
        else if (r > 100 && r < 220 && b > 150) message = "Африка";
        else if (r < 160 && g > 110 && b > 180) message = "Сев. Америка";
        else if (r > 180 && g > 150 && b < 170) message = "Юж. Америка";
        else if (g > 140 && r < 100) message = "Европа";
        else if (r > 100 && g > 60 && b < 160) message = "Австралия";

        if (!message.isEmpty()) {
            Toast.makeText(getContext(), "Выбран континент: " + message, Toast.LENGTH_SHORT).show();
            // Тут позже добавим switchFragment(new AsiaFragment()); и т.д.
        }
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}