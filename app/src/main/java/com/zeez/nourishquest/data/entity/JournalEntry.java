package com.zeez.nourishquest.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "journal_entries")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // Physical sensations or observations
    @ColumnInfo(name = "body_feeling")
    private String bodyFeeling;

    // Identifying and reframing diet culture thoughts
    @ColumnInfo(name = "challenged_thought")
    private String challengedThought;

    // Focusing on bodily function rather than appearance
    @ColumnInfo(name = "body_gratitude")
    private String bodyGratitude;

    // Overall mood state for the entry
    @ColumnInfo(name = "mood")
    private String mood;

    public JournalEntry(long timestamp, String bodyFeeling, String challengedThought,
                        String bodyGratitude, String mood) {
        this.timestamp = timestamp;
        this.bodyFeeling = bodyFeeling;
        this.challengedThought = challengedThought;
        this.bodyGratitude = bodyGratitude;
        this.mood = mood;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getBodyFeeling() { return bodyFeeling; }
    public void setBodyFeeling(String bodyFeeling) { this.bodyFeeling = bodyFeeling; }

    public String getChallengedThought() { return challengedThought; }
    public void setChallengedThought(String challengedThought) { this.challengedThought = challengedThought; }

    public String getBodyGratitude() { return bodyGratitude; }
    public void setBodyGratitude(String bodyGratitude) { this.bodyGratitude = bodyGratitude; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
}