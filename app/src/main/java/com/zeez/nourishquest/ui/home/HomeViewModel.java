package com.zeez.nourishquest.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.data.repository.HungerRepository;
import com.zeez.nourishquest.data.repository.MealRepository;
import com.zeez.nourishquest.util.AffirmationProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Provides data for the home screen.
// The streak calculation runs on a background thread because it queries the DB synchronously.
public class HomeViewModel extends AndroidViewModel {

    private final HungerRepository hungerRepository;
    private final MealRepository mealRepository;
    private final ExecutorService executor;

    private final MutableLiveData<String> dailyAffirmation = new MutableLiveData<>();
    private final MutableLiveData<Integer> checkInStreak = new MutableLiveData<>(0);
    private final LiveData<List<MealEntry>> recentMeals;
    private final LiveData<Float> weeklySatisfactionAverage;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        hungerRepository = new HungerRepository(application);
        mealRepository = new MealRepository(application);
        executor = Executors.newSingleThreadExecutor();

        recentMeals = mealRepository.getRecentMeals(3);
        weeklySatisfactionAverage = mealRepository.getWeeklyAverageSatisfaction();

        dailyAffirmation.setValue(AffirmationProvider.getDailyAffirmation());
        calculateStreak();
    }

    // Streak runs on background thread — looks back 30 days
    public void calculateStreak() {
        executor.execute(() -> {
            int streak = hungerRepository.calculateStreakDays(30);
            checkInStreak.postValue(streak);
        });
    }

    public LiveData<String> getDailyAffirmation() { return dailyAffirmation; }
    public LiveData<Integer> getCheckInStreak() { return checkInStreak; }
    public LiveData<List<MealEntry>> getRecentMeals() { return recentMeals; }
    public LiveData<Float> getWeeklySatisfactionAverage() { return weeklySatisfactionAverage; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
