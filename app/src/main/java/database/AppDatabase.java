package database;

import androidx.room.Database;
import androidx.room.migration.Migration;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

// Добавили DailyChallenge.class и увеличили версию до 4
@Database(
        entities = {
                FavoriteCountry.class,
                CountryNote.class,
                VisitedCountry.class,
                database.DailyChallenge.class   // <- новая таблица
        },
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();
    public abstract CountryNoteDao noteDao();
    public abstract VisitedCountryDao visitedDao();
    public abstract database.DailyChallengeDao challengeDao(); // <- новый DAO

    // Миграция 3 -> 4: просто создаём новую таблицу
    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS daily_challenges (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "dayOfYear INTEGER NOT NULL, " +
                            "year INTEGER NOT NULL, " +
                            "title TEXT, " +
                            "description TEXT, " +
                            "type TEXT, " +
                            "continent TEXT, " +
                            "targetCount INTEGER NOT NULL, " +
                            "currentCount INTEGER NOT NULL, " +
                            "completed INTEGER NOT NULL, " +
                            "completedAt INTEGER NOT NULL)"
            );
        }
    };
}