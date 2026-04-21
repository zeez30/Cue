package com.zeez.nourishquest.ui.onboarding;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

// Provides the five onboarding slide Fragments to the ViewPager2.
public class OnboardingPagerAdapter extends FragmentStateAdapter {

    private static final int PAGE_COUNT = 5;

    public OnboardingPagerAdapter(@NonNull FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return OnboardingSlideFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
