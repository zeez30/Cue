package com.zeez.nourishquest.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.zeez.nourishquest.data.database.AppDatabase;
import com.zeez.nourishquest.data.dao.MealEntryDao;
import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.util.DateUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MealRepository {

    private final MealEntryDao dao;
    private final ExecutorService writeExecutor;

    public MealRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.mealEntryDao();

        // Background executor to handle database write operations
        writeExecutor = Executors.newSingleThreadExecutor();
    }

    // Insert a new meal record into the database
    public void insert(MealEntry entry) {
        writeExecutor.execute(() -> dao.insert(entry));
    }

    // Delete a specific meal entry using its ID
    public void deleteById(long id) {
        writeExecutor.execute(() -> dao.deleteById(id));
    }

    // Retrieve all meals recorded in the database
    public LiveData<List<MealEntry>> getAllMeals() {
        return dao.getAllMeals();
    }

    // Fetch a specific number of recent meal entries
    public LiveData<List<MealEntry>> getRecentMeals(int limit) {
        return dao.getRecentMeals(limit);
    }

    // Retrieves meal data for the current date range
    public LiveData<List<MealEntry>> getTodaysMeals() {
        long[] range = DateUtils.getTodayRange();
        return dao.getMealsForDay(range[0], range[1]);
    }

    /**
     * Calculates the average satisfaction score over the last seven days.
     * This data supports the trend visualization on the dashboard.
     */
    public LiveData<Float> getWeeklyAverageSatisfaction() {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * DateUtils.ONE_DAY_MS);
        return dao.getAverageSatisfactionSince(sevenDaysAgo);
    }
}