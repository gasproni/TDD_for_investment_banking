package com.asprotunity.exchange.server;

import com.asprotunity.time.TestTimeProvider;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExchangeTickPublisherTest {

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

        EventPublisherEngine eventPublisher = new EventPublisherEngine(eventStore);

        com.asprotunity.exchange.events.Event event = eventPublisher.queryLatestEvent(security);

        assertThat(event.security, is(security));
        assertThat(event.timestamp, is(timestamp));
        assertThat(event.spot, is(securityData.spot));
        assertThat(event.volatility, is(securityData.volatility));
        assertThat(event.currency, is(securityData.currency));

        context.assertIsSatisfied();
    }

    public static DateTime currentDateTimeUTC() {
        return new TestTimeProvider(new DateTime()).nowUTC();
    }
}
