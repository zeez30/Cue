package com.zeez.nourishquest.ui.hunger;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.data.repository.HungerRepository;
import com.zeez.nourishquest.util.SingleLiveEvent;

import java.util.List;

public class HungerViewModel extends AndroidViewModel {

    private final HungerRepository repository;

    // Default selection initialized to the neutral midpoint of the scale
    private final MutableLiveData<Integer> selectedLevel = new MutableLiveData<>(5);

    // Custom event type to ensure UI notifications trigger only once
    private final SingleLiveEvent<Boolean> saveResult = new SingleLiveEvent<>();

    private final LiveData<List<HungerLog>> todaysLogs;

    public HungerViewModel(@NonNull Application application) {
        super(application);
        repository = new HungerRepository(application);
        todaysLogs = repository.getTodaysLogs();
    }

    public void setSelectedLevel(int level) {
        selectedLevel.setValue(level);
    }

    // Getters for UI observation
    public LiveData<Integer> getSelectedLevel() { return selectedLevel; }
    public LiveData<Boolean> getSaveResult() { return saveResult; }
    public LiveData<List<HungerLog>> getTodaysLogs() { return todaysLogs; }

    /**
     * Constructs a new HungerLog entity and persists it via the repository.
     * Updates saveResult to trigger a UI confirmation.
     */
    public void saveCheckIn(int level, String phase, String note) {
        repository.insert(new HungerLog(System.currentTimeMillis(), level, phase, note));
        saveResult.setValue(true);
    }

    // Requests deletion of a specific log entry by ID
    public void deleteLog(long id) {
        repository.deleteById(id);
    }
}