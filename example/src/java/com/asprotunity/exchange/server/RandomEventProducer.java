package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;
import com.asprotunity.time.TimeProvider;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RandomEventProducer implements EventProducer {

    private Lock poisonLock;
    private Event poison;
    private EventStore eventStore;
    private TimeProvider timeProvider;
    public static final String[] SECURITIES = new String[]{"TIK_1", "TIK_2", "TIK_3", "TIK_4"};
    public static final String CURRENCY = "USD";

    public RandomEventProducer(EventStore eventStore, TimeProvider timeProvider) {
        this.eventStore = eventStore;
        this.timeProvider = timeProvider;
        this.poisonLock = new ReentrantLock();
        this.poison = null;
    }

    @Override
    public void poison() {
        poisonLock.lock();
        try {
            poison = Event.POISON_PILL;
        } finally {
            poisonLock.unlock();
        }
    }


    @Override
    public Event next() {
        Event poison = getAndResetPoison();
        if (poison != null) {
            return poison;
        }

        try {
            Thread.sleep((long) (new Random().nextFloat() * 1000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        SecurityData securityData = createNewSecurityData();

        eventStore.store(securityData);
        return new Event(securityData.timestamp, securityData.security, securityData.currency,
                securityData.spot, securityData.volatility);
    }

    private SecurityData createNewSecurityData() {
        String security = SECURITIES[new Random().nextInt(SECURITIES.length)];

        double spot = new Random().nextDouble() * 50;
        double volatility = new Random().nextDouble();

        return new SecurityData(timeProvider.nowUTC(), security, CURRENCY, spot, volatility);
    }


    public Event getAndResetPoison() {
        poisonLock.lock();
        try {
            Event result = poison;
            poison = null;
            return result;
        } finally {
            poisonLock.unlock();
        }
    }
}
