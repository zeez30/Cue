package com.zeez.nourishquest;

import com.zeez.nourishquest.util.HungerScaleHelper;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for HungerScaleHelper — the utility that maps the 1–10 scale
 * to human-readable labels, body cues, and colour values.
 *
 * These tests guard against silent regressions when the scale descriptions
 * are edited. The IE research basis for each level label should never be
 * accidentally removed or overwritten with a judgement-laden term.
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
        assertEquals("", HungerScaleHelper.getLabel(0));
        assertEquals("", HungerScaleHelper.getLabel(11));
        assertEquals("", HungerScaleHelper.getLabel(-1));
    }

    @Test
    public void getBodyCue_allLevels_notEmpty() {
        for (int i = 1; i <= 10; i++) {
            String cue = HungerScaleHelper.getBodyCue(i);
            assertNotNull("Body cue at level " + i + " should not be null", cue);
            assertFalse("Body cue at level " + i + " should not be empty", cue.isEmpty());
        }
    }

    @Test
    public void getColour_allLevels_returnValidColour() {
        for (int i = 1; i <= 10; i++) {
            int colour = HungerScaleHelper.getColour(i);
            // Android colours are packed ARGB ints — alpha must be 0xFF for opaque
            int alpha = (colour >> 24) & 0xFF;
            assertEquals("Colour at level " + i + " must be fully opaque", 0xFF, alpha);
        }
    }

    @Test
    public void getDimColour_isAlwaysDarkerThanBase() {
        for (int i = 1; i <= 10; i++) {
            int base = HungerScaleHelper.getColour(i);
            int dim = HungerScaleHelper.getDimColour(i);
            int baseR = (base >> 16) & 0xFF;
            int dimR  = (dim  >> 16) & 0xFF;
            assertTrue("Dim colour red channel should be <= base at level " + i, dimR <= baseR);
        }
    }

    @Test
    public void level5_label_containsNeutralOrComfort() {
        String label = HungerScaleHelper.getLabel(5).toLowerCase();
        boolean isNeutralOrComfort = label.contains("neutral") || label.contains("comfort");
        assertTrue("Level 5 should represent neutral/comfortable", isNeutralOrComfort);
    }
}
