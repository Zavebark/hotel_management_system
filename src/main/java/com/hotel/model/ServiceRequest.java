package com.hotel.model;

import java.io.Serializable;

public class ServiceRequest implements Serializable {
    private static final long serialVersionUID = 4L;

    private int serviceId;
    private int roomNumber;
    private String serviceType;
    private String status; // "Pending" or "Completed"

    public ServiceRequest(int serviceId, int roomNumber, String serviceType) {
        this.serviceId = serviceId;
        this.roomNumber = roomNumber;
        this.serviceType = serviceType;
        this.status = "Pending";
    }

    public int getServiceId()      { return serviceId; }
    public int getRoomNumber()     { return roomNumber; }
    public String getServiceType() { return serviceType; }
    public String getStatus()      { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Service #" + serviceId + " | Room " + roomNumber +
               " | " + serviceType + " | " + status;
    }
}