package com.asprotunity.exchange.server;

import com.asprotunity.exchange.events.Event;

import java.util.EnumSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class EventServer implements Runnable {

    private EventPublisher eventPublisher;
    private Status status;
    private ReentrantLock statusLock;
    private Condition statusStopped;
    private EventProducer eventProducer;
    private Thread thread;

    public enum Status {
        STARTING,
        RUNNING,
        STOPPING,
        STOPPED
    }


    public EventServer(EventPublisher eventPublisher, EventProducer eventProducer) {
        this.eventPublisher = eventPublisher;
        this.status = Status.STOPPED;
        this.statusLock = new ReentrantLock();
        this.statusStopped = statusLock.newCondition();
        this.eventProducer = eventProducer;
        this.thread = new Thread(this);
    }

    public void start() {
        statusLock.lock();
        try {
            setStatus(Status.STARTING);
            thread.start();
        } finally {
            statusLock.unlock();
        }
    }

    public void stop() {
        statusLock.lock();
        try {
            if (!EnumSet.of(Status.STOPPED, Status.STOPPING).contains(status)) {
                setStatus(Status.STOPPING);
                eventProducer.poison();
            }
        } finally {
            statusLock.unlock();
        }
    }

    public void waitStatusStopped() {
        statusLock.lock();
        try {
            while (status != Status.STOPPED) {
                statusStopped.await();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            statusLock.unlock();
        }
    }

    @Override
    public void run() {
        setStatus(Status.RUNNING);
        while (isRunning()) {
            try {
                Event event = eventProducer.next();

                if (!Event.POISON_PILL.equals(event)) {
                    eventPublisher.publish(event);
                } else {
                    setStatus(Status.STOPPED);
                }

            } catch (Throwable e) {
                setStatus(Status.STOPPED);
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isRunning() {
        statusLock.lock();
        try {
            return status != Status.STOPPED;
        } finally {
            statusLock.unlock();
        }
    }


    public Status getStatus() {
        statusLock.lock();
        try {
            return status;
        } finally {
            statusLock.unlock();
        }
    }

    private void setStatus(Status newStatus) {
        statusLock.lock();
        try {
            status = newStatus;
            if (status == Status.STOPPED) {
                statusStopped.signalAll();
            }
        } finally {
            statusLock.unlock();
        }
    }

}
