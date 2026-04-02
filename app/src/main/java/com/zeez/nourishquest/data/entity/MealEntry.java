package com.zeez.nourishquest.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Stores a single meal log entry.
// Deliberately no calorie field — satisfaction and context are the focus.
// mealType added in DB v2 — nullable so old rows aren't broken.
@Entity(tableName = "meal_entries")
public class MealEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // Free-text description, no portion sizes required
    @ColumnInfo(name = "food_description")
    private String foodDescription;

    // 1 (unsatisfying) to 10 (deeply satisfying)
    @ColumnInfo(name = "satisfaction_rating")
    private int satisfactionRating;

    // HOME, RESTAURANT, DESK, CAR, SOCIAL, OTHER
    @ColumnInfo(name = "eating_context")
    private String eatingContext;

    // CALM, HAPPY, STRESSED, ANXIOUS, BORED, SAD, RUSHED, OTHER
    @ColumnInfo(name = "emotional_state")
    private String emotionalState;

    // BREAKFAST, LUNCH, DINNER, SNACK — nullable (added in v2)
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
