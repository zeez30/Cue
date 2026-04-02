package com.zeez.nourishquest;

import com.zeez.nourishquest.util.DateUtils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for date utility functions used in streak calculation.
 *
 * These tests run on the JVM without an Android device, verifying the
 * date range logic that underpins the awareness streak feature.
 *
 * Testing strategy follows the Arrange-Act-Assert pattern throughout.
 */
public class HungerRepositoryTest {

    @Test
    public void todayRange_startIsBeforeEnd() {
        long[] range = DateUtils.getTodayRange();
        assertTrue("Start of day must be before end of day", range[0] < range[1]);
    }

    @Test
    public void todayRange_spanIsExactlyOneDay() {
        long[] range = DateUtils.getTodayRange();
        long expectedSpan = DateUtils.ONE_DAY_MS;
        assertEquals("Day range must span exactly 24 hours", expectedSpan, range[1] - range[0]);
    }

    @Test
    public void todayRange_nowFallsWithinRange() {
        long[] range = DateUtils.getTodayRange();
        long now = System.currentTimeMillis();
        assertTrue("Current time must fall within today's range", now >= range[0] && now < range[1]);
    }

    @Test
    public void dayRange_offsetByOneDay_doesNotOverlapToday() {
        long[] today = DateUtils.getTodayRange();
        long[] yesterday = DateUtils.getDayRange(System.currentTimeMillis() - DateUtils.ONE_DAY_MS);
        assertTrue("Yesterday's end must equal today's start", yesterday[1] == today[0]);
    }

    @Test
    public void formatTime_doesNotReturnNull() {
        String result = DateUtils.formatTime(System.currentTimeMillis());
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void formatDate_doesNotReturnNull() {
        String result = DateUtils.formatDate(System.currentTimeMillis());
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void isToday_currentTimeReturnsTrue() {
        assertTrue(DateUtils.isToday(System.currentTimeMillis()));
    }

    @Test
    public void isToday_yesterdayReturnsFalse() {
        assertFalse(DateUtils.isToday(System.currentTimeMillis() - DateUtils.ONE_DAY_MS));
    }
}
