package com.asprotunity.exchange.server.main;

import com.asprotunity.exchange.server.*;
import com.asprotunity.time.SystemTimeProvider;
import com.asprotunity.time.TimeProvider;

public class Main {
    static EventServer eventServer;

    public static void main(String[] args) {
        int status = 0;
        Ice.Communicator ic = null;
        try {
            ic = Ice.Util.initialize(args);
            Ice.ObjectAdapter adapter =
                    ic.createObjectAdapterWithEndpoints("SimpleServerAdapter", "tcp -p " + args[0]);

            EventStore eventStore = new HSQLEventStore("jdbc:hsqldb:file:hsql/server/eventdb", "SA", "");
            IceEventPublisher icePublisher = new IceEventPublisher(eventStore);
            TimeProvider timeProvider = new SystemTimeProvider();
            eventServer = new EventServer(icePublisher, new RandomEventProducer(eventStore, timeProvider));
            adapter.add(icePublisher, ic.stringToIdentity("SimpleServer"));
            adapter.activate();
            System.out.println("Starting event server");
            eventServer.start();
            ic.waitForShutdown();
        } catch (Ice.LocalException e) {
            e.printStackTrace();
            status = 1;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            status = 1;
        }
        if (ic != null) {
            // Clean up
            //
            try {
                ic.destroy();
            } catch (Exception e) {
                System.err.println(e.getMessage());
                status = 1;
            }
            System.exit(status);
        }
    }
}
