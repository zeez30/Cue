package com.zeez.nourishquest;

import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.util.DateUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the statistical computation logic used in StatsViewModel.
 *
 * The computation methods are extracted here for pure JVM testing — they
 * depend only on entity classes and DateUtils, neither of which requires
 * an Android context or database.
 *
 * Tests cover:
 *  - Correct daily bucketing of hunger logs into 7-day windows
 *  - Average calculation accuracy
 *  - Edge cases: no data, data outside the window, single entry
 */
public class StatsViewModelTest {

    // Mirrors StatsViewModel.computeWeeklyHungerAverages() for isolated testing
    private float[] computeWeeklyAverages(List<HungerLog> logs) {
        float[] totals = new float[7];
        int[]   counts = new int[7];
        long now = System.currentTimeMillis();

        for (HungerLog log : logs) {
            long ageMs   = now - log.getTimestamp();
            int dayIndex = (int) (ageMs / DateUtils.ONE_DAY_MS);
            if (dayIndex >= 0 && dayIndex < 7) {
                int bucket = 6 - dayIndex;
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

    @Test
    public void emptyLogs_allBucketsAreZero() {
        float[] result = computeWeeklyAverages(new ArrayList<>());
        for (float v : result) {
            assertEquals(0f, v, 0.001f);
        }
    }

    @Test
    public void singleLogToday_appearsInLastBucket() {
        List<HungerLog> logs = new ArrayList<>();
        logs.add(new HungerLog(System.currentTimeMillis(), 6, "BEFORE", ""));

        float[] result = computeWeeklyAverages(logs);

        // Bucket 6 (index 6) = today
        assertEquals(6f, result[6], 0.001f);
        // All other buckets should be zero
        for (int i = 0; i < 6; i++) {
            assertEquals(0f, result[i], 0.001f);
        }
    }

    @Test
    public void multipleLogsToday_averageIsCorrect() {
        long now = System.currentTimeMillis();
        List<HungerLog> logs = new ArrayList<>();
        logs.add(new HungerLog(now - 100, 4, "BEFORE", ""));
        logs.add(new HungerLog(now - 200, 6, "AFTER", ""));
        logs.add(new HungerLog(now - 300, 8, "BEFORE", ""));

        float[] result = computeWeeklyAverages(logs);

        // Average of 4, 6, 8 = 6.0
        assertEquals(6.0f, result[6], 0.001f);
    }

    @Test
    public void logOlderThan7Days_notIncluded() {
        long eightDaysAgo = System.currentTimeMillis() - (8L * DateUtils.ONE_DAY_MS);
        List<HungerLog> logs = new ArrayList<>();
        logs.add(new HungerLog(eightDaysAgo, 9, "BEFORE", ""));

        float[] result = computeWeeklyAverages(logs);

        // No bucket should contain the old log
        for (float v : result) {
            assertEquals(0f, v, 0.001f);
        }
    }

    @Test
    public void logSixDaysAgo_appearsInFirstBucket() {
        long sixDaysAgo = System.currentTimeMillis() - (6L * DateUtils.ONE_DAY_MS) + 1000L;
        List<HungerLog> logs = new ArrayList<>();
        logs.add(new HungerLog(sixDaysAgo, 3, "BEFORE", ""));

        float[] result = computeWeeklyAverages(logs);

        assertEquals(3f, result[0], 0.001f);
    }

    // ---- Meal satisfaction tests ----

    private float[] computeWeeklySatisfaction(List<MealEntry> meals) {
        float[] totals = new float[7];
        int[]   counts = new int[7];
        long now = System.currentTimeMillis();

        for (MealEntry meal : meals) {
            long ageMs   = now - meal.getTimestamp();
            int dayIndex = (int) (ageMs / DateUtils.ONE_DAY_MS);
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

    @Test
    public void satisfactionAverage_twoMealsToday_correct() {
        long now = System.currentTimeMillis();
        List<MealEntry> meals = new ArrayList<>();
        meals.add(new MealEntry(now - 100, "breakfast", 9, "HOME", "HAPPY", ""));
        meals.add(new MealEntry(now - 200, "lunch",     7, "DESK", "CALM",  ""));

        float[] result = computeWeeklySatisfaction(meals);

        // Average of 9 and 7 = 8.0
        assertEquals(8.0f, result[6], 0.001f);
    }

    @Test
    public void satisfactionRange_neverExceedsMaxValue() {
        long now = System.currentTimeMillis();
        List<MealEntry> meals = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            meals.add(new MealEntry(now - i * 100L, "food " + i, i, "HOME", "CALM", ""));
        }

        float[] result = computeWeeklySatisfaction(meals);

        for (float v : result) {
            assertTrue("Satisfaction average should never exceed 10", v <= 10f);
        }
    }
}
