package com.hotel.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 2L;

    private int customerId;
    private String name;
    private String contactNumber;

    public Customer(int customerId, String name, String contactNumber) {
        this.customerId = customerId;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public int getCustomerId()       { return customerId; }
    public String getName()          { return name; }
    public String getContactNumber() { return contactNumber; }

    public void setName(String name)                  { this.name = name; }
    public void setContactNumber(String contactNumber){ this.contactNumber = contactNumber; }

    @Override
    public String toString() {
        return "Customer #" + customerId + " | " + name + " | " + contactNumber;
    }
}
