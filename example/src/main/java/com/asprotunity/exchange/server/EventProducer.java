package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;

public interface EventProducer {

    /**
     * It will make the next event in the queue the Event.POISON_PILL.
     * Used to awaken the event producer in case it is blocked waiting
     * to generate more events, when the system needs to be stopped.
     */
    void poison();

    Event next();
}
