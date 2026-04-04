package com.zeez.nourishquest.ui.meal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeez.nourishquest.R;
import com.zeez.nourishquest.databinding.FragmentMealBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

// Meal logging screen.
// Three Spinners replace the old horizontal chip groups — easier to tap on small screens.
// Meal type, eating context, and emotional state are all separate fields because
// they capture different things about the same meal.
public class MealFragment extends Fragment {

    private FragmentMealBinding binding;
    private MealViewModel viewModel;
    private MealHistoryAdapter adapter;

    // Parallel arrays — index position must match between labels and values
    private static final String[] MEAL_TYPE_VALUES  = {"BREAKFAST", "LUNCH", "DINNER", "SNACK"};
    private static final String[] MEAL_TYPE_LABELS  = {"Breakfast", "Lunch", "Dinner", "Snack"};

    private static final String[] CONTEXT_VALUES = {"HOME", "RESTAURANT", "DESK", "CAR", "SOCIAL", "OTHER"};
    private static final String[] CONTEXT_LABELS = {"Home", "Restaurant", "At my desk", "On the go", "Out with people", "Somewhere else"};

    private static final String[] EMOTION_VALUES = {"CALM", "HAPPY", "STRESSED", "ANXIOUS", "BORED", "SAD", "RUSHED", "OTHER"};
    private static final String[] EMOTION_LABELS = {"Calm", "Happy", "Stressed", "Anxious", "Bored", "Sad", "Rushed", "Other"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMealBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MealViewModel.class);

        setupMealTypeSpinner();
        setupContextSpinner();
        setupEmotionSpinner();
        setupSatisfactionBar();
        setupSaveButton();
        setupHistoryList();
        observeViewModel();
    }

    // Builds the meal type spinner (Breakfast / Lunch / Dinner / Snack)
    private void setupMealTypeSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_selected_item, MEAL_TYPE_LABELS);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.spinnerMealType.setAdapter(adapter);
        binding.spinnerMealType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setSelectedMealType(MEAL_TYPE_VALUES[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Builds the eating context spinner (Home / Restaurant / Desk etc.)
    private void setupContextSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_selected_item, CONTEXT_LABELS);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.spinnerContext.setAdapter(adapter);
        binding.spinnerContext.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setSelectedContext(CONTEXT_VALUES[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Builds the emotional state spinner (Calm / Happy / Stressed etc.)
    private void setupEmotionSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_selected_item, EMOTION_LABELS);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.spinnerEmotion.setAdapter(adapter);
        binding.spinnerEmotion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setSelectedEmotion(EMOTION_VALUES[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSatisfactionBar() {
        binding.satisfactionSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                viewModel.setSatisfactionRating((int) value);
            }
        });
    }

    private void setupSaveButton() {
        binding.btnSaveMeal.setOnClickListener(v -> {
            String description = binding.editFoodDescription.getText() != null
                    ? binding.editFoodDescription.getText().toString().trim() : "";

            // Description is the only required field
            if (description.isEmpty()) {
                // Point the user directly to the empty field with a clear message
                binding.editFoodDescription.setError("Please describe what you ate");
                binding.editFoodDescription.requestFocus();
                return;
            }

            Integer satisfaction = viewModel.getSatisfactionRating().getValue();
            String context  = viewModel.getSelectedContext().getValue();
            String emotion  = viewModel.getSelectedEmotion().getValue();
            String mealType = viewModel.getSelectedMealType().getValue();
            String note     = binding.editMealNote.getText() != null
                    ? binding.editMealNote.getText().toString().trim() : "";

            viewModel.saveMeal(
                    description,
                    satisfaction != null ? satisfaction : 5,
                    context  != null ? context  : "HOME",
                    emotion  != null ? emotion  : "CALM",
                    mealType != null ? mealType : "BREAKFAST",
                    note
            );

            // Reset form after save
            binding.editFoodDescription.setText("");
            binding.editMealNote.setText("");
            binding.satisfactionSlider.setValue(5f);
        });
    }

    private void setupHistoryList() {
        adapter = new MealHistoryAdapter();
        binding.recyclerMealHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMealHistory.setAdapter(adapter);
        // Wire up the delete callback
        adapter.setOnDeleteListener(entry -> viewModel.deleteMeal(entry.getId()));

// Swipe left on any row to delete it
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv,
                                  @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                // Drag-to-reorder not supported
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                adapter.getOnDeleteListener().onDelete(adapter.getItemAt(pos));
                Toast.makeText(requireContext(), "Meal deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(binding.recyclerMealHistory);
    }

    private void observeViewModel() {
        viewModel.getSatisfactionRating().observe(getViewLifecycleOwner(), rating ->
                binding.textSatisfactionValue.setText(rating + " / 10"));

        viewModel.getAllMeals().observe(getViewLifecycleOwner(), meals ->
                adapter.submitList(meals));

        // SingleLiveEvent — fires once per save only
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), saved -> {
            if (Boolean.TRUE.equals(saved)) {
                Toast.makeText(requireContext(), "Meal logged ✓", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
