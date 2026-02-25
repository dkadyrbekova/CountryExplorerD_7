package database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily_challenges")
public class DailyChallenge {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int dayOfYear;       // номер дня в году (1-365)
    private int year;            // год — чтобы не путать 2025 и 2026
    private String title;        // "Угадай 5 флагов Азии"
    private String description;  // подробное описание задания
    private String type;         // "flags" / "capitals" / "currency" / "find_country"
    private String continent;    // "Asia" / "Europe" / "" = все страны
    private int targetCount;     // сколько нужно выполнить
    private int currentCount;    // сколько уже выполнено
    private boolean completed;   // выполнен ли
    private long completedAt;    // timestamp когда выполнен (0 если нет)

    public DailyChallenge(int dayOfYear, int year, String title, String description,
                          String type, String continent, int targetCount) {
        this.dayOfYear    = dayOfYear;
        this.year         = year;
        this.title        = title;
        this.description  = description;
        this.type         = type;
        this.continent    = continent;
        this.targetCount  = targetCount;
        this.currentCount = 0;
        this.completed    = false;
        this.completedAt  = 0;
    }

    // Геттеры
    public int getId()             { return id; }
    public int getDayOfYear()      { return dayOfYear; }
    public int getYear()           { return year; }
    public String getTitle()       { return title; }
    public String getDescription() { return description; }
    public String getType()        { return type; }
    public String getContinent()   { return continent; }
    public int getTargetCount()    { return targetCount; }
    public int getCurrentCount()   { return currentCount; }
    public boolean isCompleted()   { return completed; }
    public long getCompletedAt()   { return completedAt; }

    // Сеттеры
    public void setId(int id)                    { this.id = id; }
    public void setCurrentCount(int count)       { this.currentCount = count; }
    public void setCompleted(boolean completed)  { this.completed = completed; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }
}