package com.github.ilooner.polygoncli.utils;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {
    @Test
    public void isWeekendTest() {
        var date = new LocalDate(2020, 1, 6).toDateTimeAtStartOfDay();

        for (int i = 0; i < 7; i++) {
            Assert.assertEquals(i >= 5, DateUtils.isWeekend(date));
            date = date.toLocalDate().plusDays(1).toDateTimeAtStartOfDay();
        }
    }

    @Test
    public void nextDayTest() {
        var date = new LocalDate(2020, 1, 6).toDateTimeAtStartOfDay();

        for (int i = 0; i < 4; i++) {
            Assert.assertFalse(DateUtils.isWeekend(date));
            final var next = DateUtils.nextWeekDay(date);
            Assert.assertEquals(1, next.getDayOfYear() - date.getDayOfYear());
            date = next;
        }

        Assert.assertFalse(DateUtils.isWeekend(date));
        final var next = DateUtils.nextWeekDay(date);
        Assert.assertEquals(3, next.getDayOfYear() - date.getDayOfYear());
    }
}
