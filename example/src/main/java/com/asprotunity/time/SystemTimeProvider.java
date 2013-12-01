package com.asprotunity.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class SystemTimeProvider extends TimeProvider {
    @Override
    public DateTime nowUTC() {
        return DateTime.now(DateTimeZone.UTC);
    }

    @Override
    public DateTime now(DateTimeZone timeZone) {
        return DateTime.now(timeZone);
    }

}
