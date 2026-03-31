package com.hotel.service;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.storage.FileStorageManager;

import java.util.List;

public class BookingService {

    private List<Booking> bookings;
    private RoomService roomService;

    public BookingService(RoomService roomService) {
        this.bookings = FileStorageManager.loadBookings();
        this.roomService = roomService;
    }

    public boolean createBooking(int bookingId, Room room, Customer customer, String checkInDate, String checkOutDate, int nights) {
        if (room.isBooked()) return false;

        Booking booking = new Booking(bookingId, room, customer, checkInDate, checkOutDate, nights);
        bookings.add(booking);
        roomService.bookRoom(room.getRoomNumber());
        FileStorageManager.saveBookings(bookings);
        return true;
    }

    public boolean checkout(int bookingId) {
        Booking toRemove = null;
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                toRemove = b;
                break;
            }
        }
        if (toRemove == null) return false;

        roomService.releaseRoom(toRemove.getRoom().getRoomNumber());
        bookings.remove(toRemove);
        FileStorageManager.saveBookings(bookings);
        return true;
    }

    public List<Booking> getAllBookings() {
        return bookings;
    }

    public Booking getBookingById(int bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                return b;
            }
        }
        return null;
    }

    public int generateBookingId() {
        if (bookings.isEmpty()) return 1;
        int max = 0;
        for (Booking b : bookings) {
            if (b.getBookingId() > max) max = b.getBookingId();
        }
        return max + 1;
    }
}