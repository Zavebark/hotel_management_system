package com.hotel.model;

import java.io.Serializable;

public class Booking implements Serializable {
    private static final long serialVersionUID = 3L;

    private int bookingId;
    private Room room;
    private Customer customer;
    private String checkInDate;
    private String checkOutDate;
    private int nights;

    public Booking(int bookingId, Room room, Customer customer, String checkInDate, String checkOutDate, int nights) {
        this.bookingId = bookingId;
        this.room = room;
        this.customer = customer;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.nights = nights;
    }

    public int getBookingId()       { return bookingId; }
    public Room getRoom()           { return room; }
    public Customer getCustomer()   { return customer; }
    public String getCheckInDate()  { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public int getNights()          { return nights; }

    public double getTotalCost() {
        return room.getPricePerNight() * nights;
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId + 
               " | Room " + room.getRoomNumber() + 
               " | " + customer.getName() + 
               " | " + checkInDate + " to " + checkOutDate + 
               " | Rs." + getTotalCost();
    }
}