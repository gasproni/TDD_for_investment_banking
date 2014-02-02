package com.asprotunity.exchange.middleware;

public class Event {

    public final String timestampUTC;
    public final String security;
    public final String currency;
    public final double spot;
    public final double volatility;

    public Event(String timestampUTC, String security, String currency, double spot, double volatility) {
        this.security = security;
        this.timestampUTC = timestampUTC;
        this.currency = currency;
        this.spot = spot;
        this.volatility = volatility;
    }


    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Event event = (Event) other;

        return (Double.compare(event.spot, spot) == 0) &&
                currency.equals(event.currency) &&
                security.equals(event.security) &&
                timestampUTC.equals(event.timestampUTC);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = timestampUTC.hashCode();
        result = 31 * result + security.hashCode();
        result = 31 * result + currency.hashCode();
        temp = Double.doubleToLongBits(spot);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
