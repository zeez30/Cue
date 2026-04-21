package com.zeez.nourishquest.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.zeez.nourishquest.data.database.AppDatabase;
import com.zeez.nourishquest.data.dao.JournalEntryDao;
import com.zeez.nourishquest.data.entity.JournalEntry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalRepository {

    private final JournalEntryDao dao;
    private final ExecutorService writeExecutor;

    public JournalRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.journalEntryDao();

        // Single thread executor to handle background database writes
        writeExecutor = Executors.newSingleThreadExecutor();
    }

    // Persist a new journal entry to the local database
    public void insert(JournalEntry entry) {
        writeExecutor.execute(() -> dao.insert(entry));
    }

    // Remove a specific entry by its unique identifier
    public void deleteById(long id) {
        writeExecutor.execute(() -> dao.deleteById(id));
    }

    // Retrieve all journal entries as observable LiveData
    public LiveData<List<JournalEntry>> getAllEntries() {
        return dao.getAllEntries();
    }

    // Fetch a subset of entries based on a specific limit
    public LiveData<List<JournalEntry>> getRecentEntries(int limit) {
        return dao.getRecentEntries(limit);
    }
}