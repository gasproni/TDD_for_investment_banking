package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Implementation taken from "Growing Object Oriented Software Guided by Tests",
 * by Steve Freeman and Nat Pryce
 */
class Timeout {
    private final long endTime;

    public Timeout(long duration) {
        this.endTime = System.currentTimeMillis() + duration;
    }

    public boolean hasTimedOut() {
        return timeRemaining() <= 0;
    }

    public void waitOn(Object lock) throws InterruptedException {
        long waitTime = timeRemaining();
        if (waitTime > 0) lock.wait(waitTime);
    }

    private long timeRemaining() {
        return endTime - System.currentTimeMillis();
    }
}


class EventPublisherStub implements EventPublisher {

    public final long timeoutMillis = 10;
    public Event eventPublished = null;
    private final Object eventPublishedLock = new Object();

    @Override
    public void publish(Event event) {
        synchronized (eventPublishedLock) {
            eventPublished = event;
            eventPublishedLock.notify();
        }
    }

    public void assertEventPublishedIs(Event event) throws InterruptedException {
        synchronized (eventPublishedLock) {
            while (eventPublished == null) {
                Timeout timeout = new Timeout(timeoutMillis);
                timeout.waitOn(eventPublishedLock);
                if (timeout.hasTimedOut()) {
                    fail("Wait for event published timed out");
                }
            }
            assertThat(eventPublished, is(event));
        }
    }
}

/**
 * This is used as an example of testing with stubs
 */
public class EventServerWithStubTest {

    private EventPublisherStub eventPublisher;
    private EventServer eventServer;
    private FakeEventProducer eventProducer;

    @Before
    public void setUp() {
        eventPublisher = new EventPublisherStub();
        eventProducer = new FakeEventProducer();
        eventServer = new EventServer(eventPublisher, eventProducer);
        eventServer.start();
    }

    @After
    public void tearDown() {
        eventServer.stop();
        eventServer.waitStatusStopped();
    }


    @Test
    public void publishesNewEvents() throws InterruptedException {
        final Event event = new EventBuilder().build();
        eventProducer.setNextEventToProduce(event);
        eventPublisher.assertEventPublishedIs(event);
    }

    @Test
    public void canBeStopped() throws InterruptedException {
        assertTrue(eventServer.isRunning());

        eventServer.stop();
        eventServer.waitStatusStopped();

        assertThat(eventServer.isRunning(), is(false));
        assertThat(eventServer.getStatus(), is(EventServer.Status.STOPPED));

    }

    @Test
    public void canBeStoppedMultipleTimes() throws InterruptedException {
        assertTrue(eventServer.isRunning());

        eventServer.stop();
        eventServer.waitStatusStopped();

        eventServer.stop();
        eventServer.waitStatusStopped();

        assertThat(eventServer.isRunning(), is(false));
        assertThat(eventServer.getStatus(), is(EventServer.Status.STOPPED));

    }
}
