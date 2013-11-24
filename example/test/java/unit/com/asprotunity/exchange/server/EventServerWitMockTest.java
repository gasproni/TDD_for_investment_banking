package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

public class EventServerWitMockTest {

    private Mockery context;
    private EventPublisher eventPublisher;
    private EventServer eventServer;
    private FakeEventProducer eventProducer;

    @Before
    public void setUp() {

        context = new JUnit4Mockery() {{
            setThreadingPolicy(new Synchroniser());
        }};

        eventPublisher = context.mock(EventPublisher.class);
        eventProducer = new FakeEventProducer();
        eventServer = new EventServer(eventPublisher, eventProducer);
        eventServer.start();
    }

    @After
    public void tearDown() {
        eventServer.stop();
        eventServer.waitStatusStopped();
    }

    /**
     * This test is for illustration purposes only. The Thread.sleep inside is an approach
     * to be avoided.
     */
    @Test
    public void publishesNewEvents() throws InterruptedException {

        final Event event = new EventBuilder().build();
        context.checking(new Expectations() {{
            oneOf(eventPublisher).publish(event);
            ignoring(eventPublisher);
        }});

        eventProducer.setNextEventToProduce(event);

        Thread.sleep(10);

        context.assertIsSatisfied();
    }

    @Test
    public void canBeStopped() throws InterruptedException {
        assertTrue(eventServer.isRunning());

        stopAndWaitForNumberOfTimes(1);

        assertThatEventServerIsStopped();

    }


    @Test
    public void canBeStoppedMultipleTimes() throws InterruptedException {
        assertTrue(eventServer.isRunning());

        stopAndWaitForNumberOfTimes(2);

        assertThatEventServerIsStopped();

    }

    private void assertThatEventServerIsStopped() {
        assertThat(eventServer.isRunning(), is(false));
        assertThat(eventServer.getStatus(), is(EventServer.Status.STOPPED));
    }

    private void stopAndWaitForNumberOfTimes(int numberOfTimes) {
        for (int i = 0; i < numberOfTimes; ++i) {
            eventServer.stop();
            eventServer.waitStatusStopped();
        }
    }

}
