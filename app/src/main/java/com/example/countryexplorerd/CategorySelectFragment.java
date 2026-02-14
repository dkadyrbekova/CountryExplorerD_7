package com.example.countryexplorerd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CategorySelectFragment extends Fragment {

    private String currentMode = "capitals";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Получаем режим, выбранный на экране с картой
            currentMode = getArguments().getString("mode", "capitals");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_select, container, false);

        // Кнопка назад
        ImageButton btnBack = view.findViewById(R.id.btnBackToModes);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Привязываем клики к карточкам из твоего XML
        setupClick(view.findViewById(R.id.selectEurope), "Europe");
        setupClick(view.findViewById(R.id.selectAsia), "Asia");
        setupClick(view.findViewById(R.id.selectAfrica), "Africa");
        setupClick(view.findViewById(R.id.selectNAmerica), "Americas"); // Для API это обычно Americas
        setupClick(view.findViewById(R.id.selectSAmerica), "Americas");
        setupClick(view.findViewById(R.id.selectAustralia), "Oceania");

        // Кнопку btnAllCountries я удалила, так как её нет в твоем XML

        return view;
    }

    private void setupClick(View cardView, String continentName) {
        if (cardView != null) {
            cardView.setOnClickListener(v -> {
                FlashcardFragment fragment = new FlashcardFragment();
                Bundle args = new Bundle();
                args.putString("mode", currentMode);
                args.putString("continent", continentName);
                fragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        }
    }
}