package com.zeez.nourishquest.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.zeez.nourishquest.R;
import com.zeez.nourishquest.ui.MainActivity;
import com.zeez.nourishquest.util.MindfulEatingReminderWorker;
import com.zeez.nourishquest.util.PrefsManager;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext;
    private Button btnSkip;
    private TextView textPageIndicator;

    private OnboardingPagerAdapter adapter;
    private PrefsManager prefs;

    private static final int TOTAL_PAGES = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = new PrefsManager(this);

        // Redirect returning users if onboarding has previously been completed
        if (prefs.isOnboardingDone()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);
        viewPager         = findViewById(R.id.onboarding_pager);
        btnNext           = findViewById(R.id.btn_onboarding_next);
        btnSkip           = findViewById(R.id.btn_onboarding_skip);
        textPageIndicator = findViewById(R.id.text_page_indicator);

        adapter = new OnboardingPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setUserInputEnabled(true);

        // Listener to update UI elements based on the current scroll position
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateControls(position);
            }
        });

        btnNext.setOnClickListener(v -> advancePage());
        btnSkip.setOnClickListener(v -> finishOnboarding());
        updateControls(0);
    }

    // Progresses the ViewPager to the next slide or finishes the workflow
    private void advancePage() {
        int current = viewPager.getCurrentItem();
        if (current < TOTAL_PAGES - 1) {
            viewPager.setCurrentItem(current + 1, true);
        } else {
            finishOnboarding();
        }
    }

    // Refreshes the page indicator and button text based on current index
    private void updateControls(int position) {
        textPageIndicator.setText((position + 1) + " / " + TOTAL_PAGES);

        if (position == TOTAL_PAGES - 1) {
            btnNext.setText("BEGIN  ▶");
            btnSkip.setVisibility(android.view.View.GONE);
        } else {
            btnNext.setText("NEXT  ▶");
            btnSkip.setVisibility(android.view.View.VISIBLE);
        }
    }

    /**
     * Finalizes the onboarding state.
     * Persists the completion flag and schedules periodic background work.
     */
    private void finishOnboarding() {
        prefs.setOnboardingDone(true);
        MindfulEatingReminderWorker.scheduleDaily(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}