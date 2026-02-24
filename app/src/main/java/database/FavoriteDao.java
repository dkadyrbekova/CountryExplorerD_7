package database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert
    void insert(FavoriteCountry favorite);

    @Delete
    void delete(FavoriteCountry favorite);

    @Query("SELECT * FROM favorites")
    List<FavoriteCountry> getAllFavorites();

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE countryName = :name LIMIT 1)")
    boolean isFavorite(String name);

    // ДОБАВЬ ЭТО:
    @Query("DELETE FROM favorites")
    void deleteAll();
}