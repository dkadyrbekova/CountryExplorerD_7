package com.example.countryexplorerd; // Твой новый пакет

import androidx.room.Database;
import androidx.room.RoomDatabase;

// Указываем сущности и версию
@Database(entities = {FavoriteCountry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // Ссылка на интерфейс, который мы создали отдельным файлом
    public abstract FavoriteDao favoriteDao();
}