package com.github.ilooner.polygoncli.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public final class DateUtils {
    private DateUtils() {
    }

    public static DateTime nextWeekDay(DateTime dateTime) {
        do {
            dateTime = dateTime.toLocalDate().plusDays(1).toDateTimeAtStartOfDay();
        } while (isWeekend(dateTime));

        return dateTime;
    }

    public static boolean isWeekend(final DateTime dateTime) {
        return dateTime.getDayOfWeek() == DateTimeConstants.SATURDAY ||
               dateTime.getDayOfWeek() == DateTimeConstants.SUNDAY;
    }
}
