package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class EventBuilder {
    Event build() {
        return new Event(DateTime.now(DateTimeZone.UTC), "security", "USD", 1.2, 0.1);
    }
}
