package com.asprotunity.exchange.client;

import Ice.Communicator;
import Ice.ObjectAdapter;
import Ice.ObjectPrx;
import com.asprotunity.exchange.eventconversion.EventConverter;
import com.asprotunity.exchange.events.Event;
import com.asprotunity.exchange.middleware.PublisherPrx;
import com.asprotunity.exchange.middleware.PublisherPrxHelper;
import com.asprotunity.exchange.middleware.SubscriberPrx;
import com.asprotunity.exchange.middleware.SubscriberPrxHelper;

public class IcePublisher {

    private final SubscriberPrx broadcasterPrx;
    private Communicator ic;
    private final ObjectAdapter adapter;
    private final PublisherPrx publisher;
    private LocalBroadcaster broadcaster;

    public IcePublisher(String serverConnectionString) {
        try {
            ic = Ice.Util.initialize(new String[0]);
            adapter = ic.createObjectAdapterWithEndpoints("SimpleClientAdapter", "default");
            adapter.activate();

            Ice.ObjectPrx iceExchangeServer = ic.stringToProxy(serverConnectionString);
            publisher = PublisherPrxHelper.checkedCast(iceExchangeServer);
            if (publisher == null) {
                throw new RuntimeException("Invalid proxy");
            }

            broadcaster = new LocalBroadcaster();
            ObjectPrx broadcasterPrxObject = adapter.addWithUUID(broadcaster);
            broadcasterPrx = SubscriberPrxHelper.checkedCast(broadcasterPrxObject);
            publisher.subscribe(broadcasterPrx);
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
        return EventConverter.fromIceEvent(publisher.queryLatestEvent(security));
    }

    public void cleanup() {
        try {
            if (broadcasterPrx != null) {
                publisher.unsubscribe(broadcasterPrx);
                adapter.remove(broadcasterPrx.ice_getIdentity());
            }
            if (ic != null) {
                ic.destroy();
                ic = null;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void waitForShutdown() {
        ic.waitForShutdown();
        broadcaster.stopAndWait();
        cleanup();
    }
}
