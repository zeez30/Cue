package com.zeez.nourishquest;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.zeez.nourishquest.data.dao.HungerLogDao;
import com.zeez.nourishquest.data.dao.MealEntryDao;
import com.zeez.nourishquest.data.dao.JournalEntryDao;
import com.zeez.nourishquest.data.database.AppDatabase;
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
 * Instrumented integration tests for the Room DAOs.
 *
 * These tests require an Android device or emulator because Room uses
 * SQLite, which is not available in the JVM unit test environment.
 *
 * An in-memory database is used so:
 *  - Tests are fully isolated (no leftover state from previous runs)
 *  - The device's filesystem is not written to
 *  - Teardown is instant (no DELETE FROM * needed)
 *
 * InstantTaskExecutorRule makes LiveData execute synchronously on the
 * test thread, eliminating the need for Thread.sleep() or
 * CountDownLatch in most cases.
 *
 * Testing strategy: one "happy path" test per DAO covering insert + query
 * round-trip. Edge cases (empty results, deletion, aggregate queries) are
 * covered by the JVM unit tests where possible.
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
                .allowMainThreadQueries()  // acceptable in tests only
                .build();
        hungerLogDao  = db.hungerLogDao();
        mealEntryDao  = db.mealEntryDao();
        journalEntryDao = db.journalEntryDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    // ---- HungerLog DAO tests ----

    @Test
    public void insertHungerLog_retrievedByGetRecent() throws InterruptedException {
        HungerLog log = new HungerLog(System.currentTimeMillis(), 4, "BEFORE", "test note");
        hungerLogDao.insert(log);

        List<HungerLog> result = getOrAwait(hungerLogDao.getRecentLogs(10));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getScaleLevel());
        assertEquals("BEFORE", result.get(0).getMealPhase());
        assertEquals("test note", result.get(0).getNote());
    }

    @Test
    public void insertMultipleHungerLogs_retrievedInDescendingTimestampOrder()
            throws InterruptedException {
        long base = System.currentTimeMillis();
        hungerLogDao.insert(new HungerLog(base,      3, "BEFORE", "first"));
        hungerLogDao.insert(new HungerLog(base + 100, 7, "AFTER",  "second"));
        hungerLogDao.insert(new HungerLog(base + 200, 5, "BEFORE", "third"));

        List<HungerLog> result = getOrAwait(hungerLogDao.getRecentLogs(10));

        assertEquals(3, result.size());
        // Descending order — most recent first
        assertEquals("third",  result.get(0).getNote());
        assertEquals("second", result.get(1).getNote());
        assertEquals("first",  result.get(2).getNote());
    }

    @Test
    public void deleteHungerLog_removedFromResults() throws InterruptedException {
        HungerLog log = new HungerLog(System.currentTimeMillis(), 6, "AFTER", "");
        long id = hungerLogDao.insert(log);

        hungerLogDao.deleteById(id);

        List<HungerLog> result = getOrAwait(hungerLogDao.getRecentLogs(10));
        assertTrue(result.isEmpty());
    }

    // ---- MealEntry DAO tests ----

    @Test
    public void insertMealEntry_allFieldsPreserved() throws InterruptedException {
        MealEntry entry = new MealEntry(
                System.currentTimeMillis(),
                "scrambled eggs on toast",
                8,
                "HOME",
                "CALM",
                "weekend breakfast"
        );
        mealEntryDao.insert(entry);

        List<MealEntry> result = getOrAwait(mealEntryDao.getAllMeals());

        assertEquals(1, result.size());
        MealEntry retrieved = result.get(0);
        assertEquals("scrambled eggs on toast", retrieved.getFoodDescription());
        assertEquals(8, retrieved.getSatisfactionRating());
        assertEquals("HOME", retrieved.getEatingContext());
        assertEquals("CALM", retrieved.getEmotionalState());
        assertEquals("weekend breakfast", retrieved.getNote());
    }

    @Test
    public void getMealsForDay_returnsOnlyTodaysMeals() throws InterruptedException {
        long now = System.currentTimeMillis();
        long yesterday = now - 24 * 60 * 60 * 1000L;

        // Insert one meal today, one yesterday
        mealEntryDao.insert(new MealEntry(now, "today meal", 7, "HOME", "HAPPY", ""));
        mealEntryDao.insert(new MealEntry(yesterday, "yesterday meal", 5, "DESK", "CALM", ""));

        // Today's range
        long startOfDay = now - (now % (24 * 60 * 60 * 1000L));
        long endOfDay   = startOfDay + 24 * 60 * 60 * 1000L;

        List<MealEntry> result = getOrAwait(
                mealEntryDao.getMealsForDay(startOfDay, endOfDay)
        );

        assertEquals(1, result.size());
        assertEquals("today meal", result.get(0).getFoodDescription());
    }

    // ---- JournalEntry DAO tests ----

    @Test
    public void insertJournalEntry_retrievedCorrectly() throws InterruptedException {
        JournalEntry entry = new JournalEntry(
                System.currentTimeMillis(),
                "Shoulders tense, stomach settled",
                "Thought I shouldn't eat dessert → I enjoyed it and that's fine",
                "My legs carried me all day",
                "GOOD"
        );
        journalEntryDao.insert(entry);

        List<JournalEntry> result = getOrAwait(journalEntryDao.getAllEntries());

        assertEquals(1, result.size());
        JournalEntry retrieved = result.get(0);
        assertEquals("GOOD", retrieved.getMood());
        assertEquals("My legs carried me all day", retrieved.getBodyGratitude());
    }

    @Test
    public void insertJournalEntry_recentEntriesLimitRespected() throws InterruptedException {
        long base = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            journalEntryDao.insert(new JournalEntry(
                    base + i * 1000L, "body " + i, "challenge " + i, "gratitude " + i, "OKAY"
            ));
        }

        List<JournalEntry> result = getOrAwait(journalEntryDao.getRecentEntries(3));
        assertEquals(3, result.size());
    }

    // ---- Helper: synchronously get LiveData value ----

    private <T> T getOrAwait(LiveData<T> liveData) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        liveData.observeForever(value -> {
            data[0] = value;
            latch.countDown();
        });
        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new AssertionError("LiveData never emitted a value within 2 seconds");
        }
        //noinspection unchecked
        return (T) data[0];
    }
}
