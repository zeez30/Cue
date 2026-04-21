package com.zeez.nourishquest;

import com.zeez.nourishquest.util.HungerScaleHelper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for the HungerScaleHelper utility.
 * Validates the mapping of the decile scale to corresponding linguistic
 * descriptors, physiological cues, and visual representation values.
 */
public class HungerScaleHelperTest {

    @Test
    public void getLabel_level1_isNotEmpty() {
        assertFalse(HungerScaleHelper.getLabel(1).isEmpty());
    }

    @Test
    public void getLabel_level10_isNotEmpty() {
        assertFalse(HungerScaleHelper.getLabel(10).isEmpty());
    }

    @Test
    public void getLabel_outOfBounds_returnsEmpty() {
        // Verifies boundary handling for inputs outside the 1:10 range
        assertEquals("", HungerScaleHelper.getLabel(0));
        assertEquals("", HungerScaleHelper.getLabel(11));
        assertEquals("", HungerScaleHelper.getLabel(-1));
    }

    @Test
    public void getBodyCue_allLevels_notEmpty() {
        for (int i = 1; i <= 10; i++) {
            String cue = HungerScaleHelper.getBodyCue(i);
            assertNotNull("Physiological descriptor at level " + i + " must not be null", cue);
            assertFalse("Physiological descriptor at level " + i + " must not be empty", cue.isEmpty());
        }
    }

    @Test
    public void getColour_allLevels_returnValidColour() {
        for (int i = 1; i <= 10; i++) {
            int colour = HungerScaleHelper.getColour(i);
            // Validates that the ARGB integer maintains a fully opaque alpha channel
            int alpha = (colour >> 24) & 0xFF;
            assertEquals("Colour at level " + i + " must utilize a 0xFF alpha value", 0xFF, alpha);
        }
    }

    @Test
    public void getDimColour_isAlwaysDarkerThanBase() {
        for (int i = 1; i <= 10; i++) {
            int base = HungerScaleHelper.getColour(i);
            int dim = HungerScaleHelper.getDimColour(i);
            int baseR = (base >> 16) & 0xFF;
            int dimR = (dim >> 16) & 0xFF;
            assertTrue("Diminished red channel must be less than or equal to base at level " + i, dimR <= baseR);
        }
    }

    @Test
    public void level5_label_containsNeutralOrComfort() {
        String label = HungerScaleHelper.getLabel(5).toLowerCase();
        boolean isNeutralOrComfort = label.contains("neutral") || label.contains("comfort");
        assertTrue("Level 5 must represent a neutral or comfortable physiological state", isNeutralOrComfort);
    }
}