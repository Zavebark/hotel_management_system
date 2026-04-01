package com.hotel.model;

import java.io.Serializable;

public class PaidService implements Serializable {
    private static final long serialVersionUID = 6L;

    private int roomNumber;
    private String serviceName;
    private double price;

    public PaidService(int roomNumber, String serviceName, double price) {
        this.roomNumber  = roomNumber;
        this.serviceName = serviceName;
        this.price       = price;
    }

    public int getRoomNumber()      { return roomNumber; }
    public String getServiceName()  { return serviceName; }
    public double getPrice()        { return price; }

    @Override
    public String toString() {
        return serviceName + " — ₹" + price;
    }
}