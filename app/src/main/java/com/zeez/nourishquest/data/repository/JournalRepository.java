package com.zeez.nourishquest.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.zeez.nourishquest.data.dao.JournalEntryDao;
import com.zeez.nourishquest.data.database.AppDatabase;
import com.zeez.nourishquest.data.entity.JournalEntry;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Repository for journal entries — abstracts DB access from the ViewModel.
public class JournalRepository {

    private final JournalEntryDao dao;
    private final ExecutorService writeExecutor;

    public JournalRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.journalEntryDao();
        writeExecutor = Executors.newSingleThreadExecutor();
    }

    public void insert(JournalEntry entry) {
        writeExecutor.execute(() -> dao.insert(entry));
    }

    public void deleteById(long id) {
        writeExecutor.execute(() -> dao.deleteById(id));
    }

    public LiveData<List<JournalEntry>> getAllEntries() {
        return dao.getAllEntries();
    }

    public LiveData<List<JournalEntry>> getRecentEntries(int limit) {
        return dao.getRecentEntries(limit);
    }
}
