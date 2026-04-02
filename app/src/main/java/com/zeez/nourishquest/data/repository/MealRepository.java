package com.zeez.nourishquest.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.zeez.nourishquest.data.dao.MealEntryDao;
import com.zeez.nourishquest.data.database.AppDatabase;
import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.util.DateUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Repository for meal entries — abstracts DB access from the ViewModel.
public class MealRepository {

    private final MealEntryDao dao;
    private final ExecutorService writeExecutor;

    public MealRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.mealEntryDao();
        writeExecutor = Executors.newSingleThreadExecutor();
    }

    public void insert(MealEntry entry) {
        writeExecutor.execute(() -> dao.insert(entry));
    }

    public void deleteById(long id) {
        writeExecutor.execute(() -> dao.deleteById(id));
    }

    public LiveData<List<MealEntry>> getAllMeals() {
        return dao.getAllMeals();
    }

    public LiveData<List<MealEntry>> getRecentMeals(int limit) {
        return dao.getRecentMeals(limit);
    }

    public LiveData<List<MealEntry>> getTodaysMeals() {
        long[] range = DateUtils.getTodayRange();
        return dao.getMealsForDay(range[0], range[1]);
    }

    // Used on the home screen to show 7-day satisfaction trend
    public LiveData<Float> getWeeklyAverageSatisfaction() {
        long sevenDaysAgo = System.currentTimeMillis() - (7L * DateUtils.ONE_DAY_MS);
        return dao.getAverageSatisfactionSince(sevenDaysAgo);
    }
}
