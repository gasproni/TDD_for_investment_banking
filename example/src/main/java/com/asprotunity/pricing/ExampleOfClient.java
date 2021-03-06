package com.asprotunity.pricing;

import com.asprotunity.exchange.client.EventSubscriber;
import com.asprotunity.exchange.client.ExchangeTickPublisher;
import com.asprotunity.exchange.events.Event;


public class ExampleOfClient {

    public static void main(String[] args) throws InterruptedException {
        ExchangeTickPublisher publisher = new ExchangeTickPublisher("SimpleServer", "localhost", 6666);

        EventSubscriber eventSubscriber1 = new EventSubscriber() {
            @Override
            public void notify(Event event) {
                System.out.println(event.security + " 1 ");
            }
        };

        EventSubscriber eventSubscriber2 = new EventSubscriber() {
            @Override
            public void notify(Event event) {
                System.out.println(event.security + " 2 ");
            }
        };

        publisher.subscribe(eventSubscriber1);
        publisher.subscribe(eventSubscriber2);

        publisher.waitForShutdown();
    }

}
