package database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface DailyChallengeDao {

    // Вставить новый челлендж
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(DailyChallenge challenge);

    // Обновить прогресс (currentCount, completed, completedAt)
    @Update
    void update(DailyChallenge challenge);

    // Получить челлендж сегодняшнего дня — LiveData для автообновления UI
    @Query("SELECT * FROM daily_challenges WHERE dayOfYear = :dayOfYear AND year = :year LIMIT 1")
    LiveData<DailyChallenge> getTodayChallenge(int dayOfYear, int year);

    // Синхронная версия — нужна в Repository чтобы проверить есть ли уже запись
    @Query("SELECT * FROM daily_challenges WHERE dayOfYear = :dayOfYear AND year = :year LIMIT 1")
    DailyChallenge getTodayChallengeSync(int dayOfYear, int year);

    // Удалить старые челленджи (старше 30 дней) — чтобы база не росла
    @Query("DELETE FROM daily_challenges WHERE dayOfYear < :minDay AND year <= :year")
    void deleteOld(int minDay, int year);
}