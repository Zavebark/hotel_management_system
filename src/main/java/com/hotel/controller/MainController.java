package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class MainController {

    // ── Room controls ──
    @FXML private TextField txtRoomNumber;
    @FXML private ComboBox<String> cmbRoomType;
    @FXML private TextField txtRoomPrice;
    @FXML private Label lblRoomMessage;
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, Integer> colRoomNumber;
    @FXML private TableColumn<Room, String>  colRoomType;
    @FXML private TableColumn<Room, Double>  colRoomPrice;
    @FXML private TableColumn<Room, String>  colRoomStatus;

    // ── Customer controls ──
    @FXML private TextField txtCustomerName;
    @FXML private TextField txtCustomerContact;
    @FXML private Label lblCustomerMessage;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colCustomerId;
    @FXML private TableColumn<Customer, String>  colCustomerName;
    @FXML private TableColumn<Customer, String>  colCustomerContact;

    // ── Booking controls ──
    @FXML private TextField txtBookingCustomerId;
    @FXML private ComboBox<Integer> cmbBookingRoom;
    @FXML private TextField txtCheckIn;
    @FXML private TextField txtCheckOut;
    @FXML private TextField txtNights;
    @FXML private TextField txtCheckoutId;
    @FXML private Label lblBookingMessage;
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> colBookingId;
    @FXML private TableColumn<Booking, Integer> colBookingRoom;
    @FXML private TableColumn<Booking, String>  colBookingCustomer;
    @FXML private TableColumn<Booking, String>  colBookingCheckIn;
    @FXML private TableColumn<Booking, String>  colBookingCheckOut;
    @FXML private TableColumn<Booking, Double>  colBookingTotal;

    // ── Services ──
    private RoomService roomService;
    private CustomerService customerService;
    private BookingService bookingService;

    @FXML
    public void initialize() {
        roomService     = new RoomService();
        customerService = new CustomerService();
        bookingService  = new BookingService(roomService);

        // Room type options
        cmbRoomType.setItems(FXCollections.observableArrayList(
            "Single", "Double", "Deluxe"
        ));

        // Room table columns
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colRoomPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        colRoomStatus.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().isBooked() ? "Booked" : "Available"
            )
        );

        // Customer table columns
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustomerContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        // Booking table columns
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colBookingRoom.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(
                cellData.getValue().getRoom().getRoomNumber()).asObject()
        );
        colBookingCustomer.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getCustomer().getName()
            )
        );
        colBookingCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colBookingCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colBookingTotal.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(
                cellData.getValue().getTotalCost()).asObject()
        );

        // Load existing data
        refreshRoomTable(roomService.getAllRooms());
        refreshCustomerTable();
        refreshBookingTable();
        refreshAvailableRoomsCombo();
    }

    // ─────────────── ROOM HANDLERS ───────────────

    @FXML
    private void handleAddRoom() {
        try {
            int roomNumber = Integer.parseInt(txtRoomNumber.getText().trim());
            String roomType = cmbRoomType.getValue();
            double price = Double.parseDouble(txtRoomPrice.getText().trim());

            if (roomType == null) {
                showMessage(lblRoomMessage, "Please select a room type.", true);
                return;
            }

            boolean success = roomService.addRoom(roomNumber, roomType, price);
            if (success) {
                showMessage(lblRoomMessage, "Room " + roomNumber + " added successfully!", false);
                txtRoomNumber.clear();
                txtRoomPrice.clear();
                cmbRoomType.setValue(null);
                refreshRoomTable(roomService.getAllRooms());
                refreshAvailableRoomsCombo();
            } else {
                showMessage(lblRoomMessage, "Room number already exists.", true);
            }
        } catch (NumberFormatException e) {
            showMessage(lblRoomMessage, "Invalid input. Check room number and price.", true);
        }
    }

    @FXML
    private void handleShowAllRooms() {
        refreshRoomTable(roomService.getAllRooms());
    }

    @FXML
    private void handleShowAvailableRooms() {
        refreshRoomTable(roomService.getAvailableRooms());
    }

    // ─────────────── CUSTOMER HANDLERS ───────────────

    @FXML
    private void handleAddCustomer() {
        String name = txtCustomerName.getText().trim();
        String contact = txtCustomerContact.getText().trim();

        if (name.isEmpty() || contact.isEmpty()) {
            showMessage(lblCustomerMessage, "Please fill in all fields.", true);
            return;
        }

        int newId = customerService.generateCustomerId();
        boolean success = customerService.addCustomer(newId, name, contact);
        if (success) {
            showMessage(lblCustomerMessage,
                "Customer added! Assigned ID: " + newId, false);
            txtCustomerName.clear();
            txtCustomerContact.clear();
            refreshCustomerTable();
        } else {
            showMessage(lblCustomerMessage, "Failed to add customer.", true);
        }
    }

    // ─────────────── BOOKING HANDLERS ───────────────

    @FXML
    private void handleBookRoom() {
        try {
            int customerId = Integer.parseInt(txtBookingCustomerId.getText().trim());
            Integer roomNumber = cmbBookingRoom.getValue();
            String checkIn = txtCheckIn.getText().trim();
            String checkOut = txtCheckOut.getText().trim();
            int nights = Integer.parseInt(txtNights.getText().trim());

            if (roomNumber == null) {
                showMessage(lblBookingMessage, "Please select a room.", true);
                return;
            }

            Customer customer = customerService.getCustomerById(customerId);
            if (customer == null) {
                showMessage(lblBookingMessage, "Customer ID not found.", true);
                return;
            }

            Room room = roomService.getRoomByNumber(roomNumber);
            int bookingId = bookingService.generateBookingId();

            boolean success = bookingService.createBooking(
                bookingId, room, customer, checkIn, checkOut, nights
            );

            if (success) {
                showMessage(lblBookingMessage,
                    "Booking #" + bookingId + " created! Total: Rs." +
                    room.getPricePerNight() * nights, false);
                txtBookingCustomerId.clear();
                txtCheckIn.clear();
                txtCheckOut.clear();
                txtNights.clear();
                cmbBookingRoom.setValue(null);
                refreshBookingTable();
                refreshRoomTable(roomService.getAllRooms());
                refreshAvailableRoomsCombo();
            } else {
                showMessage(lblBookingMessage, "Room is already booked.", true);
            }
        } catch (NumberFormatException e) {
            showMessage(lblBookingMessage, "Invalid input. Check all fields.", true);
        }
    }

    @FXML
    private void handleCheckout() {
        try {
            int bookingId = Integer.parseInt(txtCheckoutId.getText().trim());
            boolean success = bookingService.checkout(bookingId);
            if (success) {
                showMessage(lblBookingMessage,
                    "Checkout successful for Booking #" + bookingId, false);
                txtCheckoutId.clear();
                refreshBookingTable();
                refreshRoomTable(roomService.getAllRooms());
                refreshAvailableRoomsCombo();
            } else {
                showMessage(lblBookingMessage, "Booking ID not found.", true);
            }
        } catch (NumberFormatException e) {
            showMessage(lblBookingMessage, "Invalid Booking ID.", true);
        }
    }

    // ─────────────── HELPERS ───────────────

    private void refreshRoomTable(List<Room> rooms) {
        roomTable.setItems(FXCollections.observableArrayList(rooms));
    }

    private void refreshCustomerTable() {
        customerTable.setItems(FXCollections.observableArrayList(
            customerService.getAllCustomers()
        ));
    }

    private void refreshBookingTable() {
        bookingTable.setItems(FXCollections.observableArrayList(
            bookingService.getAllBookings()
        ));
    }

    private void refreshAvailableRoomsCombo() {
        List<Integer> roomNumbers = new ArrayList<>();
        for (Room r : roomService.getAvailableRooms()) {
            roomNumbers.add(r.getRoomNumber());
        }
        cmbBookingRoom.setItems(FXCollections.observableArrayList(roomNumbers));
    }

    private void showMessage(Label label, String message, boolean isError) {
        label.setText(message);
        label.setStyle(isError ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
    }
}