package database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// Добавили VisitedCountry.class и увеличили версию до 3
@Database(entities = {FavoriteCountry.class, CountryNote.class, VisitedCountry.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();

    public abstract CountryNoteDao noteDao();

    // НОВЫЙ DAO для посещённых стран
    public abstract VisitedCountryDao visitedDao();
}