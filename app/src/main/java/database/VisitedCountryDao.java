package database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VisitedCountryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(VisitedCountry country);

    @Delete
    void delete(VisitedCountry country);

    @Query("SELECT * FROM visited_countries ORDER BY dateAdded DESC")
    LiveData<List<VisitedCountry>> getAll();

    @Query("SELECT COUNT(*) FROM visited_countries")
    LiveData<Integer> getCount();

    @Query("SELECT COUNT(DISTINCT region) FROM visited_countries")
    LiveData<Integer> getContinentsCount();

    // Проверка — посещена ли страна
    @Query("SELECT COUNT(*) FROM visited_countries WHERE countryName = :name")
    int isVisited(String name);

    @Query("DELETE FROM visited_countries")
    void deleteAll();
}