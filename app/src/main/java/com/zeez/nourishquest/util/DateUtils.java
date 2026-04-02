package com.zeez.nourishquest.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Date/time helpers used across DB range queries and display formatting.
public final class DateUtils {

    public static final long ONE_DAY_MS = 24 * 60 * 60 * 1000L;

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("EEE d MMM", Locale.getDefault());
    private static final SimpleDateFormat FULL_FORMAT =
            new SimpleDateFormat("EEE d MMM, h:mm a", Locale.getDefault());

    private DateUtils() {}

    // Returns [startOfToday, startOfTomorrow) in epoch ms
    public static long[] getTodayRange() {
        return getDayRange(System.currentTimeMillis());
    }

    // Returns [startOfDay, startOfNextDay) for whichever day contains epochMs
    public static long[] getDayRange(long epochMs) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(epochMs);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();
        return new long[]{start, start + ONE_DAY_MS};
    }

    public static String formatTime(long epochMs) {
        return TIME_FORMAT.format(new Date(epochMs));
    }

    public static String formatDate(long epochMs) {
        return DATE_FORMAT.format(new Date(epochMs));
    }

    public static String formatFull(long epochMs) {
        return FULL_FORMAT.format(new Date(epochMs));
    }

    public static boolean isToday(long epochMs) {
        long[] range = getTodayRange();
        return epochMs >= range[0] && epochMs < range[1];
    }
}
