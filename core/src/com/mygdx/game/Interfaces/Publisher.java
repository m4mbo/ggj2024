package com.mygdx.game.Interfaces;

public interface Publisher {
    public void addSubscriber(Subscriber subscriber);
    public void removeSubscriber(Subscriber subscriber);
    public void notifyAllSubscribers(String flag);
    public void notifySubscriber(String flag, Subscriber subscriber);
}
