package com.zeez.nourishquest;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.zeez.nourishquest.ui.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso navigation smoke tests.
 *
 * These tests launch MainActivity directly (bypassing onboarding by relying
 * on PrefsManager having been set, or via a test rule that sets it first)
 * and verify that tapping each bottom nav item displays the expected screen.
 *
 * Espresso's ViewMatchers verify UI state synchronously — no Thread.sleep()
 * or polling loops are needed. The Idling Resources mechanism handles async
 * LiveData updates.
 *
 * These are smoke tests, not exhaustive. They guard against:
 *  - Fragment not attaching (crash on navigation tap)
 *  - Wrong destination loaded (nav graph misconfiguration)
 *  - ViewBinding null reference after navigation
 */
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Test
    public void bottomNav_tapHunger_displaysHungerScreen() {
        try (ActivityScenario<MainActivity> scenario =
                     ActivityScenario.launch(MainActivity.class)) {

            Espresso.onView(ViewMatchers.withId(R.id.hungerFragment))
                    .perform(ViewActions.click());

            // The hunger screen title should be visible
            Espresso.onView(ViewMatchers.withText("HUNGER CHECK-IN"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void bottomNav_tapMeal_displaysMealScreen() {
        try (ActivityScenario<MainActivity> scenario =
                     ActivityScenario.launch(MainActivity.class)) {

            Espresso.onView(ViewMatchers.withId(R.id.mealFragment))
                    .perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("LOG A MEAL"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void bottomNav_tapJournal_displaysJournalScreen() {
        try (ActivityScenario<MainActivity> scenario =
                     ActivityScenario.launch(MainActivity.class)) {

            Espresso.onView(ViewMatchers.withId(R.id.journalFragment))
                    .perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("BODY JOURNAL"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void bottomNav_tapStats_displaysInsightsScreen() {
        try (ActivityScenario<MainActivity> scenario =
                     ActivityScenario.launch(MainActivity.class)) {

            Espresso.onView(ViewMatchers.withId(R.id.statsFragment))
                    .perform(ViewActions.click());

            Espresso.onView(ViewMatchers.withText("INSIGHTS"))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void bottomNav_tapHomeAfterHunger_returnsToHome() {
        try (ActivityScenario<MainActivity> scenario =
                     ActivityScenario.launch(MainActivity.class)) {

            // Navigate away then return
            Espresso.onView(ViewMatchers.withId(R.id.hungerFragment))
                    .perform(ViewActions.click());
            Espresso.onView(ViewMatchers.withId(R.id.homeFragment))
                    .perform(ViewActions.click());

            // Affirmation card should be visible on home
            Espresso.onView(ViewMatchers.withId(R.id.text_affirmation))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }
}
