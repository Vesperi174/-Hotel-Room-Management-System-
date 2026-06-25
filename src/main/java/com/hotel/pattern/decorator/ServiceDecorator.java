package com.hotel.pattern.decorator;

public abstract class ServiceDecorator implements RoomServiceBill {

    protected final RoomServiceBill decoratedBill;

    public ServiceDecorator(RoomServiceBill decoratedBill) {
        this.decoratedBill = decoratedBill;
    }
}