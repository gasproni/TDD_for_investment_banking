package com.asprotunity.exchange.client;

import com.asprotunity.exchange.events.Event;
import com.asprotunity.time.TestTimeProvider;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class LocalBroadcasterTest {

    static class MockEventSubscriber implements EventSubscriber {

        Lock lock = new ReentrantLock();
        Condition okToProceed = lock.newCondition();
        int eventsReceived;
        private Behaviour behaviour;

        public static enum Behaviour {
            BLOCKING,
            NON_BLOCKING
        }

        public MockEventSubscriber(Behaviour behaviour) {
            this.behaviour = behaviour;
            eventsReceived = 0;
        }

        @Override
        public void notify(Event event) {
            lock.lock();
            try {
                if (behaviour == Behaviour.BLOCKING) {
                    okToProceed.await();
                }
                ++eventsReceived;
            } catch (InterruptedException e) {
                throw new RuntimeException("something happened during wait");
            } finally {
                lock.unlock();
            }
        }

        public void unblockForever() {
            lock.lock();
            try {
                behaviour = Behaviour.NON_BLOCKING;
                okToProceed.signal();
            } finally {
                lock.unlock();
            }

        }

        public int getEventsReceived() {
            lock.lock();
            try {
                return eventsReceived;
            } finally {
                lock.unlock();
            }
        }
    }


    @Test
    public void callsToNotifyEventAreNonBlocking() throws Exception {

        LocalBroadcaster broadcaster = new LocalBroadcaster();

        MockEventSubscriber blockingSubscriber = new MockEventSubscriber(MockEventSubscriber.Behaviour.BLOCKING);
        broadcaster.subscribe(blockingSubscriber);

        int numberOfEventsSent = 3;
        callNotifyEventForTimes(broadcaster, numberOfEventsSent);

        assertThat(blockingSubscriber.getEventsReceived(), is(0));

        blockingSubscriber.unblockForever();

        waitUntilAllEventsPublished(broadcaster);
        broadcaster.stopAndWait();

        assertThat(blockingSubscriber.getEventsReceived(), is(numberOfEventsSent));
    }

    private void callNotifyEventForTimes(LocalBroadcaster broadcaster, int numberOfEventsToSend) {
        Event iceEvent =
                new Event(new TestTimeProvider(new DateTime()).nowUTC(),
                        "TIK_1", "USD", 1, 0.2);

        for (int i = 0; i < numberOfEventsToSend; ++i) {
            broadcaster.notifyEvent(new com.asprotunity.exchange.middleware.Event(iceEvent.timestamp.toString(),
                    iceEvent.security,
                    iceEvent.currency, iceEvent.spot, iceEvent.volatility));
        }
    }

    private void waitUntilAllEventsPublished(LocalBroadcaster broadcaster) {
        while (broadcaster.getQueueSize() > 0) ;
    }

}
