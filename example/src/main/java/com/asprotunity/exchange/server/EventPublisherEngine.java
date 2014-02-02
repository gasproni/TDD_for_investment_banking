package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;
import com.asprotunity.exchange.middleware.RemoteEventPublisher;
import com.asprotunity.exchange.middleware.Subscriber;
import com.asprotunity.joker.proxy.ServiceAddress;
import com.asprotunity.joker.proxy.ServiceProxy;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventPublisherEngine implements RemoteEventPublisher, EventPublisher {

    ConcurrentHashMap<ServiceAddress, ServiceProxy<Subscriber>> subscribers;
    private EventStore eventStore;

    public EventPublisherEngine(EventStore eventStore) {
        this.eventStore = eventStore;
        subscribers = new ConcurrentHashMap<>();
    }


    @Override
    public void subscribe(ServiceProxy<Subscriber> sub) {
        subscribers.put(sub.address(), sub);
    }

    @Override
    public void unsubscribe(ServiceProxy<Subscriber> sub) {
        subscribers.remove(sub.address());
    }

    @Override
    public Event queryLatestEvent(String security) {
        return createEvent(eventStore.queryLatest(security));
    }


    @Override
    public void publish(Event event) {
        Iterator<Map.Entry<ServiceAddress, ServiceProxy<Subscriber>>> it = subscribers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ServiceAddress, ServiceProxy<Subscriber>> entry = it.next();
            try {
                entry.getValue().service().notifyEvent(
                        new com.asprotunity.exchange.middleware.Event(event.timestamp.toString(),
                                event.security, event.currency, event.spot, event.volatility));
            } catch (Throwable e) {
                it.remove();
            }
        }
    }

    public static Event createEvent(SecurityData securityData) {
        return new Event(securityData.timestamp, securityData.security,
                securityData.currency, securityData.spot, securityData.volatility);
    }
}
