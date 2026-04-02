package com.zeez.nourishquest.ui.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zeez.nourishquest.data.entity.JournalEntry;
import com.zeez.nourishquest.data.repository.JournalRepository;
import com.zeez.nourishquest.util.SingleLiveEvent;

import java.util.List;

// ViewModel for the body journal screen.
public class JournalViewModel extends AndroidViewModel {

    private final JournalRepository repository;

    private final MutableLiveData<String> selectedMood = new MutableLiveData<>("OKAY");
    // Fires once on save to drive the confirmation Toast
    private final SingleLiveEvent<Boolean> saveResult = new SingleLiveEvent<>();
    private final LiveData<List<JournalEntry>> allEntries;

    public JournalViewModel(@NonNull Application application) {
        super(application);
        repository = new JournalRepository(application);
        allEntries = repository.getAllEntries();
    }

    public void setSelectedMood(String mood) { selectedMood.setValue(mood); }
    public LiveData<String> getSelectedMood() { return selectedMood; }
    public LiveData<Boolean> getSaveResult() { return saveResult; }
    public LiveData<List<JournalEntry>> getAllEntries() { return allEntries; }

    public void saveEntry(String bodyFeeling, String challengedThought,
                          String bodyGratitude, String mood) {
        repository.insert(new JournalEntry(
                System.currentTimeMillis(), bodyFeeling, challengedThought, bodyGratitude, mood));
        saveResult.setValue(true);
    }

    public void deleteEntry(long id) {
        repository.deleteById(id);
    }
}
