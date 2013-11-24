package com.asprotunity.exchange.client;

import Ice.Current;
import com.asprotunity.exchange.eventconversion.EventConverter;
import com.asprotunity.exchange.middleware.Event;
import com.asprotunity.exchange.middleware._SubscriberDisp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LocalBroadcaster extends _SubscriberDisp implements Runnable {

    private static class LocalId {

        private final EventSubscriber sub;

        public LocalId(EventSubscriber sub) {
            this.sub = sub;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            LocalId localId = (LocalId) other;

            return sub == localId.sub;
        }

        @Override
        public int hashCode() {
            return sub.hashCode();
        }
    }

    ConcurrentHashMap<LocalId, EventSubscriber> subscribers;

    LinkedList<com.asprotunity.exchange.events.Event> eventsQueue;
    Lock eventsQueueLock;
    Condition notEmpty;
    Thread dispatcherThread;

    public LocalBroadcaster() {
        subscribers = new ConcurrentHashMap<LocalId, EventSubscriber>();
        eventsQueue = new LinkedList<com.asprotunity.exchange.events.Event>();
        eventsQueueLock = new ReentrantLock();
        notEmpty = eventsQueueLock.newCondition();
        dispatcherThread = new Thread(this);
        dispatcherThread.start();
    }

    public void stopAndWait() {
        addToQueue(com.asprotunity.exchange.events.Event.POISON_PILL);
        try {
            dispatcherThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void notifyEvent(Event ev, Current current) {
        com.asprotunity.exchange.events.Event event = EventConverter.fromIceEvent(ev);
        addToQueue(event);
    }

    @Override
    public void run() {

        while (true) {
            com.asprotunity.exchange.events.Event event = removeFromQueue();
            if (!event.equals(com.asprotunity.exchange.events.Event.POISON_PILL)) {
                Iterator<Map.Entry<LocalId, EventSubscriber>> it = subscribers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<LocalId, EventSubscriber> entry = it.next();
                    try {
                        entry.getValue().notify(event);
                    } catch (Throwable e) {
                        it.remove();
                    }
                }
            } else {
                break;
            }
        }
    }

    private void addToQueue(com.asprotunity.exchange.events.Event event) {
        eventsQueueLock.lock();
        try {
            eventsQueue.add(event);
            notEmpty.signalAll();
        } finally {
            eventsQueueLock.unlock();
        }
    }

    public int getQueueSize() {
        eventsQueueLock.lock();
        try {
            return eventsQueue.size();
        } finally {
            eventsQueueLock.unlock();
        }
    }

    private com.asprotunity.exchange.events.Event removeFromQueue() {
        eventsQueueLock.lock();
        try {
            while (eventsQueue.size() == 0) {
                notEmpty.await();
            }
            return eventsQueue.remove();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            eventsQueueLock.unlock();
        }
    }

    public void subscribe(EventSubscriber subscriber) {
        subscribers.put(new LocalId(subscriber), subscriber);
    }

    public void unsubscribe(EventSubscriber subscriber) {
        subscribers.remove(new LocalId(subscriber));
    }

}
