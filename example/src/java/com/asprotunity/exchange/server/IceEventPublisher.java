package com.asprotunity.exchange.server;

import Ice.Current;
import Ice.Identity;
import com.asprotunity.exchange.eventconversion.EventConverter;
import com.asprotunity.exchange.events.Event;
import com.asprotunity.exchange.middleware.SubscriberPrx;
import com.asprotunity.exchange.middleware._PublisherDisp;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IceEventPublisher extends _PublisherDisp implements EventPublisher {

    ConcurrentHashMap<Identity, SubscriberPrx> subscribers;
    private EventStore eventStore;

    public IceEventPublisher(EventStore eventStore) {
        this.eventStore = eventStore;
        subscribers = new ConcurrentHashMap<Identity, SubscriberPrx>();
    }


    @Override
    public void subscribe(SubscriberPrx sub, Current current) {
        subscribers.put(sub.ice_getIdentity(), sub);
    }

    @Override
    public void unsubscribe(SubscriberPrx sub, Current current) {
        subscribers.remove(sub.ice_getIdentity());
    }

    @Override
    public com.asprotunity.exchange.middleware.Event queryLatestEvent(String security, Current __current) {
        return createIceEvent(eventStore.queryLatest(security));
    }


    @Override
    public void publish(Event event) {
        com.asprotunity.exchange.middleware.Event iceEvent = EventConverter.createIceEvent(event);
        Iterator<Map.Entry<Identity, SubscriberPrx>> it = subscribers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Identity, SubscriberPrx> entry = it.next();
            try {
                entry.getValue().notifyEvent(iceEvent);
            } catch (Throwable e) {
                it.remove();
            }
        }
    }

    public static com.asprotunity.exchange.middleware.Event createIceEvent(SecurityData securityData) {
        com.asprotunity.exchange.middleware.Event result = new com.asprotunity.exchange.middleware.Event();
        result.security = securityData.security;
        result.spot = securityData.spot;
        result.volatility = securityData.volatility;
        result.currency = securityData.currency;
        result.timestampUTC = securityData.timestamp.toString();

        return result;
    }
}
