package com.zeez.nourishquest;

import com.zeez.nourishquest.util.AffirmationProvider;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Verifies that the affirmation provider never returns null/empty,
 * that the daily rotation produces a value for every day of the year,
 * and that no affirmation contains diet-culture language.
 *
 * The final check is especially important: if an affirmation is
 * accidentally edited to include words like "calories", "lose weight",
 * or "cheat day", a test failure will catch the regression immediately.
 */
public class AffirmationProviderTest {

    private static final String[] BANNED_PHRASES = {
        "calories", "cheat", "lose weight", "diet", "restrict",
        "guilt", "bad food", "good food", "junk food", "clean eating"
    };

    @Test
    public void getDailyAffirmation_isNotNull() {
        assertNotNull(AffirmationProvider.getDailyAffirmation());
    }

    @Test
    public void getDailyAffirmation_isNotEmpty() {
        assertFalse(AffirmationProvider.getDailyAffirmation().isEmpty());
    }

    @Test
    public void getAffirmation_allIndices_returnNonEmpty() {
        int count = AffirmationProvider.getCount();
        for (int i = 0; i < count; i++) {
            String affirmation = AffirmationProvider.getAffirmation(i);
            assertNotNull("Affirmation at index " + i + " must not be null", affirmation);
            assertFalse("Affirmation at index " + i + " must not be empty", affirmation.isEmpty());
        }
    }

    @Test
    public void allAffirmations_doNotContainDietCultureLanguage() {
        int count = AffirmationProvider.getCount();
        for (int i = 0; i < count; i++) {
            String affirmation = AffirmationProvider.getAffirmation(i).toLowerCase();
            for (String banned : BANNED_PHRASES) {
                assertFalse(
                    "Affirmation #" + i + " contains banned phrase: \"" + banned + "\"",
                    affirmation.contains(banned)
                );
            }
        }
    }

    @Test
    public void getAffirmation_negativeIndex_doesNotCrash() {
        // Math.abs() in the implementation ensures negative indices wrap safely
        String result = AffirmationProvider.getAffirmation(-5);
        assertNotNull(result);
    }

    @Test
    public void getAffirmation_veryLargeIndex_doesNotCrash() {
        String result = AffirmationProvider.getAffirmation(Integer.MAX_VALUE);
        assertNotNull(result);
    }

    @Test
    public void affirmationCount_coversFullYear() {
        // At least 33 affirmations means a new one every 11 days even in a leap year.
        // Aim for at least one per rotation cycle felt by the user.
        assertTrue("Should have enough affirmations for meaningful rotation",
                AffirmationProvider.getCount() >= 30);
    }
}
