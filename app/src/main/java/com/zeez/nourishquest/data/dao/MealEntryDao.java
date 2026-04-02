package com.zeez.nourishquest.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zeez.nourishquest.data.entity.MealEntry;

import java.util.List;

@Dao
public interface MealEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MealEntry entry);

    // Full meal history — used on the meal log screen
    @Query("SELECT * FROM meal_entries ORDER BY timestamp DESC")
    LiveData<List<MealEntry>> getAllMeals();

    // Limited list — used on home screen recent meals indicator
    @Query("SELECT * FROM meal_entries ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<MealEntry>> getRecentMeals(int limit);

    // Today's meals only
    @Query("SELECT * FROM meal_entries WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC")
    LiveData<List<MealEntry>> getMealsForDay(long startOfDay, long endOfDay);

    // 7-day average satisfaction — shown on home screen
    @Query("SELECT AVG(satisfaction_rating) FROM meal_entries WHERE timestamp >= :since")
    LiveData<Float> getAverageSatisfactionSince(long since);

    @Query("DELETE FROM meal_entries WHERE id = :id")
    void deleteById(long id);
}
