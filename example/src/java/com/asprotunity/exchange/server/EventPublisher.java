package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;

public interface EventPublisher {
    void publish(Event event);
}
