package com.asprotunity.exchange.server;

import com.asprotunity.exchange.middleware.Event;
import com.asprotunity.time.TestTimeProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class IceEventPublisherTest {

    private Mockery context;
    private EventStore eventStore;

    @Before
    public void setUp() throws Exception {
        context = new JUnit4Mockery();
        eventStore = context.mock(EventStore.class);
    }

    @Test
    public void queriesLatestValueFromEventStore() throws Exception {

        final String security = "VOD.L";
        DateTime timestamp = currentDateTimeUTC();
        final SecurityData securityData = new SecurityData(timestamp, security, "USD", 1.23, 0.2);

        context.checking(new Expectations() {{
            oneOf(eventStore).queryLatest(security);
            will(returnValue(securityData));
        }});

        IceEventPublisher eventPublisher = new IceEventPublisher(eventStore);

        Event event = eventPublisher.queryLatestEvent(security, null);

        assertThat(event.security, is(security));
        assertThat(event.timestampUTC, is(timestamp.toString()));
        assertThat(event.spot, is(securityData.spot));
        assertThat(event.volatility, is(securityData.volatility));
        assertThat(event.currency, is(securityData.currency));

        context.assertIsSatisfied();
    }

    public static DateTime currentDateTimeUTC() {
        return new TestTimeProvider(new DateTime()).nowUTC();
    }
}
