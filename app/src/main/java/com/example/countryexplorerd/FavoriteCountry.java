package com.example.countryexplorerd; // Твой новый пакет

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "favorites")
public class FavoriteCountry {

    @PrimaryKey
    @NonNull
    public String countryName;

    public FavoriteCountry(@NonNull String countryName) {
        this.countryName = countryName;
    }

    // Добавим геттер, чтобы FavoritesFragment мог спокойно брать имя
    @NonNull
    public String getCountryName() {
        return countryName;
    }
}