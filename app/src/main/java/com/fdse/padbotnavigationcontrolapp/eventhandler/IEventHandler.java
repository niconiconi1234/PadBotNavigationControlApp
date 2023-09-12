package com.fdse.padbotnavigationcontrolapp.eventhandler;

public interface IEventHandler<T> {

    public void handle(T event);

    public T getLatestEvent();
}
