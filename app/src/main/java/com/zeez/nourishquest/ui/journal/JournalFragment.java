package com.zeez.nourishquest.ui.journal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeez.nourishquest.databinding.FragmentJournalBinding;

// Body journal screen — three IE-based prompts plus a mood selector.
// At least one prompt must be filled in before saving.
// Mood is selected by tapping emoji tiles — faster than a dropdown on low-mood days.
public class JournalFragment extends Fragment {

    private FragmentJournalBinding binding;
    private JournalViewModel viewModel;
    private JournalHistoryAdapter adapter;

    // Parallel arrays — index matches between MOODS and the mood view IDs in the layout
    private static final String[] MOODS = {"GREAT", "GOOD", "OKAY", "DIFFICULT", "ROUGH"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentJournalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(JournalViewModel.class);

        setupMoodSelector();
        setupSaveButton();
        setupHistoryList();
        observeViewModel();
    }

    // Wires up the five mood emoji tiles — selected tile scales up, others dim
    private void setupMoodSelector() {
        View[] moodViews = {
            binding.moodGreat, binding.moodGood, binding.moodOkay,
            binding.moodDifficult, binding.moodRough
        };

        for (int i = 0; i < moodViews.length; i++) {
            final int index = i;
            moodViews[i].setOnClickListener(v -> {
                viewModel.setSelectedMood(MOODS[index]);
                updateMoodHighlight(moodViews, index);
            });
        }

        // Default to OKAY
        updateMoodHighlight(moodViews, 2);
    }

    // Highlights the selected mood tile and dims the others
    private void updateMoodHighlight(View[] moodViews, int selectedIndex) {
        for (int i = 0; i < moodViews.length; i++) {
            moodViews[i].setAlpha(i == selectedIndex ? 1f : 0.35f);
            moodViews[i].setScaleX(i == selectedIndex ? 1.15f : 1f);
            moodViews[i].setScaleY(i == selectedIndex ? 1.15f : 1f);
        }
    }

    private void setupSaveButton() {
        binding.btnSaveJournal.setOnClickListener(v -> {
            String bodyFeeling = binding.editBodyFeeling.getText() != null
                    ? binding.editBodyFeeling.getText().toString().trim() : "";
            String challengedThought = binding.editChallengedThought.getText() != null
                    ? binding.editChallengedThought.getText().toString().trim() : "";
            String bodyGratitude = binding.editBodyGratitude.getText() != null
                    ? binding.editBodyGratitude.getText().toString().trim() : "";
            String mood = viewModel.getSelectedMood().getValue();
            if (mood == null) mood = "OKAY";

            // Require at least one prompt to be filled in
            if (bodyFeeling.isEmpty() && challengedThought.isEmpty() && bodyGratitude.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Fill in at least one prompt to save.", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.saveEntry(bodyFeeling, challengedThought, bodyGratitude, mood);

            // Clear fields after saving
            binding.editBodyFeeling.setText("");
            binding.editChallengedThought.setText("");
            binding.editBodyGratitude.setText("");
        });
    }

    private void setupHistoryList() {
        adapter = new JournalHistoryAdapter();
        binding.recyclerJournalHistory.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerJournalHistory.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getAllEntries().observe(getViewLifecycleOwner(), entries ->
                adapter.submitList(entries));

        // SingleLiveEvent — fires once per save only
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), saved -> {
            if (Boolean.TRUE.equals(saved)) {
                Toast.makeText(requireContext(), "Entry saved ✓", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
