package com.zeez.nourishquest.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.zeez.nourishquest.data.dao.HungerLogDao;
import com.zeez.nourishquest.data.database.AppDatabase;
import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.util.DateUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Sits between the ViewModel and the Room DAO.
// Handles threading — all DB writes happen off the main thread.
// Single-thread executor keeps writes sequential, which matters for streak timestamp ordering.
public class HungerRepository {

    private final HungerLogDao dao;
    private final ExecutorService writeExecutor;

    public HungerRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.hungerLogDao();
        // Single thread (not cached pool) to preserve insert order
        writeExecutor = Executors.newSingleThreadExecutor();
    }

    public void insert(HungerLog log) {
        writeExecutor.execute(() -> dao.insert(log));
    }

    public void deleteById(long id) {
        writeExecutor.execute(() -> dao.deleteById(id));
    }

    public LiveData<List<HungerLog>> getRecentLogs(int limit) {
        return dao.getRecentLogs(limit);
    }

    public LiveData<List<HungerLog>> getTodaysLogs() {
        long[] range = DateUtils.getTodayRange();
        return dao.getLogsForDay(range[0], range[1]);
    }

    // Counts consecutive days with at least one check-in, looking back up to lookbackDays.
    // Stops counting as soon as a day with zero check-ins is found.
    // Must run on a background thread — caller is responsible.
    public int calculateStreakDays(int lookbackDays) {
        int streak = 0;
        long now = System.currentTimeMillis();
        for (int i = 0; i < lookbackDays; i++) {
            long[] range = DateUtils.getDayRange(now - (long) i * DateUtils.ONE_DAY_MS);
            int count = dao.getCheckInCountForDay(range[0], range[1]);
            if (count > 0) {
                streak++;
            } else if (i > 0) {
                // Gap found — streak is broken
                break;
            }
        }
        return streak;
    }
}
