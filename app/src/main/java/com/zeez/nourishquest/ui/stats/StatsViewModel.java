package com.zeez.nourishquest.ui.stats;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.data.repository.HungerRepository;
import com.zeez.nourishquest.data.repository.MealRepository;
import com.zeez.nourishquest.util.DateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatsViewModel extends AndroidViewModel {

    private final HungerRepository hungerRepo;
    private final MealRepository mealRepo;
    private final ExecutorService executor;

    // Observable data for trend charts and frequency distribution
    private final MutableLiveData<float[]> weeklyHungerAverages = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Integer>> emotionFrequency = new MutableLiveData<>();
    private final MutableLiveData<float[]> weeklySatisfactionTrend = new MutableLiveData<>();

    private final LiveData<List<HungerLog>> recentHungerLogs;
    private final LiveData<List<MealEntry>> recentMeals;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        hungerRepo = new HungerRepository(application);
        mealRepo = new MealRepository(application);

        // Cached thread pool to handle concurrent statistical computations
        executor = Executors.newCachedThreadPool();

        // Limit data retrieval to ensure performance during processing
        recentHungerLogs = hungerRepo.getRecentLogs(500);
        recentMeals = mealRepo.getRecentMeals(500);
    }

    /**
     * Triggers data processing on a background thread to avoid blocking the UI.
     * Computes averages and frequencies in parallel.
     */
    public void computeStats(List<HungerLog> hungerLogs, List<MealEntry> meals) {
        executor.execute(() -> {
            weeklyHungerAverages.postValue(computeWeeklyHungerAverages(hungerLogs));
            weeklySatisfactionTrend.postValue(computeWeeklySatisfactionTrend(meals));
            emotionFrequency.postValue(computeEmotionFrequency(meals));
        });
    }

    // Aggregates hunger levels into a 7-day chronological array
    private float[] computeWeeklyHungerAverages(List<HungerLog> logs) {
        float[] totals = new float[7];
        int[] counts = new int[7];
        long now = System.currentTimeMillis();

        for (HungerLog log : logs) {
            int dayIndex = (int) ((now - log.getTimestamp()) / DateUtils.ONE_DAY_MS);
            if (dayIndex >= 0 && dayIndex < 7) {
                int bucket = 6 - dayIndex; // Align oldest data to index 0
                totals[bucket] += log.getScaleLevel();
                counts[bucket]++;
            }
        }

        float[] averages = new float[7];
        for (int i = 0; i < 7; i++) {
            averages[i] = counts[i] > 0 ? totals[i] / counts[i] : 0f;
        }
        return averages;
    }

    // Calculates a 7-day trend for meal satisfaction scores
    private float[] computeWeeklySatisfactionTrend(List<MealEntry> meals) {
        float[] totals = new float[7];
        int[] counts = new int[7];
        long now = System.currentTimeMillis();

        for (MealEntry meal : meals) {
            int dayIndex = (int) ((now - meal.getTimestamp()) / DateUtils.ONE_DAY_MS);
            if (dayIndex >= 0 && dayIndex < 7) {
                int bucket = 6 - dayIndex;
                totals[bucket] += meal.getSatisfactionRating();
                counts[bucket]++;
            }
        }

        float[] averages = new float[7];
        for (int i = 0; i < 7; i++) {
            averages[i] = counts[i] > 0 ? totals[i] / counts[i] : 0f;
        }
        return averages;
    }

    // Generates a distribution map of emotional states recorded in meals
    private Map<String, Integer> computeEmotionFrequency(List<MealEntry> meals) {
        Map<String, Integer> freq = new HashMap<>();
        for (MealEntry meal : meals) {
            String emotion = meal.getEmotionalState();
            if (emotion == null || emotion.isEmpty()) continue;
            freq.put(emotion, freq.getOrDefault(emotion, 0) + 1);
        }
        return freq;
    }

    // Getters for UI observation
    public LiveData<float[]> getWeeklyHungerAverages() { return weeklyHungerAverages; }
    public LiveData<float[]> getWeeklySatisfactionTrend() { return weeklySatisfactionTrend; }
    public LiveData<Map<String, Integer>> getEmotionFrequency() { return emotionFrequency; }
    public LiveData<List<HungerLog>> getRecentHungerLogs() { return recentHungerLogs; }
    public LiveData<List<MealEntry>> getRecentMeals() { return recentMeals; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}