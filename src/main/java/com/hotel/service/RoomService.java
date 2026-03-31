package com.hotel.service;

import com.hotel.model.Room;
import com.hotel.storage.FileStorageManager;
import java.util.ArrayList;
import java.util.List;

public class RoomService {

    private List<Room> rooms;

    public RoomService() {
        this.rooms = FileStorageManager.loadRooms();
    }

    // Add a new room
    public boolean addRoom(int roomNumber, String roomType, double pricePerNight) {
        // Check if room number already exists
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return false; // duplicate
            }
        }
        rooms.add(new Room(roomNumber, roomType, pricePerNight));
        FileStorageManager.saveRooms(rooms);
        return true;
    }

    // Get all rooms
    public List<Room> getAllRooms() {
        return rooms;
    }

    // Get only available rooms
    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms) {
            if (!r.isBooked()) {
                available.add(r);
            }
        }
        return available;
    }

    // Find a room by room number
    public Room getRoomByNumber(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }

    // Mark a room as booked
    public boolean bookRoom(int roomNumber) {
        Room r = getRoomByNumber(roomNumber);
        if (r == null || r.isBooked()) return false;
        r.setBooked(true);
        FileStorageManager.saveRooms(rooms);
        return true;
    }

    // Mark a room as available again (checkout)
    public boolean releaseRoom(int roomNumber) {
        Room r = getRoomByNumber(roomNumber);
        if (r == null || !r.isBooked()) return false;
        r.setBooked(false);
        FileStorageManager.saveRooms(rooms);
        return true;
    }

    public void deleteRoom(int roomNumber) {
    rooms.removeIf(r -> r.getRoomNumber() == roomNumber);
    FileStorageManager.saveRooms(rooms);
}
}