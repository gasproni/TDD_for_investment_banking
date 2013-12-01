package com.asprotunity.exchange.events;

import org.joda.time.DateTime;

public class Event {
    public static final Event POISON_PILL = new Event();

    public final DateTime timestamp;
    public final String security;
    public final String currency;
    public final double spot;
    public final double volatility;

    public Event(DateTime timestamp, String security, String currency, double spot, double volatility) {
        this.security = security;
        this.timestamp = timestamp;
        this.currency = currency;
        this.spot = spot;
        this.volatility = volatility;
    }

    /**
     * Used only for the poison pill
     */
    private Event() {
        this.security = "";
        this.timestamp = DateTime.parse("1970-11-11T00:00:00.0+00");
        this.currency = "";
        this.spot = 0;
        this.volatility = 0;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Event event = (Event) other;

        return (Double.compare(event.spot, spot) == 0) &&
                currency.equals(event.currency) &&
                security.equals(event.security) &&
                timestamp.equals(event.timestamp);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = timestamp.hashCode();
        result = 31 * result + security.hashCode();
        result = 31 * result + currency.hashCode();
        temp = Double.doubleToLongBits(spot);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
