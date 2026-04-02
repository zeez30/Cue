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
import java.util.Map;

// Insights screen — shows three pixel bar charts:
// 1. Average hunger level per day (last 7 days)
// 2. Average meal satisfaction per day (last 7 days)
// 3. How often each emotional state appeared in meal logs
//
// Stats are computed in StatsViewModel on a background thread.
// Both LiveData sources must emit before computation runs — the double-observer pattern handles this.
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
        observeAndComputeStats();
    }

    // Triggers computation when both hunger logs and meals are available.
    // Either observer can fire first — whichever fires checks if the other has data.
    private void observeAndComputeStats() {
        viewModel.getRecentHungerLogs().observe(getViewLifecycleOwner(), hungerLogs -> {
            List<MealEntry> meals = viewModel.getRecentMeals().getValue();
            if (meals != null) viewModel.computeStats(hungerLogs, meals);
        });

        viewModel.getRecentMeals().observe(getViewLifecycleOwner(), meals -> {
            List<HungerLog> hungerLogs = viewModel.getRecentHungerLogs().getValue();
            if (hungerLogs != null) viewModel.computeStats(hungerLogs, meals);
        });

        // Hunger chart — coral bars
        viewModel.getWeeklyHungerAverages().observe(getViewLifecycleOwner(), averages ->
                binding.chartHunger.setData(averages, buildDayLabels(),
                        Color.parseColor("#EF767A"), 10f));

        // Satisfaction chart — grape bars
        viewModel.getWeeklySatisfactionTrend().observe(getViewLifecycleOwner(), trend ->
                binding.chartSatisfaction.setData(trend, buildDayLabels(),
                        Color.parseColor("#6457A6"), 10f));

        // Emotion breakdown chart — mustard bars
        viewModel.getEmotionFrequency().observe(getViewLifecycleOwner(),
                this::renderEmotionBreakdown);
    }

    // Builds x-axis labels: two-letter day abbreviations with "Today" for the last column
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

    private void renderEmotionBreakdown(Map<String, Integer> freq) {
        if (freq == null || freq.isEmpty()) {
            // Show empty state message if no meal data yet
            binding.textEmotionEmpty.setVisibility(View.VISIBLE);
            binding.containerEmotionBars.setVisibility(View.GONE);
            return;
        }
        binding.textEmotionEmpty.setVisibility(View.GONE);
        binding.containerEmotionBars.setVisibility(View.VISIBLE);

        String[] emotions = freq.keySet().toArray(new String[0]);
        float[] counts = new float[emotions.length];
        float maxCount = 1f;
        for (int i = 0; i < emotions.length; i++) {
            counts[i] = freq.get(emotions[i]);
            if (counts[i] > maxCount) maxCount = counts[i];
        }

        // Truncate long emotion labels to 4 chars to fit under the bars
        String[] shortLabels = new String[emotions.length];
        for (int i = 0; i < emotions.length; i++) {
            shortLabels[i] = emotions[i].length() > 4
                    ? emotions[i].substring(0, 4) : emotions[i];
        }

        binding.chartEmotion.setData(counts, shortLabels,
                Color.parseColor("#FFE347"), maxCount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
