package com.zeez.nourishquest.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zeez.nourishquest.data.entity.HungerLog;

import java.util.List;

@Dao
public interface HungerLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(HungerLog log);

    // Most recent logs first, limited to avoid loading everything
    @Query("SELECT * FROM hunger_logs ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<HungerLog>> getRecentLogs(int limit);

    // Today's check-ins — used on the hunger screen history list
    @Query("SELECT * FROM hunger_logs WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp DESC")
    LiveData<List<HungerLog>> getLogsForDay(long startOfDay, long endOfDay);

    // Returns a count — used by the streak calculator to check if a day has any check-ins
    @Query("SELECT COUNT(*) FROM hunger_logs WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    int getCheckInCountForDay(long startOfDay, long endOfDay);

    @Query("DELETE FROM hunger_logs WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM hunger_logs")
    void deleteAll();
}
