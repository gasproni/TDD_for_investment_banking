module middleware {

struct Event {

    string timestampUTC;
    string security;
    string currency;
    double spot;
    double volatility;

};

interface Subscriber {

  void notifyEvent(Event ev);

};


interface Publisher {

    void subscribe(Subscriber* sub);
    void unsubscribe(Subscriber* sub);

    Event queryLatestEvent(string security);

};


};