package com.hotel.storage;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.model.ServiceRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorageManager {

    private static final String ROOMS_FILE    = "data/rooms.dat";
    private static final String CUSTOMERS_FILE = "data/customers.dat";
    private static final String BOOKINGS_FILE  = "data/bookings.dat";

    static {
        new File("data").mkdirs();
    }

    // ─────────────────── ROOMS ───────────────────

    public static void saveRooms(List<Room> rooms) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ROOMS_FILE))) {
            oos.writeObject(rooms);
        } catch (IOException e) {
            System.out.println("Error saving rooms: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Room> loadRooms() {
        File f = new File(ROOMS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(f))) {
            return (List<Room>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading rooms: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ─────────────────── CUSTOMERS ───────────────────

    public static void saveCustomers(List<Customer> customers) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(CUSTOMERS_FILE))) {
            oos.writeObject(customers);
        } catch (IOException e) {
            System.out.println("Error saving customers: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Customer> loadCustomers() {
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(f))) {
            return (List<Customer>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading customers: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ─────────────────── BOOKINGS ───────────────────

    public static void saveBookings(List<Booking> bookings) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(BOOKINGS_FILE))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.out.println("Error saving bookings: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Booking> loadBookings() {
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(f))) {
            return (List<Booking>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static final String SERVICES_FILE = "data/services.dat";

public static void saveServices(List<ServiceRequest> services) {
    try (ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(SERVICES_FILE))) {
        oos.writeObject(services);
    } catch (IOException e) {
        System.out.println("Error saving services: " + e.getMessage());
    }
}

@SuppressWarnings("unchecked")
public static List<ServiceRequest> loadServices() {
    File f = new File(SERVICES_FILE);
    if (!f.exists()) return new ArrayList<>();
    try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(f))) {
        return (List<ServiceRequest>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
        System.out.println("Error loading services: " + e.getMessage());
        return new ArrayList<>();
    }
}
}