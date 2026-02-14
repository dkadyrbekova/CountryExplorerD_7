package com.example.countryexplorerd;

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
        View view = inflater.inflate(R.layout.fragment_continents, container, false);

        ImageView mapImage = view.findViewById(R.id.imgWorldMap);

        CardView cardCapitals = view.findViewById(R.id.cardCapitals);
        CardView cardFlags = view.findViewById(R.id.cardFlags);
        CardView cardCurrency = view.findViewById(R.id.cardCurrency);

        // ИСПРАВЛЕНО: Теперь ведет на выбор континента (CategorySelectFragment)
        if (cardCapitals != null) {
            cardCapitals.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("mode", "capitals"); // Передаем режим "столицы"

                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
        }

        // ИСПРАВЛЕНО: Теперь ведет на выбор континента (CategorySelectFragment)
        if (cardFlags != null) {
            cardFlags.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("mode", "flags"); // Передаем режим "флаги"

                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
        }

        // ИСПРАВЛЕНО: Теперь ведет на выбор континента (CategorySelectFragment)
        if (cardCurrency != null) {
            cardCurrency.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("mode", "currency"); // Передаем режим "валюты"

                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
        }

        // Клики по карте (оставляем как есть, они ведут на конкретные континенты)
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
        Fragment fragment = null;

        if (r > 150 && g < 120 && b < 120) {
            Toast.makeText(getContext(), "Азия", Toast.LENGTH_SHORT).show();
            fragment = new AsiaFragment();
        } else if (r > 100 && r < 220 && b > 150) {
            Toast.makeText(getContext(), "Африка", Toast.LENGTH_SHORT).show();
            fragment = new AfricaFragment();
        } else if (r < 160 && g > 110 && b > 180) {
            Toast.makeText(getContext(), "Сев. Америка", Toast.LENGTH_SHORT).show();
            fragment = new NAmericaFragment();
        } else if (r > 180 && g > 150 && b < 170) {
            Toast.makeText(getContext(), "Юж. Америка", Toast.LENGTH_SHORT).show();
            fragment = new SAmericaFragment();
        } else if (g > 140 && r < 100) {
            Toast.makeText(getContext(), "Европа", Toast.LENGTH_SHORT).show();
            fragment = new EuropeFragment();
        } else if (r > 100 && g > 60 && b < 160) {
            Toast.makeText(getContext(), "Австралия", Toast.LENGTH_SHORT).show();
            fragment = new AustraliaFragment();
        }

        if (fragment != null) {
            switchFragment(fragment);
        }
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Чтобы можно было вернуться назад к карте
                .commit();
    }
}