package com.hotel.controller;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import com.hotel.model.ServiceRequest;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.service.ServiceRequestService;
import com.hotel.storage.FileStorageManager;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ScrollPane;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    // ── Sidebar nav buttons ──
    @FXML private Button btnNavDashboard;
    @FXML private Button btnNavRooms;
    @FXML private Button btnNavCustomers;
    @FXML private Button btnNavBookings;
    @FXML private Button btnNavServices;
    @FXML private Label lblPageTitle;

    // ── Pages ──
    @FXML private ScrollPane pageDashboard;
    @FXML private ScrollPane pageRooms;
    @FXML private ScrollPane pageCustomers;
    @FXML private ScrollPane pageBookings;
    @FXML private ScrollPane pageServices;

    // ── Dashboard ──
    @FXML private Label lblTotalRooms;
    @FXML private Label lblOccupied;
    @FXML private Label lblAvailable;
    @FXML private Label lblTotalGuests;
    @FXML private Label lblRevenue;
    @FXML private TableView<Booking> dashboardBookingTable;
    @FXML private TableColumn<Booking, Integer> dashColBookingId;
    @FXML private TableColumn<Booking, Integer> dashColRoom;
    @FXML private TableColumn<Booking, String>  dashColGuest;
    @FXML private TableColumn<Booking, String>  dashColCheckIn;
    @FXML private TableColumn<Booking, String>  dashColCheckOut;
    @FXML private TableColumn<Booking, Double>  dashColTotal;

    // ── Rooms ──
    @FXML private TextField txtRoomNumber;
    @FXML private ComboBox<String> cmbRoomType;
    @FXML private TextField txtRoomPrice;
    @FXML private Label lblRoomMessage;
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, Integer> colRoomNumber;
    @FXML private TableColumn<Room, String>  colRoomType;
    @FXML private TableColumn<Room, Double>  colRoomPrice;
    @FXML private TableColumn<Room, String>  colRoomStatus;
    @FXML private TableColumn<Room, Void>    colRoomDelete;
    @FXML private ComboBox<String> cmbFilterType;

    // ── Customers ──
    @FXML private TextField txtCustomerName;
    @FXML private TextField txtCustomerContact;
    @FXML private Label lblCustomerMessage;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> colCustomerId;
    @FXML private TableColumn<Customer, String>  colCustomerName;
    @FXML private TableColumn<Customer, String>  colCustomerContact;
    @FXML private TableColumn<Customer, Void>    colCustomerDelete;

    // ── Bookings ──
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
    @FXML private TextField txtServiceRoom;
    @FXML private ComboBox<String> cmbServiceType;
    @FXML private Label lblServiceMessage;
    @FXML private TableView<ServiceRequest> serviceTable;
    @FXML private TableColumn<ServiceRequest, Integer> colServiceId;
    @FXML private TableColumn<ServiceRequest, Integer> colServiceRoom;
    @FXML private TableColumn<ServiceRequest, String>  colServiceType;
    @FXML private TableColumn<ServiceRequest, String>  colServiceStatus;
    @FXML private TableColumn<ServiceRequest, Void>    colServiceAction;

    // ── Services ──
    private RoomService roomService;
    private CustomerService customerService;
    private BookingService bookingService;
    private ServiceRequestService serviceRequestService;

    @FXML
    public void initialize() {
        roomService           = new RoomService();
        customerService       = new CustomerService();
        bookingService        = new BookingService(roomService);
        serviceRequestService = new ServiceRequestService();

        setupComboBoxes();
        setupRoomTable();
        setupCustomerTable();
        setupBookingTable();
        setupDashboardTable();
        setupServiceTable();

        refreshAll();
        showDashboard();
    }

    // ─────────────── SETUP ───────────────

    private void setupComboBoxes() {
        cmbRoomType.setItems(FXCollections.observableArrayList(
            "Single", "Double", "Deluxe", "Suite"
        ));
        cmbFilterType.setItems(FXCollections.observableArrayList(
            "All", "Single", "Double", "Deluxe", "Suite"
        ));
        cmbServiceType.setItems(FXCollections.observableArrayList(
            "Room Cleaning", "Laundry", "Room Service",
            "Maintenance", "Extra Towels", "Wake-up Call"
        ));
    }

    private void setupRoomTable() {
        colRoomNumber.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colRoomPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        colRoomStatus.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().isBooked() ? "Occupied" : "Available"
            )
        );
        colRoomStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle(item.equals("Occupied")
                        ? "-fx-text-fill: #8B1A1A; -fx-font-weight: bold;"
                        : "-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                }
            }
        });
        // Delete button column
        colRoomDelete.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("Delete");
            {
                btn.getStyleClass().add("btn-danger");
                btn.setOnAction(e -> {
                    Room room = getTableView().getItems().get(getIndex());
                    if (room.isBooked()) {
                        showMessage(lblRoomMessage,
                            "Cannot delete an occupied room.", true);
                        return;
                    }
                    roomService.deleteRoom(room.getRoomNumber());
                    refreshRoomTable(roomService.getAllRooms());
                    refreshAvailableRoomsCombo();
                    refreshDashboard();
                    showMessage(lblRoomMessage,
                        "Room " + room.getRoomNumber() + " deleted.", false);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void setupCustomerTable() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustomerContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        // Delete button column
        colCustomerDelete.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("Delete");
            {
                btn.getStyleClass().add("btn-danger");
                btn.setOnAction(e -> {
                    Customer c = getTableView().getItems().get(getIndex());
                    customerService.deleteCustomer(c.getCustomerId());
                    refreshCustomerTable();
                    refreshDashboard();
                    showMessage(lblCustomerMessage,
                        "Guest " + c.getName() + " removed.", false);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void setupBookingTable() {
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
    }

    private void setupDashboardTable() {
        dashColBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        dashColRoom.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(
                cellData.getValue().getRoom().getRoomNumber()).asObject()
        );
        dashColGuest.setCellValueFactory(cellData ->
            new SimpleStringProperty(
                cellData.getValue().getCustomer().getName()
            )
        );
        dashColCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        dashColCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        dashColTotal.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(
                cellData.getValue().getTotalCost()).asObject()
        );
    }

    private void setupServiceTable() {
        colServiceId.setCellValueFactory(new PropertyValueFactory<>("serviceId"));
        colServiceRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colServiceType.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        colServiceStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colServiceStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else {
                    setText(item);
                    setStyle(item.equals("Pending")
                        ? "-fx-text-fill: #C9A84C; -fx-font-weight: bold;"
                        : "-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
                }
            }
        });
        // Mark Done button
        colServiceAction.setCellFactory(col -> new TableCell<>() {
            final Button btn = new Button("Mark Done");
            {
                btn.getStyleClass().add("btn-gold");
                btn.setOnAction(e -> {
                    ServiceRequest sr =
                        getTableView().getItems().get(getIndex());
                    serviceRequestService.markDone(sr.getServiceId());
                    refreshServiceTable();
                    showMessage(lblServiceMessage,
                        "Service #" + sr.getServiceId() + " marked as done.", false);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    // ─────────────── NAVIGATION ───────────────

    private void hideAllPages() {
        pageDashboard.setVisible(false);
        pageRooms.setVisible(false);
        pageCustomers.setVisible(false);
        pageBookings.setVisible(false);
        pageServices.setVisible(false);

        btnNavDashboard.getStyleClass().remove("sidebar-btn-active");
        btnNavRooms.getStyleClass().remove("sidebar-btn-active");
        btnNavCustomers.getStyleClass().remove("sidebar-btn-active");
        btnNavBookings.getStyleClass().remove("sidebar-btn-active");
        btnNavServices.getStyleClass().remove("sidebar-btn-active");
    }

    @FXML public void showDashboard() {
        hideAllPages();
        pageDashboard.setVisible(true);
        btnNavDashboard.getStyleClass().add("sidebar-btn-active");
        lblPageTitle.setText("Dashboard");
        refreshDashboard();
    }

    @FXML public void showRooms() {
        hideAllPages();
        pageRooms.setVisible(true);
        btnNavRooms.getStyleClass().add("sidebar-btn-active");
        lblPageTitle.setText("Room Management");
        refreshRoomTable(roomService.getAllRooms());
    }

    @FXML public void showCustomers() {
        hideAllPages();
        pageCustomers.setVisible(true);
        btnNavCustomers.getStyleClass().add("sidebar-btn-active");
        lblPageTitle.setText("Guest Registry");
        refreshCustomerTable();
    }

    @FXML public void showBookings() {
        hideAllPages();
        pageBookings.setVisible(true);
        btnNavBookings.getStyleClass().add("sidebar-btn-active");
        lblPageTitle.setText("Reservations");
        refreshBookingTable();
        refreshAvailableRoomsCombo();
    }

    @FXML public void showServices() {
        hideAllPages();
        pageServices.setVisible(true);
        btnNavServices.getStyleClass().add("sidebar-btn-active");
        lblPageTitle.setText("Housekeeping & Services");
        refreshServiceTable();
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
                showMessage(lblRoomMessage,
                    "Room " + roomNumber + " added successfully.", false);
                txtRoomNumber.clear();
                txtRoomPrice.clear();
                cmbRoomType.setValue(null);
                refreshRoomTable(roomService.getAllRooms());
                refreshAvailableRoomsCombo();
                refreshDashboard();
            } else {
                showMessage(lblRoomMessage, "Room number already exists.", true);
            }
        } catch (NumberFormatException e) {
            showMessage(lblRoomMessage,
                "Invalid input. Check room number and price.", true);
        }
    }

    @FXML private void handleShowAllRooms() {
        refreshRoomTable(roomService.getAllRooms());
    }

    @FXML private void handleShowAvailableRooms() {
        refreshRoomTable(roomService.getAvailableRooms());
    }

    @FXML private void handleFilterByType() {
        String selected = cmbFilterType.getValue();
        if (selected == null || selected.equals("All")) {
            refreshRoomTable(roomService.getAllRooms());
        } else {
            List<Room> filtered = new ArrayList<>();
            for (Room r : roomService.getAllRooms()) {
                if (r.getRoomType().equals(selected)) filtered.add(r);
            }
            refreshRoomTable(filtered);
        }
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
        customerService.addCustomer(newId, name, contact);
        showMessage(lblCustomerMessage,
            "Guest registered. ID: " + newId, false);
        txtCustomerName.clear();
        txtCustomerContact.clear();
        refreshCustomerTable();
        refreshDashboard();
    }

    // ─────────────── BOOKING HANDLERS ───────────────

    @FXML
    private void handleBookRoom() {
        try {
            int customerId =
                Integer.parseInt(txtBookingCustomerId.getText().trim());
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
                showMessage(lblBookingMessage, "Guest ID not found.", true);
                return;
            }
            Room room = roomService.getRoomByNumber(roomNumber);
            int bookingId = bookingService.generateBookingId();
            boolean success = bookingService.createBooking(
                bookingId, room, customer, checkIn, checkOut, nights
            );
            if (success) {
                showMessage(lblBookingMessage,
                    "Reservation #" + bookingId + " confirmed. Total: Rs." +
                    (room.getPricePerNight() * nights), false);
                txtBookingCustomerId.clear();
                txtCheckIn.clear();
                txtCheckOut.clear();
                txtNights.clear();
                cmbBookingRoom.setValue(null);
                refreshBookingTable();
                refreshRoomTable(roomService.getAllRooms());
                refreshAvailableRoomsCombo();
                refreshDashboard();
            } else {
                showMessage(lblBookingMessage, "Room is already occupied.", true);
            }
        } catch (NumberFormatException e) {
            showMessage(lblBookingMessage,
                "Invalid input. Check all fields.", true);
        }
    }

    @FXML
    private void handleCheckout() {
        try {
            int bookingId =
                Integer.parseInt(txtCheckoutId.getText().trim());
            boolean success = bookingService.checkout(bookingId);
            if (success) {
                showMessage(lblBookingMessage,
                    "Checkout successful for Reservation #" + bookingId, false);
                txtCheckoutId.clear();
                refreshBookingTable();
                refreshRoomTable(roomService.getAllRooms());
                refreshAvailableRoomsCombo();
                refreshDashboard();
            } else {
                showMessage(lblBookingMessage, "Reservation ID not found.", true);
            }
        } catch (NumberFormatException e) {
            showMessage(lblBookingMessage, "Invalid Reservation ID.", true);
        }
    }

    // ─────────────── SERVICE HANDLERS ───────────────

    @FXML
    private void handleAddService() {
        String roomStr = txtServiceRoom.getText().trim();
        String serviceType = cmbServiceType.getValue();
        if (roomStr.isEmpty() || serviceType == null) {
            showMessage(lblServiceMessage,
                "Please fill in room number and service type.", true);
            return;
        }
        try {
            int roomNumber = Integer.parseInt(roomStr);
            int serviceId = serviceRequestService.generateServiceId();
            serviceRequestService.addService(serviceId, roomNumber, serviceType);
            showMessage(lblServiceMessage,
                "Service request added for Room " + roomNumber, false);
            txtServiceRoom.clear();
            cmbServiceType.setValue(null);
            refreshServiceTable();
        } catch (NumberFormatException e) {
            showMessage(lblServiceMessage, "Invalid room number.", true);
        }
    }

    // ─────────────── DASHBOARD ───────────────

    private void refreshDashboard() {
        int total    = roomService.getAllRooms().size();
        int occupied = 0;
        for (Room r : roomService.getAllRooms()) {
            if (r.isBooked()) occupied++;
        }
        double revenue = 0;
        for (Booking b : bookingService.getAllBookings()) {
            revenue += b.getTotalCost();
        }
        lblTotalRooms.setText(String.valueOf(total));
        lblOccupied.setText(String.valueOf(occupied));
        lblAvailable.setText(String.valueOf(total - occupied));
        lblTotalGuests.setText(
            String.valueOf(customerService.getAllCustomers().size())
        );
        lblRevenue.setText("Rs." + String.format("%.0f", revenue));

        dashboardBookingTable.setItems(FXCollections.observableArrayList(
            bookingService.getAllBookings()
        ));
    }

    // ─────────────── REFRESH HELPERS ───────────────

    private void refreshAll() {
        refreshRoomTable(roomService.getAllRooms());
        refreshCustomerTable();
        refreshBookingTable();
        refreshServiceTable();
        refreshAvailableRoomsCombo();
        refreshDashboard();
    }

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

    private void refreshServiceTable() {
        serviceTable.setItems(FXCollections.observableArrayList(
            serviceRequestService.getAllServices()
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
        label.setStyle(isError
            ? "-fx-text-fill: #8B1A1A; -fx-font-weight: bold;"
            : "-fx-text-fill: #2E7D32; -fx-font-weight: bold;");
    }
}