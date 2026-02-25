package ui.continents;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ui.continents.adapters.SightseeingAdapter;
import ui.continents.quiz.CategorySelectFragment;
import com.example.countryexplorerd.MainActivity;
import com.example.countryexplorerd.R;
import com.example.countryexplorerd.models.Country;
import com.example.countryexplorerd.models.CountryDetail;
import com.example.countryexplorerd.models.Sightseeing;
import com.example.countryexplorerd.network.RetrofitClient;
import com.example.countryexplorerd.viewmodel.CountryViewModel;
import com.example.countryexplorerd.viewmodel.MainViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContinentsFragment extends Fragment {

    private CountryViewModel countryViewModel;
    private MainViewModel sightseeingViewModel;
    private TextView tvCountryFlag, tvCountryName, tvCountryFact;
    private Button btnLearnMore;
    private Country countryOfDay;
    private RecyclerView rvSightseeing;

    public ContinentsFragment() {}

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_continents, container, false);

        ImageView mapImage    = view.findViewById(R.id.imgWorldMap);
        CardView cardCapitals = view.findViewById(R.id.cardCapitals);
        CardView cardFlags    = view.findViewById(R.id.cardFlags);
        CardView cardCurrency = view.findViewById(R.id.cardCurrency);
        tvCountryFlag         = view.findViewById(R.id.tvCountryFlag);
        tvCountryName         = view.findViewById(R.id.tvCountryName);
        tvCountryFact         = view.findViewById(R.id.tvCountryFact);
        btnLearnMore          = view.findViewById(R.id.btnLearnMore);

        LinearLayout btnFactOfDay = view.findViewById(R.id.btn_fact_of_day);
        if (btnFactOfDay != null) {
            btnFactOfDay.setOnClickListener(v -> showFactOfDay());
        }

        rvSightseeing = view.findViewById(R.id.rvSightseeing);
        if (rvSightseeing != null) {
            rvSightseeing.setLayoutManager(
                    new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        countryViewModel     = new ViewModelProvider(requireActivity()).get(CountryViewModel.class);
        sightseeingViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        loadCountryOfDay();
        setupSightseeingCarousel();
        setupClickListeners(cardCapitals, cardFlags, cardCurrency);
        setupMapTouchListener(mapImage);
        setupDailyChallenge(view); // <- челлендж дня (исправленный)

        return view;
    }

    // ════════════════════════════════════════════════════════════════════
    // ЧЕЛЛЕНДЖ ДНЯ (исправленный - просто карточка)
    // ════════════════════════════════════════════════════════════════════
    private void setupDailyChallenge(View view) {
        CardView cardChallenge = view.findViewById(R.id.cardDailyChallenge);
        TextView tvChallengeTitle = view.findViewById(R.id.tvChallengeTitle);
        TextView tvChallengeDesc = view.findViewById(R.id.tvChallengeDesc);

        if (cardChallenge == null || tvChallengeTitle == null || tvChallengeDesc == null) return;

        countryViewModel.getTodayChallenge().observe(getViewLifecycleOwner(), challenge -> {
            if (challenge == null) {
                // Если челленджа нет, показываем заглушку
                tvChallengeTitle.setText("Флаги мира");
                tvChallengeDesc.setText("Угадай 6 флагов стран со всего мира");
                return;
            }

            // Устанавливаем данные из челленджа
            tvChallengeTitle.setText(challenge.getTitle());
            tvChallengeDesc.setText(challenge.getDescription());

            // Карточка просто информационная, без клика
            cardChallenge.setOnClickListener(null);
            cardChallenge.setAlpha(1.0f);

            // Если хотите, чтобы карточка вела на задание - раскомментируйте:
            /*
            cardChallenge.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("mode", challenge.getType());
                args.putString("continent", challenge.getContinent());

                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
            */
        });
    }

    // ════════════════════════════════════════════════════════════════════
    // ФАКТ ДНЯ
    // ════════════════════════════════════════════════════════════════════
    private void showFactOfDay() {
        try {
            InputStream is = requireContext().getAssets().open("facts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONArray factsArray = new JSONArray(json);
            int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            JSONObject factObj = factsArray.getJSONObject((dayOfYear - 1) % factsArray.length());

            String flag      = factObj.getString("flag");
            String country   = factObj.getString("country");
            String continent = factObj.getString("continent");
            String category  = factObj.getString("category");
            String factText  = factObj.getString("fact");

            showFactBottomSheet(flag, country, continent, category, factText);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Не удалось загрузить факт", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFactBottomSheet(String flag, String country,
                                     String continent, String category, String factText) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_fact_of_day, null);

        TextView flagView    = sheetView.findViewById(R.id.fact_country_flag);
        TextView countryView = sheetView.findViewById(R.id.fact_country_name);
        TextView textView    = sheetView.findViewById(R.id.fact_text);
        TextView dateView    = sheetView.findViewById(R.id.fact_date);
        TextView badgeView   = sheetView.findViewById(R.id.fact_new_badge);

        flagView.setText(flag);
        countryView.setText(country);
        textView.setText(factText);

        String today = new SimpleDateFormat("d MMMM yyyy", new Locale("ru")).format(new Date());
        dateView.setText(today);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("FactPrefs", Context.MODE_PRIVATE);
        int savedDay = prefs.getInt("last_fact_seen", -1);
        int todayDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        badgeView.setVisibility(savedDay != todayDay ? View.VISIBLE : View.GONE);
        prefs.edit().putInt("last_fact_seen", todayDay).apply();

        dialog.setContentView(sheetView);
        dialog.show();
    }

    // ════════════════════════════════════════════════════════════════════
    // КАРУСЕЛЬ ДОСТОПРИМЕЧАТЕЛЬНОСТЕЙ
    // ════════════════════════════════════════════════════════════════════
    private void setupSightseeingCarousel() {
        sightseeingViewModel.getSightseeingList().observe(getViewLifecycleOwner(), sights -> {
            if (sights != null && !sights.isEmpty()) {
                SightseeingAdapter adapter = new SightseeingAdapter(sights, this::showSightseeingDetails);
                rvSightseeing.setAdapter(adapter);
            }
        });
        sightseeingViewModel.fetchSightseeing(RetrofitClient.getInstance().getApi());
    }

    private void showSightseeingDetails(Sightseeing item) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_sightseeing_details, null);

        TextView title       = view.findViewById(R.id.detailTitle);
        TextView fact        = view.findViewById(R.id.detailFact);
        TextView description = view.findViewById(R.id.detailDescription);
        Button btnClose      = view.findViewById(R.id.btnCloseDialog);

        title.setText(item.getTitle());
        fact.setText("💡 Факт: " + item.getFact());
        description.setText(item.getDescription());
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    // ════════════════════════════════════════════════════════════════════
    // СТРАНА ДНЯ
    // ════════════════════════════════════════════════════════════════════
    private void loadCountryOfDay() {
        countryViewModel.getCountries().observe(getViewLifecycleOwner(), countries -> {
            if (countries != null && !countries.isEmpty()) {
                Country selected = getOrGenerateCountryOfDay(countries);
                displayCountryOfDay(selected);
            }
        });
    }

    private Country getOrGenerateCountryOfDay(List<Country> countries) {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("CountryOfDay", Context.MODE_PRIVATE);
        int today    = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int savedDay = prefs.getInt("day", -1);

        if (savedDay == today) {
            String savedName = prefs.getString("countryName", "");
            for (Country c : countries) {
                if (c.getName().equals(savedName)) return c;
            }
        }

        Country selected = countries.get(today % countries.size());
        prefs.edit().putInt("day", today).putString("countryName", selected.getName()).apply();
        return selected;
    }

    private void displayCountryOfDay(Country country) {
        countryOfDay = country;
        tvCountryFlag.setText(country.getFlag());
        tvCountryName.setText(country.getName());

        countryViewModel.getDetails().observe(getViewLifecycleOwner(), details -> {
            if (details != null && details.containsKey(country.getName())) {
                CountryDetail detail = details.get(country.getName());
                if (detail != null && detail.getFacts() != null) {
                    String fact = detail.getFacts();
                    if (fact.length() > 150) fact = fact.substring(0, 147) + "...";
                    tvCountryFact.setText(fact);
                } else {
                    tvCountryFact.setText("Столица: " + country.getCapital());
                }
            } else {
                tvCountryFact.setText("Столица: " + country.getCapital());
            }
        });

        btnLearnMore.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openDetails(country);
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════
    // НАВИГАЦИЯ
    // ════════════════════════════════════════════════════════════════════
    private void setupClickListeners(View... cards) {
        for (View card : cards) {
            if (card == null) continue;
            card.setOnClickListener(v -> {
                String mode = "";
                if      (v.getId() == R.id.cardCapitals) mode = "capitals";
                else if (v.getId() == R.id.cardFlags)    mode = "flags";
                else if (v.getId() == R.id.cardCurrency) mode = "currency";

                Bundle args = new Bundle();
                args.putString("mode", mode);
                CategorySelectFragment fragment = new CategorySelectFragment();
                fragment.setArguments(args);
                switchFragment(fragment);
            });
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupMapTouchListener(ImageView mapImage) {
        if (mapImage == null) return;
        mapImage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.performClick();
                if (mapImage.getDrawable() == null) return false;
                Bitmap bitmap = ((BitmapDrawable) mapImage.getDrawable()).getBitmap();
                int x = (int) (event.getX() * bitmap.getWidth()  / mapImage.getWidth());
                int y = (int) (event.getY() * bitmap.getHeight() / mapImage.getHeight());
                if (x >= 0 && y >= 0 && x < bitmap.getWidth() && y < bitmap.getHeight()) {
                    int pixel = bitmap.getPixel(x, y);
                    int r = Color.red(pixel), g = Color.green(pixel), b = Color.blue(pixel);
                    if (r < 250 || g < 250 || b < 250) openContinent(r, g, b);
                }
            }
            return true;
        });
    }

    private void openContinent(int r, int g, int b) {
        Fragment fragment = null;
        if      (r > 150 && g < 120 && b < 120) fragment = new AsiaFragment();
        else if (r > 100 && r < 220 && b > 150) fragment = new AfricaFragment();
        else if (r < 160 && g > 110 && b > 180) fragment = new NAmericaFragment();
        else if (r > 180 && g > 150 && b < 170) fragment = new SAmericaFragment();
        else if (g > 140 && r < 100)             fragment = new EuropeFragment();
        else if (r > 100 && g > 60  && b < 160) fragment = new AustraliaFragment();

        if (fragment != null) switchFragment(fragment);
    }

    private void switchFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}