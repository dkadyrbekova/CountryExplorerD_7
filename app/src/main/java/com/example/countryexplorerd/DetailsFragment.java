package com.example.countryexplorerd; // Твой актуальный пакет

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;

public class DetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Убедись, что файл fragment_details.xml перенесен в папку res/layout
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Привязка элементов интерфейса
        ImageButton btnBack = view.findViewById(R.id.btnBackDetails);
        TextView tvFlag = view.findViewById(R.id.details_flag);
        TextView tvName = view.findViewById(R.id.details_name);
        TextView tvCapital = view.findViewById(R.id.details_capital);
        TextView tvCurrency = view.findViewById(R.id.details_currency);
        TextView tvLanguage = view.findViewById(R.id.details_language);
        TextView tvInfo = view.findViewById(R.id.details_info);

        MaterialButton btnMap = view.findViewById(R.id.btnOpenMap);
        MaterialButton btnShare = view.findViewById(R.id.btnShare);

        if (getArguments() != null) {
            // Извлекаем данные, которые передал MainActivity
            String name = getArguments().getString("country_name", "");
            String capital = getArguments().getString("country_capital", "");

            tvName.setText(name);
            tvFlag.setText(getArguments().getString("country_flag", "🏳️"));
            tvCapital.setText(capital);

            // Данные из нашего JSON в Postman
            tvCurrency.setText(getArguments().getString("country_currency", "Не указана"));
            tvLanguage.setText(getArguments().getString("country_language", "Не указан"));
            tvInfo.setText(getArguments().getString("country_info", "Описание скоро появится..."));

            // Кнопка открытия карт
            if (btnMap != null) {
                btnMap.setOnClickListener(v -> {
                    try {
                        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(name + " " + capital));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                        startActivity(mapIntent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Приложение Карт не найдено", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Кнопка "Поделиться"
            if (btnShare != null) {
                btnShare.setOnClickListener(v -> {
                    String info = getArguments().getString("country_info", "");
                    String shareText = "Страна: " + name + "\nИнфо: " + info;

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(sendIntent, "Поделиться"));
                });
            }
        }

        // Кнопка Назад
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        return view;
    }
}