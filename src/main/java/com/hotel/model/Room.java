package com.hotel.model;

import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private String roomType;
    private double pricePerNight;
    private boolean isBooked;

    public Room(int roomNumber, String roomType, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = pricePerNight;
        this.isBooked = false;
    }

    public int getRoomNumber()       { return roomNumber; }
    public String getRoomType()      { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isBooked()        { return isBooked; }

    public void setRoomType(String roomType)         { this.roomType = roomType; }
    public void setPricePerNight(double price)       { this.pricePerNight = price; }
    public void setBooked(boolean booked)            { this.isBooked = booked; }

    @Override
    public String toString() {
        return "Room " + roomNumber + " | " + roomType + 
               " | Rs." + pricePerNight + "/night | " + 
               (isBooked ? "Booked" : "Available");
    }
}