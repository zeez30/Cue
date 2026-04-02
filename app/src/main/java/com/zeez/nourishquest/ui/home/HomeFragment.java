package com.zeez.nourishquest.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.zeez.nourishquest.R;
import com.zeez.nourishquest.databinding.FragmentHomeBinding;
import com.zeez.nourishquest.util.PrefsManager;

import java.util.Locale;

// Home screen — shows daily affirmation, awareness streak, satisfaction average,
// and quick-action tiles to the three main features.
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupGreeting();
        observeViewModel();
        setupQuickActions();
    }

    // Shows the user's name in the title if they set one during onboarding
    private void setupGreeting() {
        PrefsManager prefs = new PrefsManager(requireContext());
        String name = prefs.getPlayerName();
        if (!name.isEmpty()) {
            binding.textAppTitle.setText("Cue · " + name);
        }
    }

    private void observeViewModel() {
        viewModel.getDailyAffirmation().observe(getViewLifecycleOwner(), affirmation ->
                binding.textAffirmation.setText(affirmation));

        viewModel.getCheckInStreak().observe(getViewLifecycleOwner(), streak -> {
            binding.textStreakCount.setText(String.valueOf(streak));
            binding.textStreakLabel.setText(streak == 1 ? "day streak" : "days streak");
        });

        viewModel.getWeeklySatisfactionAverage().observe(getViewLifecycleOwner(), avg -> {
            if (avg != null && avg > 0) {
                binding.textSatisfactionAvg.setText(
                        String.format(Locale.getDefault(), "%.1f / 10", avg));
            } else {
                // No data yet
                binding.textSatisfactionAvg.setText("—");
            }
        });

        viewModel.getRecentMeals().observe(getViewLifecycleOwner(), meals -> {
            int count = meals == null ? 0 : meals.size();
            binding.textRecentMealCount.setText(
                    count + " meal" + (count == 1 ? "" : "s") + " logged recently");
        });
    }

    // Each tile navigates to its respective fragment via the NavController
    private void setupQuickActions() {
        binding.cardHungerCheckin.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_hunger));
        binding.cardLogMeal.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_meal));
        binding.cardJournal.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_journal));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Null out binding to prevent memory leaks when the fragment view is destroyed
        binding = null;
    }
}
