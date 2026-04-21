package com.zeez.nourishquest.util;

import java.util.Calendar;

/**
 * Utility class providing Intuitive Eating affirmations.
 * Implementation uses a deterministic rotation based on the current calendar day.
 */
public final class AffirmationProvider {

    private AffirmationProvider() {
        // Private constructor to prevent instantiation
    }

    private static final String[] AFFIRMATIONS = {
            "My hunger is not a problem to be solved. It's a signal to be respected.",
            "I don't need to earn my food. Eating is not a reward: it's a right.",
            "My body is wise. It knows what it needs.",
            "There are no good foods or bad foods. Food is just food.",
            "I give myself permission to eat what sounds good to me.",
            "Fullness is not failure. My body knows when it's had enough.",
            "Satisfaction matters. I deserve to enjoy what I eat.",
            "Rest is productive. My body doesn't have to earn its keep.",
            "I release the guilt. It never helped me anyway.",
            "My worth has nothing to do with my plate.",
            "Diets have failed me: not the other way around.",
            "I am learning to trust my body again. That takes time, and that's okay.",
            "Eating when I'm hungry is an act of self-respect.",
            "I notice my body's signals with curiosity, not judgment.",
            "Gentle nutrition means nourishing my body without punishing it.",
            "Movement is something I get to do, not something I have to do.",
            "My body has carried me through everything. It deserves kindness.",
            "I can cope with my emotions without using food as a punishment.",
            "One meal doesn't define my health. One day doesn't define my life.",
            "I am more than a body. I am a whole person.",
            "I get to decide what goes on my plate.",
            "Progress in IE is not linear. Setbacks are part of the journey.",
            "Every time I check in with my hunger, I build trust with myself.",
            "Peace with food is possible. I'm already on the path.",
            "My body is not an ornament. It's the home I live in.",
            "I don't owe anyone thinness, a diet, or an explanation.",
            "Eating enough is not indulgent: it's necessary.",
            "I challenge the food police today. Their rules don't serve me.",
            "Satisfaction is a valid goal at every meal.",
            "I honour my body's needs, even when they're inconvenient.",
            "Pleasure at mealtimes is not a luxury. It's part of eating well.",
            "My relationship with food is a practice, not a performance."
    };

    // Cycles through the array based on the day of the year
    public static String getDailyAffirmation() {
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return AFFIRMATIONS[dayOfYear % AFFIRMATIONS.length];
    }

    // Returns an affirmation by index with modulo protection
    public static String getAffirmation(int index) {
        return AFFIRMATIONS[Math.abs(index) % AFFIRMATIONS.length];
    }

    // Returns the total count of available strings
    public static int getCount() {
        return AFFIRMATIONS.length;
    }
}