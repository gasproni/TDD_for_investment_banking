package com.asprotunity.exchange.middleware;


import com.asprotunity.exchange.events.Event;
import com.asprotunity.joker.proxy.ServiceProxy;

public interface RemoteEventPublisher {

    void subscribe(ServiceProxy<Subscriber> sub);

    void unsubscribe(ServiceProxy<Subscriber> sub);

    Event queryLatestEvent(String security);
}
