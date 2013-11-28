package com.asprotunity.time;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import static com.asprotunity.time.InstantMatcher.matchesInstant;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


class InstantMatcher extends BaseMatcher<DateTime> {


    private DateTime toMatch;

    public InstantMatcher(DateTime toMatch) {

        this.toMatch = toMatch;
    }


    @Override
    public boolean matches(Object other) {
        DateTime otherInstant = (DateTime) other;
        return new InstantComparator().compare(toMatch, otherInstant) == 0;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("The times don't match");
    }

    public static InstantMatcher matchesInstant(DateTime dateTime) {
        return new InstantMatcher(dateTime);
    }
}

public class TestTimeProviderTest {

    private DateTime dateTime;
    private TestTimeProvider testTimeProvider;

    @Before
    public void setUp() throws Exception {
        dateTime = new DateTime(DateTimeZone.forID("Pacific/Pago_Pago"));
        testTimeProvider = new TestTimeProvider(dateTime);
    }

    @Test
    public void initializesDateTimeCorrectly() {
        assertThat(testTimeProvider.nowUTC().toInstant(), is(dateTime.toInstant()));
        assertThat(testTimeProvider.nowUTC().getZone(), is(DateTimeZone.UTC));
    }


    @Test
    public void incrementsTimeInMillisecondsCorrectly() {
        int milliseconds = 10;
        testTimeProvider.incrementByMillis(milliseconds);
        assertThat(testTimeProvider.nowUTC(), matchesInstant(dateTime.plusMillis(milliseconds)));

    }

    @Test
    public void incrementsTimeInSecondsCorrectly() {
        int seconds = 10;
        testTimeProvider.incrementBySeconds(seconds);
        assertThat(testTimeProvider.nowUTC(), matchesInstant(dateTime.plusSeconds(seconds)));

    }

    @Test
    public void incrementsTimeInMinutesCorrectly() {
        int minutes = 10;
        testTimeProvider.incrementByMinutes(minutes);
        assertThat(testTimeProvider.nowUTC(), matchesInstant(dateTime.plusMinutes(minutes)));

    }

    @Test
    public void incrementsTimeInHoursCorrectly() {
        int hours = 10;
        testTimeProvider.incrementByHours(hours);
        assertThat(testTimeProvider.nowUTC(), matchesInstant(dateTime.plusHours(hours)));

    }

    @Test
    public void incrementsTimeInDaysCorrectly() {
        int days = 10;
        testTimeProvider.incrementByDays(days);
        assertThat(testTimeProvider.nowUTC(), matchesInstant(dateTime.plusDays(days)));

    }

    @Test
    public void incrementsTimeInMonthsCorrectly() {
        int months = 10;
        testTimeProvider.incrementByMonths(months);
        assertThat(testTimeProvider.nowUTC(), matchesInstant(dateTime.plusMonths(months)));

    }

    @Test
    public void incrementsTimeInYearsCorrectly() {
        int years = 10;
        testTimeProvider.incrementByYears(years);
        assertThat(testTimeProvider.nowUTC(), matchesInstant(dateTime.plusYears(years)));

    }

}
