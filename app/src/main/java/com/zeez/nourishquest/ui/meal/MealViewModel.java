package com.zeez.nourishquest.ui.meal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.data.repository.MealRepository;
import com.zeez.nourishquest.util.SingleLiveEvent;

import java.util.List;

// ViewModel for the meal logging screen.
// No calorie fields anywhere in this class — satisfaction and context are the metrics.
public class MealViewModel extends AndroidViewModel {

    private final MealRepository repository;

    private final MutableLiveData<Integer> satisfactionRating = new MutableLiveData<>(5);
    private final MutableLiveData<String> selectedContext = new MutableLiveData<>("HOME");
    private final MutableLiveData<String> selectedEmotion = new MutableLiveData<>("CALM");
    private final MutableLiveData<String> selectedMealType = new MutableLiveData<>("BREAKFAST");
    // Fires once on save to drive the confirmation Toast
    private final SingleLiveEvent<Boolean> saveResult = new SingleLiveEvent<>();
    private final LiveData<List<MealEntry>> allMeals;

    public MealViewModel(@NonNull Application application) {
        super(application);
        repository = new MealRepository(application);
        allMeals = repository.getAllMeals();
    }

    public void setSatisfactionRating(int rating) { satisfactionRating.setValue(rating); }
    public void setSelectedContext(String context) { selectedContext.setValue(context); }
    public void setSelectedEmotion(String emotion) { selectedEmotion.setValue(emotion); }
    public void setSelectedMealType(String mealType) { selectedMealType.setValue(mealType); }

    public LiveData<Integer> getSatisfactionRating() { return satisfactionRating; }
    public LiveData<String> getSelectedContext() { return selectedContext; }
    public LiveData<String> getSelectedEmotion() { return selectedEmotion; }
    public LiveData<String> getSelectedMealType() { return selectedMealType; }
    public LiveData<Boolean> getSaveResult() { return saveResult; }
    public LiveData<List<MealEntry>> getAllMeals() { return allMeals; }

    public void saveMeal(String description, int satisfaction,
                         String context, String emotion, String mealType, String note) {
        repository.insert(new MealEntry(
                System.currentTimeMillis(),
                description, satisfaction, context, emotion, mealType, note));
        saveResult.setValue(true);
    }

    public void deleteMeal(long id) {
        repository.deleteById(id);
    }
}
