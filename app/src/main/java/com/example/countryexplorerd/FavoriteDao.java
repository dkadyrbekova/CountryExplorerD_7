package com.example.countryexplorerd; // Твой актуальный пакет

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy; // Добавим для безопасности
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    List<FavoriteCountry> getAllFavorites();

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE countryName = :name)")
    boolean isFavorite(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE) // Если вдруг такая запись есть, просто обновим
    void insert(FavoriteCountry country);

    @Delete
    void delete(FavoriteCountry country);
}