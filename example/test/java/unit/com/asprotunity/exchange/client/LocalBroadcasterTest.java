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

        public void waitEventsReceivedEqualTo(int number) {
            while (getEventsReceived() < number) ;
        }

    }


    @Test
    public void notificationCallInNonBlocking() throws Exception {

        LocalBroadcaster broadcaster = new LocalBroadcaster();

        MockEventSubscriber blockingSubscriber = new MockEventSubscriber(MockEventSubscriber.Behaviour.BLOCKING);
        broadcaster.subscribe(blockingSubscriber);

        com.asprotunity.exchange.middleware.Event iceEvent =
                new com.asprotunity.exchange.middleware.Event(new TestTimeProvider(new DateTime()).nowUTC().toString(),
                        "TIK_1", "USD", 1, 0.2);

        broadcaster.notifyEvent(iceEvent, null);
        broadcaster.notifyEvent(iceEvent, null);
        broadcaster.notifyEvent(iceEvent, null);

        blockingSubscriber.unblockForever();

        waitUntilAllEventsPublished(broadcaster);
        broadcaster.stopAndWait();

        assertThat(blockingSubscriber.getEventsReceived(), is(3));
    }

    private void waitUntilAllEventsPublished(LocalBroadcaster broadcaster) {
        while (broadcaster.getQueueSize() > 0) ;
    }

}