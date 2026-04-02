package com.zeez.nourishquest.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.zeez.nourishquest.data.entity.JournalEntry;

import java.util.List;

@Dao
public interface JournalEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(JournalEntry entry);

    // All entries newest first — used on the journal history list
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC")
    LiveData<List<JournalEntry>> getAllEntries();

    // Limited list for widgets or home screen use
    @Query("SELECT * FROM journal_entries ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<JournalEntry>> getRecentEntries(int limit);

    // Count check — used to see if user journalled today for streak tracking
    @Query("SELECT COUNT(*) FROM journal_entries WHERE timestamp >= :startOfDay AND timestamp < :endOfDay")
    int getEntryCountForDay(long startOfDay, long endOfDay);

    @Query("DELETE FROM journal_entries WHERE id = :id")
    void deleteById(long id);
}
