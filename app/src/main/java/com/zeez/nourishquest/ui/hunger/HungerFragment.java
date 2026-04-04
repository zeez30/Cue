package com.zeez.nourishquest.ui.hunger;

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

import com.zeez.nourishquest.databinding.FragmentHungerBinding;
import com.zeez.nourishquest.util.HungerScaleHelper;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

// Hunger/fullness check-in screen.
// The pixel scale is built programmatically — 10 View blocks added to a LinearLayout.
// Tapping a block updates the ViewModel which triggers a redraw of all blocks.
public class HungerFragment extends Fragment {

    private FragmentHungerBinding binding;
    private HungerViewModel viewModel;
    private HungerLogAdapter adapter;

    // Tracks whether this check-in is before or after a meal
    private String selectedPhase = "BEFORE";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHungerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HungerViewModel.class);

        setupScaleBlocks();
        setupPhaseToggle();
        setupSaveButton();
        setupHistoryList();
        observeViewModel();
    }

    // Builds 10 colour blocks for the hunger scale and attaches click listeners
    private void setupScaleBlocks() {
        binding.pixelScale.removeAllViews();

        float density = getResources().getDisplayMetrics().density;
        int size = (int) (density * 28);
        int margin = (int) (density * 3);

        for (int level = 1; level <= 10; level++) {
            final int finalLevel = level;
            View block = new View(requireContext());

            android.widget.LinearLayout.LayoutParams params =
                    new android.widget.LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            block.setLayoutParams(params);
            block.setBackgroundColor(HungerScaleHelper.getDimColour(level));
            block.setTag(level);
            block.setOnClickListener(v -> viewModel.setSelectedLevel(finalLevel));

            binding.pixelScale.addView(block);
        }
    }

    // Redraws scale blocks when the selected level changes
    private void refreshScaleVisuals(int selectedLevel) {
        for (int i = 0; i < binding.pixelScale.getChildCount(); i++) {
            View block = binding.pixelScale.getChildAt(i);
            int level = (int) block.getTag();
            // Blocks at or below the selected level are bright, above are dimmed
            block.setBackgroundColor(level <= selectedLevel
                    ? HungerScaleHelper.getColour(level)
                    : HungerScaleHelper.getDimColour(level));
        }
        binding.textScaleLabel.setText(HungerScaleHelper.getLabel(selectedLevel));
        binding.textBodyCue.setText(HungerScaleHelper.getBodyCue(selectedLevel));
        binding.textScaleNumber.setText(String.valueOf(selectedLevel));
    }

    // BEFORE/AFTER toggle — alpha shows which is active
    private void setupPhaseToggle() {
        binding.btnBefore.setOnClickListener(v -> {
            selectedPhase = "BEFORE";
            binding.btnBefore.setAlpha(1f);
            binding.btnAfter.setAlpha(0.45f);
        });
        binding.btnAfter.setOnClickListener(v -> {
            selectedPhase = "AFTER";
            binding.btnAfter.setAlpha(1f);
            binding.btnBefore.setAlpha(0.45f);
        });
        // Default to BEFORE
        binding.btnBefore.setAlpha(1f);
        binding.btnAfter.setAlpha(0.45f);
    }

    private void setupSaveButton() {
        binding.btnSaveCheckIn.setOnClickListener(v -> {
            Integer level = viewModel.getSelectedLevel().getValue();
            if (level == null) level = 5;
            String note = binding.editNote.getText() != null
                    ? binding.editNote.getText().toString().trim() : "";
            viewModel.saveCheckIn(level, selectedPhase, note);
            binding.editNote.setText("");

            // Brief visual confirmation on the button itself
            binding.btnSaveCheckIn.setText("SAVED ✓");
            binding.btnSaveCheckIn.setEnabled(false);
            binding.btnSaveCheckIn.postDelayed(() -> {
                binding.btnSaveCheckIn.setText("SAVE CHECK-IN");
                binding.btnSaveCheckIn.setEnabled(true);
            }, 1500);
        });
    }

    private void setupHistoryList() {
        adapter = new HungerLogAdapter();
        binding.recyclerTodaysLogs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTodaysLogs.setAdapter(adapter);
        // Wire up the delete callback — tells the ViewModel to remove the entry from the DB
        adapter.setOnDeleteListener(log -> viewModel.deleteLog(log.getId()));

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
                // Fire the delete callback then show confirmation
                adapter.getOnDeleteListener().onDelete(adapter.getItemAt(pos));
                Toast.makeText(requireContext(), "Check-in deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(binding.recyclerTodaysLogs);
    }

    private void observeViewModel() {
        viewModel.getSelectedLevel().observe(getViewLifecycleOwner(), this::refreshScaleVisuals);

        viewModel.getTodaysLogs().observe(getViewLifecycleOwner(), logs -> {
            adapter.submitList(logs);
            binding.textTodayLogCount.setText(
                    logs.size() + " check-in" + (logs.size() == 1 ? "" : "s") + " today");
        });

        // SingleLiveEvent — only fires once per save, not on every rotation
        viewModel.getSaveResult().observe(getViewLifecycleOwner(), saved -> {
            if (Boolean.TRUE.equals(saved)) {
                Toast.makeText(requireContext(), "Check-in saved ✓", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
