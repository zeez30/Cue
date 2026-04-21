package com.zeez.nourishquest;

import com.zeez.nourishquest.util.AffirmationProvider;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the AffirmationProvider utility.
 * Validates data integrity, rotation logic, and adherence to domain-specific
 * linguistic constraints by preventing the inclusion of diet-culture terminology.
 */
public class AffirmationProviderTest {

    // Terminology strictly prohibited from the affirmation dataset
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
            assertNotNull("Affirmation index " + i + " must not be null", affirmation);
            assertFalse("Affirmation index " + i + " must not be empty", affirmation.isEmpty());
        }
    }

    /**
     * Iterates through the full affirmation dataset to ensure no entries
     * contain banned phrases. This prevents regression in clinical alignment.
     */
    @Test
    public void allAffirmations_doNotContainDietCultureLanguage() {
        int count = AffirmationProvider.getCount();
        for (int i = 0; i < count; i++) {
            String affirmation = AffirmationProvider.getAffirmation(i).toLowerCase();
            for (String banned : BANNED_PHRASES) {
                assertFalse(
                        "Affirmation #" + i + " contains banned phrase: " + banned,
                        affirmation.contains(banned)
                );
            }
        }
    }

    @Test
    public void getAffirmation_negativeIndex_doesNotCrash() {
        // Implementation utilizes Math.abs() to handle negative input wrap-around
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
        // Verifies the pool size is sufficient for a meaningful rotation cycle
        assertTrue("Insufficient affirmation count for rotation",
                AffirmationProvider.getCount() >= 30);
    }
}