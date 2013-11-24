package com.asprotunity.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public abstract class TimeProvider {

    abstract public DateTime nowUTC();

    abstract public DateTime now(DateTimeZone tz);

    public DateTime createUTC(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        return create(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, DateTimeZone.UTC);
    }

    public DateTime create(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour, DateTimeZone timeZone) {
        return new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, timeZone);
    }

    public DateTime parseSameTimezone(String dateTime) {
        return DateTime.parse(dateTime);
    }

    public DateTime parseToTimezone(String dateTime, DateTimeZone timeZone) {
        return parseSameTimezone(dateTime).withZone(timeZone);
    }

    public DateTime parseToUTC(String dateTime) {
        return parseToTimezone(dateTime, DateTimeZone.UTC);
    }
}
