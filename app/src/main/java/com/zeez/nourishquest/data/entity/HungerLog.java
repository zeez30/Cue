package com.zeez.nourishquest.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Stores a single hunger or fullness check-in.
// mealPhase is either "BEFORE" (hunger going into a meal) or "AFTER" (fullness coming out).
// scaleLevel is 1–10 following the Tribole & Resch hunger/fullness scale.
@Entity(tableName = "hunger_logs")
public class HungerLog {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // 1 = painfully hungry, 5 = neutral, 10 = painfully full
    @ColumnInfo(name = "scale_level")
    private int scaleLevel;

    // "BEFORE" or "AFTER" meal
    @ColumnInfo(name = "meal_phase")
    private String mealPhase;

    @ColumnInfo(name = "note")
    private String note;

    public HungerLog(long timestamp, int scaleLevel, String mealPhase, String note) {
        this.timestamp = timestamp;
        this.scaleLevel = scaleLevel;
        this.mealPhase = mealPhase;
        this.note = note;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getScaleLevel() { return scaleLevel; }
    public void setScaleLevel(int scaleLevel) { this.scaleLevel = scaleLevel; }

    public String getMealPhase() { return mealPhase; }
    public void setMealPhase(String mealPhase) { this.mealPhase = mealPhase; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
