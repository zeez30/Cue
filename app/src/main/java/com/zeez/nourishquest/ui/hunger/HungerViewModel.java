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

// ViewModel for the hunger check-in screen.
// Survives rotation — the user never loses a partially built check-in.
// saveResult uses SingleLiveEvent so the confirmation Toast only fires once.
public class HungerViewModel extends AndroidViewModel {

    private final HungerRepository repository;

    // Currently selected position on the 1–10 scale
    private final MutableLiveData<Integer> selectedLevel = new MutableLiveData<>(5);
    // Fires once when a save completes — drives the Toast in HungerFragment
    private final SingleLiveEvent<Boolean> saveResult = new SingleLiveEvent<>();
    private final LiveData<List<HungerLog>> todaysLogs;

    public HungerViewModel(@NonNull Application application) {
        super(application);
        repository = new HungerRepository(application);
        todaysLogs = repository.getTodaysLogs();
    }

    public void setSelectedLevel(int level) { selectedLevel.setValue(level); }

    public LiveData<Integer> getSelectedLevel() { return selectedLevel; }
    public LiveData<Boolean> getSaveResult() { return saveResult; }
    public LiveData<List<HungerLog>> getTodaysLogs() { return todaysLogs; }

    public void saveCheckIn(int level, String phase, String note) {
        repository.insert(new HungerLog(System.currentTimeMillis(), level, phase, note));
        saveResult.setValue(true);
    }

    public void deleteLog(long id) {
        repository.deleteById(id);
    }
}
