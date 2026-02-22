package com.example.countryexplorerd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.countryexplorerd.models.CountryNote;
import com.google.android.material.button.MaterialButton;

public class DetailsFragment extends Fragment {

    private String countryName;
    private EditText etNote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBackDetails);
        TextView tvFlag = view.findViewById(R.id.details_flag);
        TextView tvName = view.findViewById(R.id.details_name);
        TextView tvCapital = view.findViewById(R.id.details_capital);
        TextView tvCurrency = view.findViewById(R.id.details_currency);
        TextView tvLanguage = view.findViewById(R.id.details_language);
        TextView tvInfo = view.findViewById(R.id.details_info);

        MaterialButton btnMap = view.findViewById(R.id.btnOpenMap);
        MaterialButton btnShare = view.findViewById(R.id.btnShare);

        etNote = view.findViewById(R.id.etCountryNote);

        // Явно устанавливаем inputType программно — это решает проблему с русским
        etNote.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etNote.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        MaterialButton btnSaveNote = view.findViewById(R.id.btnSaveNote);
        MaterialButton btnDeleteNote = view.findViewById(R.id.btnDeleteNote);

        if (getArguments() != null) {
            countryName = getArguments().getString("country_name", "");
            String capital = getArguments().getString("country_capital", "");

            tvName.setText(countryName);
            tvFlag.setText(getArguments().getString("country_flag", "🏳️"));
            tvCapital.setText(capital);
            tvCurrency.setText(getArguments().getString("country_currency", "Не указана"));
            tvLanguage.setText(getArguments().getString("country_language", "Не указан"));
            tvInfo.setText(getArguments().getString("country_info", "Описание скоро появится..."));

            loadNote();

            if (btnMap != null) {
                btnMap.setOnClickListener(v -> {
                    try {
                        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(countryName + " " + capital));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                        startActivity(mapIntent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Приложение Карт не найдено", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (btnShare != null) {
                btnShare.setOnClickListener(v -> {
                    String info = getArguments().getString("country_info", "");
                    String shareText = "Страна: " + countryName + "\nИнфо: " + info;
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(sendIntent, "Поделиться"));
                });
            }

            if (btnSaveNote != null) {
                btnSaveNote.setOnClickListener(v -> saveNote());
            }

            if (btnDeleteNote != null) {
                btnDeleteNote.setOnClickListener(v -> deleteNote());
            }
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        return view;
    }

    private void loadNote() {
        new Thread(() -> {
            CountryNote note = MainActivity.db.noteDao().getNoteByCountry(countryName);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (note != null && etNote != null) {
                        etNote.setText(note.getNoteText());
                        etNote.setHint("Редактировать заметку...");
                    } else if (etNote != null) {
                        etNote.setHint("Добавьте свою заметку о стране...");
                    }
                });
            }
        }).start();
    }

    private void saveNote() {
        if (etNote == null) return;
        String noteText = etNote.getText().toString().trim();
        if (noteText.isEmpty()) {
            Toast.makeText(getContext(), "Заметка пустая", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            CountryNote note = new CountryNote(countryName, noteText);
            MainActivity.db.noteDao().insert(note);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Заметка сохранена ✓", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void deleteNote() {
        new Thread(() -> {
            MainActivity.db.noteDao().deleteByCountry(countryName);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (etNote != null) {
                        etNote.setText("");
                        etNote.setHint("Добавьте свою заметку о стране...");
                    }
                    Toast.makeText(getContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}