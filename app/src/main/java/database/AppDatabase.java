package database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// ВАЖНО: Добавили CountryNote.class и увеличили версию до 2
@Database(entities = {FavoriteCountry.class, CountryNote.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Существующий DAO для избранного
    public abstract FavoriteDao favoriteDao();

    // НОВЫЙ DAO для заметок
    public abstract CountryNoteDao noteDao();
}