package com.asprotunity.exchange.eventconversion;

import com.asprotunity.exchange.events.Event;
import org.joda.time.DateTime;

public class EventConverter {
    public static com.asprotunity.exchange.events.Event fromIceEvent(com.asprotunity.exchange.middleware.Event event) {
        return new com.asprotunity.exchange.events.Event(DateTime.parse(event.timestampUTC),
                event.security, event.currency, event.spot, event.volatility);
    }

    public static com.asprotunity.exchange.middleware.Event createIceEvent(Event event) {
        com.asprotunity.exchange.middleware.Event result = new com.asprotunity.exchange.middleware.Event();
        result.security = event.security;
        result.spot = event.spot;
        result.currency = event.currency;
        result.timestampUTC = event.timestamp.toString();

        return result;
    }
}
