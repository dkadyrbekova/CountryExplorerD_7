package com.example.countryexplorerd.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "country_notes")
public class CountryNote {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String countryName; // Название страны
    private String noteText;    // Текст заметки

    // Конструкторы
    public CountryNote() {}

    public CountryNote(String countryName, String noteText) {
        this.countryName = countryName;
        this.noteText = noteText;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }
}