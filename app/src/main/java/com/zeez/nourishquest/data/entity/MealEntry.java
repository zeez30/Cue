package com.zeez.nourishquest.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_entries")
public class MealEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // Qualitative description of the meal
    @ColumnInfo(name = "food_description")
    private String foodDescription;

    // User satisfaction score from 1 to 10
    @ColumnInfo(name = "satisfaction_rating")
    private int satisfactionRating;

    // Environmental context of the meal
    @ColumnInfo(name = "eating_context")
    private String eatingContext;

    // Mood or emotional state during consumption
    @ColumnInfo(name = "emotional_state")
    private String emotionalState;

    // Categorization added in database version 2
    @ColumnInfo(name = "meal_type")
    private String mealType;

    @ColumnInfo(name = "note")
    private String note;

    public MealEntry(long timestamp, String foodDescription, int satisfactionRating,
                     String eatingContext, String emotionalState, String mealType, String note) {
        this.timestamp = timestamp;
        this.foodDescription = foodDescription;
        this.satisfactionRating = satisfactionRating;
        this.eatingContext = eatingContext;
        this.emotionalState = emotionalState;
        this.mealType = mealType;
        this.note = note;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getFoodDescription() { return foodDescription; }
    public void setFoodDescription(String foodDescription) { this.foodDescription = foodDescription; }

    public int getSatisfactionRating() { return satisfactionRating; }
    public void setSatisfactionRating(int satisfactionRating) { this.satisfactionRating = satisfactionRating; }

    public String getEatingContext() { return eatingContext; }
    public void setEatingContext(String eatingContext) { this.eatingContext = eatingContext; }

    public String getEmotionalState() { return emotionalState; }
    public void setEmotionalState(String emotionalState) { this.emotionalState = emotionalState; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}