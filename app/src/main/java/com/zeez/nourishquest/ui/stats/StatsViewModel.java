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

// ViewModel for the Insights screen.
// All three calculations run on a background thread — they iterate over potentially
// hundreds of records and would block the main thread if run inline.
//
// Array indexing: index 0 = 6 days ago, index 6 = today (left to right on the chart).
public class StatsViewModel extends AndroidViewModel {

    private final HungerRepository hungerRepo;
    private final MealRepository mealRepo;
    private final ExecutorService executor;

    // 7 daily averages for the hunger trend chart
    private final MutableLiveData<float[]> weeklyHungerAverages = new MutableLiveData<>();
    // Emotion label → count of meals logged with that emotion
    private final MutableLiveData<Map<String, Integer>> emotionFrequency = new MutableLiveData<>();
    // 7 daily averages for the satisfaction trend chart
    private final MutableLiveData<float[]> weeklySatisfactionTrend = new MutableLiveData<>();

    // Raw data — fetched once, passed to compute methods
    private final LiveData<List<HungerLog>> recentHungerLogs;
    private final LiveData<List<MealEntry>> recentMeals;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        hungerRepo = new HungerRepository(application);
        mealRepo = new MealRepository(application);
        executor = Executors.newCachedThreadPool();

        // Pull up to 500 records — enough for meaningful trends without loading everything
        recentHungerLogs = hungerRepo.getRecentLogs(500);
        recentMeals = mealRepo.getRecentMeals(500);
    }

    // Runs all three calculations in parallel on a background thread
    public void computeStats(List<HungerLog> hungerLogs, List<MealEntry> meals) {
        executor.execute(() -> {
            weeklyHungerAverages.postValue(computeWeeklyHungerAverages(hungerLogs));
            weeklySatisfactionTrend.postValue(computeWeeklySatisfactionTrend(meals));
            emotionFrequency.postValue(computeEmotionFrequency(meals));
        });
    }

    // Bins hunger logs into 7 daily buckets and returns the average per bucket.
    // 0.0 means no data for that day.
    private float[] computeWeeklyHungerAverages(List<HungerLog> logs) {
        float[] totals = new float[7];
        int[] counts = new int[7];
        long now = System.currentTimeMillis();

        for (HungerLog log : logs) {
            int dayIndex = (int) ((now - log.getTimestamp()) / DateUtils.ONE_DAY_MS);
            if (dayIndex >= 0 && dayIndex < 7) {
                int bucket = 6 - dayIndex; // flip so index 0 = oldest, 6 = today
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

    // Same bucketing logic as hunger, but for meal satisfaction ratings
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

    // Counts how many times each emotional state appeared across all logged meals
    private Map<String, Integer> computeEmotionFrequency(List<MealEntry> meals) {
        Map<String, Integer> freq = new HashMap<>();
        for (MealEntry meal : meals) {
            String emotion = meal.getEmotionalState();
            if (emotion == null || emotion.isEmpty()) continue;
            freq.put(emotion, freq.getOrDefault(emotion, 0) + 1);
        }
        return freq;
    }

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
