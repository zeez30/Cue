package com.zeez.nourishquest.util;

import android.graphics.Color;

// Maps the 1–10 hunger/fullness scale to labels, body cue descriptions, and colours.
// Colours run coral (hungry) → mustard (neutral) → grape (overfull).
public final class HungerScaleHelper {

    private HungerScaleHelper() {}

    private static final String[] HUNGER_LABELS = {
        "",                              // index 0 unused
        "Painfully hungry",
        "Very hungry, hard to focus",
        "Hungry, ready to eat",
        "Slightly hungry",
        "Neutral & comfortable",
        "Pleasantly satisfied",
        "Comfortably full",
        "Very full, slightly heavy",
        "Stuffed & uncomfortable",
        "Painfully stuffed"
    };

    // What the user might physically notice at each level
    private static final String[] BODY_CUES = {
        "",
        "Headache, shakiness, difficulty concentrating",
        "Stomach growling, irritability, low energy",
        "Clear stomach signals, slightly empty feeling",
        "Mild, subtle hunger signals",
        "No strong signals either way",
        "Stomach feels pleasantly occupied",
        "Eating feels complete, comfortable to stop",
        "Waistband feels tighter, sluggish",
        "Stomach feels very stretched, tired",
        "Nauseous, need to lie down"
    };

    // Colour gradient across the scale
    private static final int[] SCALE_COLOURS = {
        0,
        Color.parseColor("#EF767A"), // 1  coral — discomfort (hungry)
        Color.parseColor("#F2867E"), // 2
        Color.parseColor("#F5967D"), // 3
        Color.parseColor("#FFBF6B"), // 4  amber — approaching neutral
        Color.parseColor("#FFE347"), // 5  mustard — neutral
        Color.parseColor("#A8E8B4"), // 6  soft green — comfortable
        Color.parseColor("#23F0C7"), // 7  mint — satisfied
        Color.parseColor("#7D7ABC"), // 8  periwinkle — full
        Color.parseColor("#6D65B0"), // 9
        Color.parseColor("#6457A6"), // 10 grape — discomfort (overfull)
    };

    public static String getLabel(int level) {
        if (level < 1 || level > 10) return "";
        return HUNGER_LABELS[level];
    }

    public static String getBodyCue(int level) {
        if (level < 1 || level > 10) return "";
        return BODY_CUES[level];
    }

    public static int getColour(int level) {
        if (level < 1 || level > 10) return Color.parseColor("#F5F5FF");
        return SCALE_COLOURS[level];
    }

    // Dimmed version of the colour for unselected scale blocks
    public static int getDimColour(int level) {
        int base = getColour(level);
        int r = (int) (Color.red(base) * 0.25f);
        int g = (int) (Color.green(base) * 0.25f);
        int b = (int) (Color.blue(base) * 0.25f);
        return Color.rgb(r, g, b);
    }
}
