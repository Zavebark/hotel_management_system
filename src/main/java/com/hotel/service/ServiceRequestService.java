package com.hotel.service;

import com.hotel.model.ServiceRequest;
import com.hotel.storage.FileStorageManager;

import java.util.List;

public class ServiceRequestService {

    private List<ServiceRequest> services;

    public ServiceRequestService() {
        this.services = FileStorageManager.loadServices();
    }

    public void addService(int serviceId, int roomNumber, String serviceType) {
        services.add(new ServiceRequest(serviceId, roomNumber, serviceType));
        FileStorageManager.saveServices(services);
    }

    public void markDone(int serviceId) {
        for (ServiceRequest s : services) {
            if (s.getServiceId() == serviceId) {
                s.setStatus("Completed");
                break;
            }
        }
        FileStorageManager.saveServices(services);
    }

    public List<ServiceRequest> getAllServices() {
        return services;
    }

    public int generateServiceId() {
        if (services.isEmpty()) return 1;
        int max = 0;
        for (ServiceRequest s : services) {
            if (s.getServiceId() > max) max = s.getServiceId();
        }
        return max + 1;
    }
}