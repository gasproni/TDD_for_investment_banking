package com.asprotunity.exchange.server;

public interface EventStore {

    SecurityData queryLatest(String security);

    void store(SecurityData data);

}
