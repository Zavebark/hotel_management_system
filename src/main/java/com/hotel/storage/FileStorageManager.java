package com.hotel.storage;

import com.hotel.model.BillingRecord;
import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.PaidService;
import com.hotel.model.Room;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorageManager {

    private static final String ROOMS_FILE    = "data/rooms.dat";
    private static final String CUSTOMERS_FILE = "data/customers.dat";
    private static final String BOOKINGS_FILE  = "data/bookings.dat";
    private static final String SERVICES_FILE  = "data/paidservices.dat";
    private static final String BILLING_FILE   = "data/billing.dat";

    static {
        new File("data").mkdirs();
    }

    // ── Rooms ──
    public static void saveRooms(List<Room> rooms) {
        save(ROOMS_FILE, rooms);
    }
    @SuppressWarnings("unchecked")
    public static List<Room> loadRooms() {
        return (List<Room>) load(ROOMS_FILE);
    }

    // ── Customers ──
    public static void saveCustomers(List<Customer> customers) {
        save(CUSTOMERS_FILE, customers);
    }
    @SuppressWarnings("unchecked")
    public static List<Customer> loadCustomers() {
        return (List<Customer>) load(CUSTOMERS_FILE);
    }

    // ── Bookings ──
    public static void saveBookings(List<Booking> bookings) {
        save(BOOKINGS_FILE, bookings);
    }
    @SuppressWarnings("unchecked")
    public static List<Booking> loadBookings() {
        return (List<Booking>) load(BOOKINGS_FILE);
    }

    // ── Paid Services ──
    public static void savePaidServices(List<PaidService> services) {
        save(SERVICES_FILE, services);
    }
    @SuppressWarnings("unchecked")
    public static List<PaidService> loadPaidServices() {
        return (List<PaidService>) load(SERVICES_FILE);
    }

    // ── Billing Records ──
    public static void saveBillingRecords(List<BillingRecord> records) {
        save(BILLING_FILE, records);
    }
    @SuppressWarnings("unchecked")
    public static List<BillingRecord> loadBillingRecords() {
        return (List<BillingRecord>) load(BILLING_FILE);
    }

    // ── Generic helpers ──
    private static void save(String path, Object data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("Error saving " + path + ": " + e.getMessage());
        }
    }

    private static Object load(String path) {
        File f = new File(path);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(f))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading " + path + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}