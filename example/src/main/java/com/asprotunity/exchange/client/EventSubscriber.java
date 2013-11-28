package com.asprotunity.exchange.client;

import com.asprotunity.exchange.events.Event;

public interface EventSubscriber {
    public void notify(Event event);
}
