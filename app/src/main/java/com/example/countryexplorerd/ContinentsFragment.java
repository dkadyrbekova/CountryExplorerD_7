package com.example.countryexplorerd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ContinentsFragment extends Fragment {

    private CountryViewModel viewModel;
    private TextView tvCountryFlag, tvCountryName, tvCountryFact;
    private Button btnLearnMore;
    private Country countryOfDay;

    public ContinentsFragment() {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_continents, container, false);

        ImageView mapImage = view.findViewById(R.id.imgWorldMap);
        CardView cardCapitals = view.findViewById(R.id.cardCapitals);
        CardView cardFlags = view.findViewById(R.id.cardFlags);
        CardView cardCurrency = view.findViewById(R.id.cardCurrency);

        // Элементы "Страна дня"
        tvCountryFlag = view.findViewById(R.id.tvCountryFlag);
        tvCountryName = view.findViewById(R.id.tvCountryName);
        tvCountryFact = view.findViewById(R.id.tvCountryFact);
        btnLearnMore = view.findViewById(R.id.btnLearnMore);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);

        // Загружаем страну дня
        loadCountryOfDay();

        // Карточки обучения
        if (cardCapitals != null) {
            cardCapitals.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("mode", "capitals");
                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
        }

        if (cardFlags != null) {
            cardFlags.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("mode", "flags");
                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
        }

        if (cardCurrency != null) {
            cardCurrency.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("mode", "currency");
                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
        }

        // Клики по карте
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

    private void loadCountryOfDay() {
        viewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null && !countries.isEmpty()) {
                Country selectedCountry = getOrGenerateCountryOfDay(countries);
                displayCountryOfDay(selectedCountry);
            }
        });
    }

    private Country getOrGenerateCountryOfDay(List<Country> countries) {
        SharedPreferences prefs = requireContext().getSharedPreferences("CountryOfDay", Context.MODE_PRIVATE);

        // Получаем сегодняшний день года (1-365)
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int savedDay = prefs.getInt("day", -1);

        if (savedDay == today) {
            // Уже выбрана страна для сегодня
            String savedCountryName = prefs.getString("countryName", "");
            for (Country c : countries) {
                if (c.getName().equals(savedCountryName)) {
                    return c;
                }
            }
        }

        // Генерируем новую страну дня
        int randomIndex = today % countries.size(); // Используем день как seed для постоянства
        Country selected = countries.get(randomIndex);

        // Сохраняем
        prefs.edit()
                .putInt("day", today)
                .putString("countryName", selected.getName())
                .apply();

        return selected;
    }

    private void displayCountryOfDay(Country country) {
        countryOfDay = country;

        tvCountryFlag.setText(country.getFlag());
        tvCountryName.setText(country.getName());

        // Загружаем детали для интересного факта
        viewModel.getDetails().observe(getViewLifecycleOwner(), details -> {
            if (details != null && details.containsKey(country.getName())) {
                CountryDetail detail = details.get(country.getName());
                if (detail != null && detail.getFacts() != null) {
                    String fact = detail.getFacts();
                    // Берём первые 150 символов факта
                    if (fact.length() > 150) {
                        fact = fact.substring(0, 147) + "...";
                    }
                    tvCountryFact.setText(fact);
                } else {
                    tvCountryFact.setText("Столица: " + country.getCapital());
                }
            } else {
                tvCountryFact.setText("Столица: " + country.getCapital());
            }
        });

        // Кнопка "Узнать больше" открывает детали страны
        btnLearnMore.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDetails(country);
            }
        });
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
                .addToBackStack(null)
                .commit();
    }
}