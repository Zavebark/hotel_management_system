package com.hotel.model;

import java.io.Serializable;

public class BillingRecord implements Serializable {
    private static final long serialVersionUID = 7L;

    private String checkoutDate;
    private int roomNumber;
    private String guestName;
    private String phone;
    private String roomType;
    private int nights;
    private double roomCharges;
    private double serviceCharges;
    private double totalBill;

    public BillingRecord(String checkoutDate, int roomNumber,
                         String guestName, String phone, String roomType,
                         int nights, double roomCharges, double serviceCharges) {
        this.checkoutDate   = checkoutDate;
        this.roomNumber     = roomNumber;
        this.guestName      = guestName;
        this.phone          = phone;
        this.roomType       = roomType;
        this.nights         = nights;
        this.roomCharges    = roomCharges;
        this.serviceCharges = serviceCharges;
        this.totalBill      = roomCharges + serviceCharges;
    }

    public String getCheckoutDate()   { return checkoutDate; }
    public int getRoomNumber()        { return roomNumber; }
    public String getGuestName()      { return guestName; }
    public String getPhone()          { return phone; }
    public String getRoomType()       { return roomType; }
    public int getNights()            { return nights; }
    public double getRoomCharges()    { return roomCharges; }
    public double getServiceCharges() { return serviceCharges; }
    public double getTotalBill()      { return totalBill; }
}