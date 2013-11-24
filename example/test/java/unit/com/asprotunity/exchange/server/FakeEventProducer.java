package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FakeEventProducer implements EventProducer {

    private ConcurrentLinkedQueue<Event> events;
    private Lock eventsLock;
    private Condition isEmpty;
    private Condition hasSome;

    public FakeEventProducer() {
        this.events = new ConcurrentLinkedQueue<Event>();
        this.eventsLock = new ReentrantLock();
        this.isEmpty = this.eventsLock.newCondition();
        this.hasSome = this.eventsLock.newCondition();
    }

    @Override
    public void poison() {
        eventsLock.lock();
        try {
            events.add(Event.POISON_PILL);
            hasSome.signal();
        } finally {
            eventsLock.unlock();
        }
    }


    @Override
    public Event next() {
        eventsLock.lock();
        try {
            while (events.isEmpty()) {
                hasSome.await();
            }
            return events.remove();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (events.isEmpty()) {
                isEmpty.signal();
            }
            eventsLock.unlock();
        }
    }

    public void setNextEventToProduce(Event event) {
        eventsLock.lock();
        try {
            events.add(event);
            hasSome.signal();
        } finally {
            eventsLock.unlock();
        }
    }
}
