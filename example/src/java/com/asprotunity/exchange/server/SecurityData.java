package com.asprotunity.exchange.server;

import org.joda.time.DateTime;

public class SecurityData {
    public final DateTime timestamp;
    public final String security;
    public final double spot;
    public final double volatility;
    public String currency;

    public SecurityData(DateTime timestamp, String security, String currency, double spot, double volatility) {
        this.timestamp = timestamp;
        this.security = security;
        this.spot = spot;
        this.volatility = volatility;
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecurityData that = (SecurityData) o;

        if (Double.compare(that.spot, spot) != 0) return false;
        if (Double.compare(that.volatility, volatility) != 0) return false;
        if (!currency.equals(that.currency)) return false;
        if (!security.equals(that.security)) return false;
        if (!timestamp.equals(that.timestamp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = timestamp.hashCode();
        result = 31 * result + security.hashCode();
        temp = Double.doubleToLongBits(spot);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(volatility);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + currency.hashCode();
        return result;
    }
}
