package com.hotel;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

enum RoomType {
    RegularSingle(1500), RegularDouble(2500), DeluxeSingle(2300), DeluxeDouble(3500);

    int ratePerNight;

    RoomType(int ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public String displayName() {
        switch (this) {
            case RegularSingle: return "Regular Single";
            case RegularDouble: return "Regular Double";
            case DeluxeSingle:  return "Deluxe Single";
            case DeluxeDouble:  return "Deluxe Double";
            default: return name();
        }
    }
}

class Guest implements Serializable {
    private static final long serialVersionUID = 1L;

    String name;
    String phone;
    int daysBooked;
    int bill;
    String checkInDate;

    Guest(String name, String phone, int daysBooked) {
        this.name = name;
        this.phone = phone;
        this.daysBooked = daysBooked;
        this.bill = 0;
        this.checkInDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }

    public void setBill(int bill) {
        this.bill = bill;
    }
}

class BillingRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    String guestName;
    String guestPhone;
    int roomNumber;
    String roomType;
    int daysStayed;
    int totalBill;
    String checkOutDate;

    BillingRecord(Guest guest, HotelRoom room, int daysStayed) {
        this.guestName = guest.name;
        this.guestPhone = guest.phone;
        this.roomNumber = room.roomNumber;
        this.roomType = room.roomType.displayName();
        this.daysStayed = daysStayed;
        this.totalBill = room.roomType.ratePerNight * daysStayed;
        this.checkOutDate = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }
}

class HotelRoom implements Serializable {
    private static final long serialVersionUID = 1L;

    int roomNumber;
    RoomType roomType;
    boolean isAvailable;
    Guest guest;

    public HotelRoom(int roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.isAvailable = true;
        this.guest = null;
    }

    public int bookRoom(Guest guest) {
        if (!isAvailable) return 1;
        this.guest = guest;
        this.isAvailable = false;
        return 0;
    }

    /**
     * Checkout using booked days. Returns null on error, BillingRecord on success.
     */
    public BillingRecord checkOut() {
        if (isAvailable) return null;
        BillingRecord record = new BillingRecord(guest, this, guest.daysBooked);
        guest.setBill(roomType.ratePerNight * guest.daysBooked);
        guest = null;
        isAvailable = true;
        return record;
    }

    /**
     * Checkout with actual days stayed override.
     */
    public BillingRecord checkOut(int daysStayed) {
        if (isAvailable) return null;
        BillingRecord record = new BillingRecord(guest, this, daysStayed);
        guest.setBill(roomType.ratePerNight * daysStayed);
        guest = null;
        isAvailable = true;
        return record;
    }
}

class Storage {
    private static final String FILE_NAME = "hotel.dat";

    public static void save(List<HotelRoom> rooms, List<BillingRecord> billingHistory) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(rooms);
            oos.writeObject(billingHistory);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static Object[] load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            ArrayList<HotelRoom> rooms = (ArrayList<HotelRoom>) ois.readObject();
            ArrayList<BillingRecord> billing;
            try {
                billing = (ArrayList<BillingRecord>) ois.readObject();
            } catch (Exception e) {
                billing = new ArrayList<>();
            }
            return new Object[]{rooms, billing};
        } catch (Exception e) {
            System.out.println("No previous data found. Initializing fresh.");
            return null;
        }
    }
}

public class DataBase {

    static ArrayList<HotelRoom> rooms;
    static ArrayList<BillingRecord> billingHistory;

    static {
        Object[] loaded = Storage.load();
        if (loaded != null) {
            rooms = (ArrayList<HotelRoom>) loaded[0];
            billingHistory = (ArrayList<BillingRecord>) loaded[1];
        } else {
            rooms = new ArrayList<>();
            billingHistory = new ArrayList<>();
            init();
        }
    }

    static void init() {
        for (int i = 1; i <= 250; i++)
            rooms.add(new HotelRoom(i, RoomType.RegularSingle));
        for (int i = 251; i <= 500; i++)
            rooms.add(new HotelRoom(i, RoomType.RegularDouble));
        for (int i = 501; i <= 750; i++)
            rooms.add(new HotelRoom(i, RoomType.DeluxeSingle));
        for (int i = 751; i <= 1000; i++)
            rooms.add(new HotelRoom(i, RoomType.DeluxeDouble));
        save();
    }

    static void save() {
        Storage.save(rooms, billingHistory);
    }

    static ArrayList<Guest> listGuests() {
        ArrayList<Guest> guests = new ArrayList<>();
        for (HotelRoom room : rooms) {
            if (room.guest != null) guests.add(room.guest);
        }
        return guests;
    }

    static HotelRoom getRoomByNumber(int number) {
        for (HotelRoom room : rooms) {
            if (room.roomNumber == number) return room;
        }
        return null;
    }

    // Stats helpers
    static int totalRooms() { return rooms.size(); }

    static int occupiedRooms() {
        int count = 0;
        for (HotelRoom r : rooms) if (!r.isAvailable) count++;
        return count;
    }

    static int availableRooms() { return totalRooms() - occupiedRooms(); }

    static int occupiedByType(RoomType type) {
        int count = 0;
        for (HotelRoom r : rooms)
            if (r.roomType == type && !r.isAvailable) count++;
        return count;
    }

    static int totalByType(RoomType type) {
        int count = 0;
        for (HotelRoom r : rooms)
            if (r.roomType == type) count++;
        return count;
    }

    static HotelRoom nextAvailableOfType(RoomType type) {
        for (HotelRoom r : rooms)
            if (r.roomType == type && r.isAvailable) return r;
        return null;
    }

    static int totalRevenue() {
        int total = 0;
        for (BillingRecord b : billingHistory) total += b.totalBill;
        return total;
    }
}
