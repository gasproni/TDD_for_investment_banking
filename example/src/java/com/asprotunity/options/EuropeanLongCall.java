package com.asprotunity.options;

import org.joda.time.DateTime;

public class EuropeanLongCall {

    private DateTime maturity;
    private double strike;

    public EuropeanLongCall(DateTime maturity, double strike) {
        this.maturity = maturity;
        this.strike = strike;
    }

    public DateTime getMaturity() {
        return maturity;
    }

    public double getStrike() {
        return strike;
    }

}
