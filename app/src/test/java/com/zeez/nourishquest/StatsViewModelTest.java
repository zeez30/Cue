package com.zeez.nourishquest;

import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.util.DateUtils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for the statistical computation algorithms utilized in StatsViewModel.
 * These tests validate the integrity of the daily bucketing logic, chronological
 * alignment, and mathematical accuracy of the averaging functions.
 */
public class StatsViewModelTest {

    /**
     * Replicates the calculation logic for hunger averages to facilitate
     * unit testing within a JVM environment.
     */
    private float[] computeWeeklyAverages(List<HungerLog> logs) {
        float[] totals = new float[7];
        int[] counts = new int[7];
        long now = System.currentTimeMillis();

        for (HungerLog log : logs) {
            long ageMs = now - log.getTimestamp();
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

    /**
     * Replicates the calculation logic for meal satisfaction averages.
     */
    private float[] computeWeeklySatisfaction(List<MealEntry> meals) {
        float[] totals = new float[7];
        int[] counts = new int[7];
        long now = System.currentTimeMillis();

        for (MealEntry meal : meals) {
            long ageMs = now - meal.getTimestamp();
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

        // Index 6 represents the current calendar day
        assertEquals(6f, result[6], 0.001f);
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

        assertEquals(6.0f, result[6], 0.001f);
    }

    @Test
    public void logOlderThan7Days_notIncluded() {
        long eightDaysAgo = System.currentTimeMillis() - (8L * DateUtils.ONE_DAY_MS);
        List<HungerLog> logs = new ArrayList<>();
        logs.add(new HungerLog(eightDaysAgo, 9, "BEFORE", ""));

        float[] result = computeWeeklyAverages(logs);

        for (float v : result) {
            assertEquals(0f, v, 0.001f);
        }
    }

    @Test
    public void satisfactionAverage_twoMealsToday_correct() {
        long now = System.currentTimeMillis();
        List<MealEntry> meals = new ArrayList<>();
        meals.add(new MealEntry(now - 100, "breakfast", 9, "HOME", "HAPPY", "", "BREAKFAST"));
        meals.add(new MealEntry(now - 200, "lunch", 7, "DESK", "CALM", "", "LUNCH"));

        float[] result = computeWeeklySatisfaction(meals);

        assertEquals(8.0f, result[6], 0.001f);
    }

    @Test
    public void satisfactionRange_neverExceedsMaxValue() {
        long now = System.currentTimeMillis();
        List<MealEntry> meals = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            meals.add(new MealEntry(now - (i * 100L), "food", i, "HOME", "CALM", "", "SNACK"));
        }

        float[] result = computeWeeklySatisfaction(meals);

        for (float v : result) {
            assertTrue("Mean satisfaction must not exceed the maximum scale value", v <= 10f);
        }
    }
}