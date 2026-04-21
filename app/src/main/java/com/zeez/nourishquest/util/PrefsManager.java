package com.zeez.nourishquest.util;

import android.content.Context;
import android.content.SharedPreferences;

// Typed wrapper around SharedPreferences.

public final class PrefsManager {

    private static final String PREFS_NAME        = "cue_prefs";
    private static final String KEY_ONBOARDING    = "onboarding_done";
    private static final String KEY_REMINDER_ON   = "reminder_enabled";
    private static final String KEY_REMINDER_HOUR = "reminder_hour";
    private static final String KEY_REMINDER_MIN  = "reminder_minute";
    private static final String KEY_PLAYER_NAME   = "player_name";

    private final SharedPreferences prefs;

    public PrefsManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // --- Onboarding ---

    public boolean isOnboardingDone() {
        return prefs.getBoolean(KEY_ONBOARDING, false);
    }

    public void setOnboardingDone(boolean done) {
        prefs.edit().putBoolean(KEY_ONBOARDING, done).apply();
    }

    // --- Player name (optional, shown on home screen) ---

    public String getPlayerName() {
        return prefs.getString(KEY_PLAYER_NAME, "");
    }

    public void setPlayerName(String name) {
        prefs.edit().putString(KEY_PLAYER_NAME, name).apply();
    }

    // --- Daily reminder settings ---

    public boolean isReminderEnabled() {
        return prefs.getBoolean(KEY_REMINDER_ON, false);
    }

    public void setReminderEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_REMINDER_ON, enabled).apply();
    }

    public int getReminderHour() {
        return prefs.getInt(KEY_REMINDER_HOUR, 12);
    }

    public int getReminderMinute() {
        return prefs.getInt(KEY_REMINDER_MIN, 0);
    }

    public void setReminderTime(int hour, int minute) {
        prefs.edit()
                .putInt(KEY_REMINDER_HOUR, hour)
                .putInt(KEY_REMINDER_MIN, minute)
                .apply();
    }
}
