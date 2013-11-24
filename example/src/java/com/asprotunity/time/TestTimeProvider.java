package com.asprotunity.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TestTimeProvider extends TimeProvider {

    private DateTime dateTimeUTC;

    public TestTimeProvider(org.joda.time.DateTime dateTime) {
        this.dateTimeUTC = dateTime.withZone(DateTimeZone.UTC);
    }

    @Override
    public DateTime nowUTC() {
        return dateTimeUTC;
    }

    @Override
    public DateTime now(DateTimeZone tz) {
        return dateTimeUTC.withZone(tz);
    }

    public void incrementByMillis(int milliseconds) {
        dateTimeUTC = dateTimeUTC.plusMillis(milliseconds);
    }

    public void incrementBySeconds(int seconds) {
        dateTimeUTC = dateTimeUTC.plusSeconds(seconds);
    }

    public void incrementByMinutes(int minutes) {
        dateTimeUTC = dateTimeUTC.plusMinutes(minutes);
    }

    public void incrementByHours(int hours) {
        dateTimeUTC = dateTimeUTC.plusHours(hours);
    }

    public void incrementByDays(int days) {
        dateTimeUTC = dateTimeUTC.plusDays(days);
    }

    public void incrementByMonths(int months) {
        dateTimeUTC = dateTimeUTC.plusMonths(months);
    }

    public void incrementByYears(int years) {
        dateTimeUTC = dateTimeUTC.plusYears(years);
    }
}
