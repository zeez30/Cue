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

// Shown on first launch only. Five slides explaining what Cue is and what it doesn't do.
// Name input has moved into OnboardingSlideFragment (slide 4) so it's always scrollable and tappable.
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

        // REMOVE THIS LINE BEFORE FINAL SUBMISSION — resets onboarding for testing
        prefs.setOnboardingDone(false);

        // Skip onboarding for returning users
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

    private void advancePage() {
        int current = viewPager.getCurrentItem();
        if (current < TOTAL_PAGES - 1) {
            viewPager.setCurrentItem(current + 1, true);
        } else {
            finishOnboarding();
        }
    }

    // Updates the page indicator and button labels based on current position
    private void updateControls(int position) {
        textPageIndicator.setText((position + 1) + " / " + TOTAL_PAGES);

        if (position == TOTAL_PAGES - 1) {
            // Last slide — hide skip, change Next to Begin
            btnNext.setText("BEGIN  ▶");
            btnSkip.setVisibility(android.view.View.GONE);
        } else {
            btnNext.setText("NEXT  ▶");
            btnSkip.setVisibility(android.view.View.VISIBLE);
        }
    }

    // Called when user taps Begin or Skip — marks onboarding done and launches the app
    private void finishOnboarding() {
        prefs.setOnboardingDone(true);
        // Name is saved by OnboardingSlideFragment when slide 4 is destroyed
        MindfulEatingReminderWorker.scheduleDaily(this);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
