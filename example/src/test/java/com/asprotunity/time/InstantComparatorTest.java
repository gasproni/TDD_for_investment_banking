package com.asprotunity.time;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class InstantComparatorTest {

    private DateTime dateTime;

    @Before
    public void setUp() throws Exception {
        dateTime = new DateTime(1984, 10, 28, 11, 22, DateTimeZone.forID("Pacific/Johnston"));
    }

    @Test(expected = InstantComparator.InvalidParameterException.class)
    public void throwsInvalidParameterExceptionIfFirstDateTimeNull() {
        new InstantComparator().compare(null, dateTime);
    }

    @Test(expected = InstantComparator.InvalidParameterException.class)
    public void throwsInvalidParameterExceptionIfSecondDateTimeNull() {
        new InstantComparator().compare(dateTime, null);
    }

    @Test
    public void returnsZeroForSameDateTimesDifferentTimezones() {
        DateTime rhs = dateTime.toDateTime(DateTimeZone.UTC);
        assertThat(new InstantComparator().compare(dateTime, rhs), is(0));

    }

    @Test
    public void returnsZeroForSameDateTimesSameTimezones() {
        assertThat(new InstantComparator().compare(dateTime, dateTime), is(0));
    }

    @Test
    public void returnsMinusOneIfFirstDateTimePrecedesSecondForSameDateTimesDifferentTimezones() {
        DateTime rhs = dateTime.plusDays(2).toDateTime(DateTimeZone.UTC);
        assertThat(new InstantComparator().compare(dateTime, rhs), is(-1));

    }

    @Test
    public void returnsMinusOneIfFirstDateTimePrecedesSecondForSameDateTimesSameTimezones() {
        DateTime rhs = dateTime.plusDays(2);
        assertThat(new InstantComparator().compare(dateTime, rhs), is(-1));
    }


    @Test
    public void returnsOneIfFirstDateTimeFollowsSecondForSameDateTimesDifferentTimezones() {
        DateTime rhs = dateTime.plusDays(2).toDateTime(DateTimeZone.UTC);
        assertThat(new InstantComparator().compare(rhs, dateTime), is(1));

    }

    @Test
    public void returnsOneIfFirstDateTimeFollowsSecondForSameDateTimesSameTimezones() {
        DateTime rhs = dateTime.plusDays(2);
        assertThat(new InstantComparator().compare(rhs, dateTime), is(1));
    }
}
