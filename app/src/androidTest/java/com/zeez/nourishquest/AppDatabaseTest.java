package com.zeez.nourishquest;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.zeez.nourishquest.data.database.AppDatabase;
import com.zeez.nourishquest.data.dao.HungerLogDao;
import com.zeez.nourishquest.data.dao.MealEntryDao;
import com.zeez.nourishquest.data.dao.JournalEntryDao;
import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.data.entity.JournalEntry;
import com.zeez.nourishquest.data.entity.MealEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Instrumented integration tests for Room database DAOs.
 * Utilizes an in-memory database instance to ensure test isolation.
 */
@RunWith(AndroidJUnit4.class)
public class AppDatabaseTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase db;
    private HungerLogDao hungerLogDao;
    private MealEntryDao mealEntryDao;
    private JournalEntryDao journalEntryDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        hungerLogDao = db.hungerLogDao();
        mealEntryDao = db.mealEntryDao();
        journalEntryDao = db.journalEntryDao();
    }

    @After
    public void closeDb() {
        if (db != null) {
            db.close();
        }
    }

    @Test
    public void insertHungerLog_retrievedByGetRecent() throws InterruptedException {
        HungerLog log = new HungerLog(System.currentTimeMillis(), 4, "BEFORE", "test note");
        hungerLogDao.insert(log);

        List<HungerLog> result = getOrAwait(hungerLogDao.getRecentLogs(10));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getScaleLevel());
    }

    @Test
    public void insertMultipleHungerLogs_retrievedInDescendingTimestampOrder() throws InterruptedException {
        long base = System.currentTimeMillis();
        hungerLogDao.insert(new HungerLog(base, 3, "BEFORE", "first"));
        hungerLogDao.insert(new HungerLog(base + 100, 7, "AFTER", "second"));

        List<HungerLog> result = getOrAwait(hungerLogDao.getRecentLogs(10));

        assertEquals(2, result.size());
        assertEquals("second", result.get(0).getNote());
    }

    @Test
    public void insertMealEntry_allFieldsPreserved() throws InterruptedException {
        // Added 7th argument (Meal Type) to match the MealEntry entity requirements
        MealEntry entry = new MealEntry(
                System.currentTimeMillis(),
                "scrambled eggs",
                8,
                "HOME",
                "CALM",
                "note",
                "BREAKFAST"
        );
        mealEntryDao.insert(entry);

        List<MealEntry> result = getOrAwait(mealEntryDao.getAllMeals());

        assertEquals(1, result.size());
        assertEquals("scrambled eggs", result.get(0).getFoodDescription());
    }

    @Test
    public void getMealsForDay_returnsOnlyTodaysMeals() throws InterruptedException {
        long now = System.currentTimeMillis();
        long yesterday = now - 86400000L;

        mealEntryDao.insert(new MealEntry(now, "today", 7, "HOME", "CALM", "", "LUNCH"));
        mealEntryDao.insert(new MealEntry(yesterday, "yesterday", 5, "HOME", "CALM", "", "DINNER"));

        long startOfDay = now - (now % 86400000L);
        long endOfDay = startOfDay + 86400000L;

        List<MealEntry> result = getOrAwait(mealEntryDao.getMealsForDay(startOfDay, endOfDay));
        assertEquals(1, result.size());
    }

    @Test
    public void insertJournalEntry_retrievedCorrectly() throws InterruptedException {
        JournalEntry entry = new JournalEntry(
                System.currentTimeMillis(),
                "tense",
                "challenge",
                "gratitude",
                "GOOD"
        );
        journalEntryDao.insert(entry);

        List<JournalEntry> result = getOrAwait(journalEntryDao.getAllEntries());
        assertEquals(1, result.size());
        assertEquals("GOOD", result.get(0).getMood());
    }

    /**
     * Helper to retrieve LiveData values synchronously for testing.
     */
    private <T> T getOrAwait(LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        liveData.observeForever(value -> {
            data[0] = value;
            latch.countDown();
        });
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new AssertionError("LiveData emission timeout");
        }
        return (T) data[0];
    }
}