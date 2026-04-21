package com.zeez.nourishquest.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zeez.nourishquest.data.entity.HungerLog;
import com.zeez.nourishquest.data.entity.MealEntry;
import com.zeez.nourishquest.databinding.FragmentStatsBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Insights screen showing hunger and satisfaction trends over the last 7 days.
public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private StatsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);
        observeStats();
    }

    private void observeStats() {
        // Recompute when either data source updates
        viewModel.getRecentHungerLogs().observe(getViewLifecycleOwner(), hungerLogs -> {
            List<MealEntry> meals = viewModel.getRecentMeals().getValue();
            if (meals != null) viewModel.computeStats(hungerLogs, meals);
        });

        viewModel.getRecentMeals().observe(getViewLifecycleOwner(), meals -> {
            List<HungerLog> hungerLogs = viewModel.getRecentHungerLogs().getValue();
            if (hungerLogs != null) viewModel.computeStats(hungerLogs, meals);
        });

        viewModel.getWeeklyHungerAverages().observe(getViewLifecycleOwner(), averages ->
                binding.chartHunger.setData(averages, buildDayLabels(),
                        Color.parseColor("#EF767A"), 10f));

        viewModel.getWeeklySatisfactionTrend().observe(getViewLifecycleOwner(), trend ->
                binding.chartSatisfaction.setData(trend, buildDayLabels(),
                        Color.parseColor("#6457A6"), 10f));
    }

    // Two-letter day labels with "Today" for the rightmost bar
    private String[] buildDayLabels() {
        String[] labels = new String[7];
        SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        for (int i = 6; i >= 0; i--) {
            cal.setTimeInMillis(System.currentTimeMillis() - (long) i * 24 * 60 * 60 * 1000L);
            labels[6 - i] = i == 0 ? "Today" : fmt.format(cal.getTime()).substring(0, 2);
        }
        return labels;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}