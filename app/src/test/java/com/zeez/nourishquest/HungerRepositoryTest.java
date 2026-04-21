package com.zeez.nourishquest;

import com.zeez.nourishquest.util.DateUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for temporal utility functions.
 * Validates the logic for day-range calculation and timestamp formatting
 * used throughout the application to manage logging history and streaks.
 */
public class HungerRepositoryTest {

    @Test
    public void todayRange_startIsBeforeEnd() {
        long[] range = DateUtils.getTodayRange();
        assertTrue("Start of day must be chronologically before end of day", range[0] < range[1]);
    }

    @Test
    public void todayRange_spanIsExactlyOneDay() {
        long[] range = DateUtils.getTodayRange();
        long expectedSpan = DateUtils.ONE_DAY_MS;
        assertEquals("Day range must span exactly 24 hours in milliseconds", expectedSpan, range[1] - range[0]);
    }

    @Test
    public void todayRange_nowFallsWithinRange() {
        long[] range = DateUtils.getTodayRange();
        long now = System.currentTimeMillis();
        assertTrue("Current system time must fall within the calculated today range", now >= range[0] && now < range[1]);
    }

    @Test
    public void dayRange_offsetByOneDay_doesNotOverlapToday() {
        long[] today = DateUtils.getTodayRange();
        long[] yesterday = DateUtils.getDayRange(System.currentTimeMillis() - DateUtils.ONE_DAY_MS);

        // Verifies that adjacent day ranges meet exactly at the midnight boundary
        assertTrue("Yesterday's end timestamp must equal today's start timestamp", yesterday[1] == today[0]);
    }

    @Test
    public void formatTime_doesNotReturnNull() {
        String result = DateUtils.formatTime(System.currentTimeMillis());
        assertNotNull("Time formatting result should not be null", result);
        assertFalse("Time formatting result should not be empty", result.isEmpty());
    }

    @Test
    public void formatDate_doesNotReturnNull() {
        String result = DateUtils.formatDate(System.currentTimeMillis());
        assertNotNull("Date formatting result should not be null", result);
        assertFalse("Date formatting result should not be empty", result.isEmpty());
    }

    @Test
    public void isToday_currentTimeReturnsTrue() {
        assertTrue("Utility must identify the current system time as today", DateUtils.isToday(System.currentTimeMillis()));
    }

    @Test
    public void isToday_yesterdayReturnsFalse() {
        assertFalse("Utility must not identify a 24-hour offset as today", DateUtils.isToday(System.currentTimeMillis() - DateUtils.ONE_DAY_MS));
    }
}