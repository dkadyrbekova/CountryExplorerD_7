package database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CountryNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CountryNote note);

    @Update
    void update(CountryNote note);

    @Delete
    void delete(CountryNote note);

    @Query("SELECT * FROM country_notes WHERE countryName = :countryName LIMIT 1")
    CountryNote getNoteByCountry(String countryName);

    @Query("DELETE FROM country_notes WHERE countryName = :countryName")
    void deleteByCountry(String countryName);

    @Query("SELECT * FROM country_notes")
    List<CountryNote> getAllNotes();

    // ДОБАВЬ ЭТО:
    @Query("DELETE FROM country_notes")
    void deleteAll();
}