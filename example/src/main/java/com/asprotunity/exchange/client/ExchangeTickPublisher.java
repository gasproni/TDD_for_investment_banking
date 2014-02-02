package com.asprotunity.exchange.client;

import com.asprotunity.exchange.events.Event;
import com.asprotunity.exchange.middleware.RemoteEventPublisher;
import com.asprotunity.exchange.middleware.Subscriber;
import com.asprotunity.joker.client.HTTPServiceProxyMaker;
import com.asprotunity.joker.proxy.ServiceAddress;
import com.asprotunity.joker.proxy.ServiceProxy;
import com.asprotunity.joker.server.HTTPBroker;

public class ExchangeTickPublisher {

    private final ServiceProxy<Subscriber> broadcasterProxy;
    private HTTPBroker broker;
    private final ServiceProxy<RemoteEventPublisher> publisher;
    private LocalBroadcaster broadcaster;

    public ExchangeTickPublisher(String serverName, String serverHost, int port) {
        try {
            broker = new HTTPBroker(0);
            broker.start();

            HTTPServiceProxyMaker proxyMaker = new HTTPServiceProxyMaker();

            publisher = proxyMaker.make(new ServiceAddress(serverHost, port, serverName),
                    RemoteEventPublisher.class);

            broadcaster = new LocalBroadcaster();
            broadcasterProxy = broker.registerService("SimpleClient", broadcaster, Subscriber.class);
            publisher.service().subscribe(broadcasterProxy);
        } catch (Throwable e) {
            cleanup();
            throw new RuntimeException(e);
        }
    }

    public void subscribe(EventSubscriber eventSubscriber) {
        broadcaster.subscribe(eventSubscriber);
    }


    public void unsubscribe(EventSubscriber eventSubscriber) {
        broadcaster.unsubscribe(eventSubscriber);
    }

    public Event queryLatestEvent(String security) {
        return publisher.service().queryLatestEvent(security);
    }

    public void cleanup() {
        try {
            if (broadcasterProxy != null) {
                publisher.service().unsubscribe(broadcasterProxy);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void waitForShutdown() {
        broker.join();
        broadcaster.stopAndWait();
        cleanup();
    }
}
