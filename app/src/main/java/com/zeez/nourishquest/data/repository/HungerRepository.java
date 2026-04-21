package com.zeez.nourishquest.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.zeez.nourishquest.data.database.AppDatabase;
import com.zeez.nourishquest.data.dao.HungerLogDao;
import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.util.DateUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HungerRepository {

    private final HungerLogDao dao;
    private final ExecutorService writeExecutor;

    public HungerRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.hungerLogDao();

        // Use a single thread executor to maintain sequential write order
        writeExecutor = Executors.newSingleThreadExecutor();
    }

    // Database insertion executed on background thread
    public void insert(HungerLog log) {
        writeExecutor.execute(() -> dao.insert(log));
    }

    // Deletion by primary key
    public void deleteById(long id) {
        writeExecutor.execute(() -> dao.deleteById(id));
    }

    // Returns LiveData for automatic UI updates
    public LiveData<List<HungerLog>> getRecentLogs(int limit) {
        return dao.getRecentLogs(limit);
    }

    // Retrieves logs specific to the current calendar day
    public LiveData<List<HungerLog>> getTodaysLogs() {
        long[] range = DateUtils.getTodayRange();
        return dao.getLogsForDay(range[0], range[1]);
    }

    /**
     * Logic for calculating daily check-in streaks.
     * Iterates backward through days and breaks when a gap is found.
     * Note: Should be called from a background thread.
     */
    public int calculateStreakDays(int lookbackDays) {
        int streak = 0;
        long now = System.currentTimeMillis();

        for (int i = 0; i < lookbackDays; i++) {
            long[] range = DateUtils.getDayRange(now - (long) i * DateUtils.ONE_DAY_MS);
            int count = dao.getCheckInCountForDay(range[0], range[1]);

            if (count > 0) {
                streak++;
            } else if (i > 0) {
                // Streak breaks if a gap is detected after the first day
                break;
            }
        }
        return streak;
    }
}