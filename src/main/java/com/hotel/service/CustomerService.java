package com.hotel.service;

import com.hotel.model.Customer;
import com.hotel.storage.FileStorageManager;
import java.util.List;

public class CustomerService {

    private List<Customer> customers;

    public CustomerService() {
        this.customers = FileStorageManager.loadCustomers();
    }

    public boolean addCustomer(int customerId, String name, String contactNumber) {
        for (Customer c : customers) {
            if (c.getCustomerId() == customerId) {
                return false;
            }
        }
        customers.add(new Customer(customerId, name, contactNumber));
        FileStorageManager.saveCustomers(customers);
        return true;
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }

    public Customer getCustomerById(int customerId) {
        for (Customer c : customers) {
            if (c.getCustomerId() == customerId) {
                return c;
            }
        }
        return null;
    }

    public int generateCustomerId() {
        if (customers.isEmpty()) return 1;
        int max = 0;
        for (Customer c : customers) {
            if (c.getCustomerId() > max) max = c.getCustomerId();
        }
        return max + 1;
    }

    public void deleteCustomer(int customerId) {
    customers.removeIf(c -> c.getCustomerId() == customerId);
    FileStorageManager.saveCustomers(customers);
}
}