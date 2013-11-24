package com.asprotunity.time;

import org.joda.time.DateTime;

import java.util.Comparator;

/**
 * Returns 0 if two datetimes represent the same instant, i.e., they are the
 * same independently on their timezones
 */
public class InstantComparator implements Comparator<DateTime> {

    public static class InvalidParameterException extends RuntimeException {
        public InvalidParameterException(String message) {
            super(message);
        }
    }

    @Override
    public int compare(DateTime lhs, DateTime rhs) {
        if (lhs == null) {
            throw new InvalidParameterException("lhs cannot be null");
        }
        if (rhs == null) {
            throw new InvalidParameterException("rhs cannot be null");
        }
        return lhs.toInstant().compareTo(rhs.toInstant());
    }
}
