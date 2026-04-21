package com.zeez.nourishquest.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hunger_logs")
public class HungerLog {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // Level 1 to 10 based on IE scale
    @ColumnInfo(name = "scale_level")
    private int scaleLevel;

    // Track if entry is before or after eating
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

    // Getters and Setters
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