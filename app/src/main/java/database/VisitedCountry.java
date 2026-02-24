package database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "visited_countries")
public class VisitedCountry {

    @PrimaryKey
    @NonNull
    private String countryName;

    private String region; // континент — нужен для статистики по континентам
    private long dateAdded; // дата когда отметил (System.currentTimeMillis())

    public VisitedCountry(@NonNull String countryName, String region, long dateAdded) {
        this.countryName = countryName;
        this.region = region;
        this.dateAdded = dateAdded;
    }

    @NonNull
    public String getCountryName() { return countryName; }
    public String getRegion() { return region; }
    public long getDateAdded() { return dateAdded; }

    public void setCountryName(@NonNull String countryName) { this.countryName = countryName; }
    public void setRegion(String region) { this.region = region; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }
}