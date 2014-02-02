package com.asprotunity.exchange.server.main;

import com.asprotunity.exchange.middleware.RemoteEventPublisher;
import com.asprotunity.exchange.server.*;
import com.asprotunity.joker.server.HTTPBroker;
import com.asprotunity.time.SystemTimeProvider;
import com.asprotunity.time.TimeProvider;

import java.sql.SQLException;

public class Main {
    static EventServer eventServer;

    public static void main(String[] args) throws SQLException {

            EventStore eventStore = new HSQLEventStore("jdbc:hsqldb:file:hsql/server/eventdb", "SA", "");
            EventPublisherEngine eventPublisher = new EventPublisherEngine(eventStore);
            TimeProvider timeProvider = new SystemTimeProvider();
            eventServer = new EventServer(eventPublisher, new RandomEventProducer(eventStore, timeProvider));

            HTTPBroker broker = new HTTPBroker(Integer.parseInt(args[0]));
            broker.start();
            broker.registerService("SimpleServer", eventPublisher, RemoteEventPublisher.class);
            System.out.println("Starting event server");

            eventServer.start();
            broker.join();
    }
}
