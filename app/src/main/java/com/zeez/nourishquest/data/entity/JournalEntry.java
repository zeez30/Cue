package com.zeez.nourishquest.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Stores a single body journal entry with three IE-based prompts.
// All three text fields are optional — users can fill in one, two, or all three.
@Entity(tableName = "journal_entries")
public class JournalEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // Prompt 1: physical body sensations right now
    @ColumnInfo(name = "body_feeling")
    private String bodyFeeling;

    // Prompt 2: a diet-culture thought and a reframe of it
    @ColumnInfo(name = "challenged_thought")
    private String challengedThought;

    // Prompt 3: something the body did today — function, not appearance
    @ColumnInfo(name = "body_gratitude")
    private String bodyGratitude;

    // GREAT, GOOD, OKAY, DIFFICULT, ROUGH
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
