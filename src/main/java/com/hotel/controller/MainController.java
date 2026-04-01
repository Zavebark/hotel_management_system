package com.hotel.controller;

import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

import com.hotel.model.Booking;
import com.hotel.model.BillingRecord;
import com.hotel.model.Customer;
import com.hotel.model.PaidService;
import com.hotel.model.Room;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.storage.FileStorageManager;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainController {

    // ── Room type constants ──
    private static final String SS = "Standard Single";
    private static final String SD = "Standard Double";
    private static final String DS = "Deluxe Suite";
    private static final String GS = "Grand Suite";
    private static final String PS = "Presidential Suite";

    private static final int CNT_SS = 300, CNT_SD = 300,
                              CNT_DS = 200, CNT_GS = 150, CNT_PS = 50;

    private static final double P_SS = 1500, P_SD = 2500,
                                 P_DS = 4500, P_GS = 7500, P_PS = 15000;

    private static final DateTimeFormatter DT =
        DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    // Service prices
    private static final String SVC_CLEANING   = "Room Cleaning";
    private static final String SVC_LAUNDRY    = "Laundry";
    private static final String SVC_ROOMSVC    = "Room Service";
    private static final String SVC_SPA        = "Spa Access";
    private static final String SVC_TRANSFER   = "Airport Transfer";
    private static final String SVC_EXTRABED   = "Extra Bed";

    private static final double PR_CLEANING    = 200;
    private static final double PR_LAUNDRY     = 350;
    private static final double PR_ROOMSVC     = 500;
    private static final double PR_SPA         = 1200;
    private static final double PR_TRANSFER    = 800;
    private static final double PR_EXTRABED    = 600;

    // ── Dashboard ──
    @FXML private Label lblTotalGuests, lblOccupied, lblAvailable, lblRevenue;
    @FXML private ProgressBar pbSS, pbSD, pbDS, pbGS, pbPS;
    @FXML private Label lblSSStat, lblSDStat, lblDSStat, lblGSStat, lblPSStat;
    @FXML private TableView<Booking> guestTable;
    @FXML private TableColumn<Booking, Integer> colGuestRoom;
    @FXML private TableColumn<Booking, String>  colGuestName;
    @FXML private TableColumn<Booking, String>  colGuestPhone;
    @FXML private TableColumn<Booking, String>  colGuestType;
    @FXML private TableColumn<Booking, Integer> colGuestNights;
    @FXML private TableColumn<Booking, String>  colGuestCheckIn;
    @FXML private TableColumn<Booking, String>  colGuestServices;
    @FXML private TextField searchField;

    // ── Booking ──
    @FXML private VBox chipSS, chipSD, chipDS, chipGS, chipPS;
    @FXML private Label availSS, availSD, availDS, availGS, availPS;
    @FXML private Label bookRoomInfoLabel;
    @FXML private TextField bookNameField, bookPhoneField, bookDaysField;
    @FXML private Label bookStatusLabel;

    // ── Services ──
    @FXML private TextField serviceRoomField;
    @FXML private Label serviceGuestLabel, serviceStatusLabel;
    @FXML private VBox svcCleaning, svcLaundry, svcRoomService,
                       svcSpa, svcTransfer, svcExtraBed;
    @FXML private TableView<PaidService> serviceTable;
    @FXML private TableColumn<PaidService, Integer> colSvcRoom;
    @FXML private TableColumn<PaidService, String>  colSvcName;
    @FXML private TableColumn<PaidService, Double>  colSvcPrice;
    @FXML private TableColumn<PaidService, String>  colSvcStatus;

    // ── Checkout ──
    @FXML private TextField checkoutRoomField, checkoutDaysField;
    @FXML private Label checkoutGuestLabel, checkoutStatusLabel;
    @FXML private VBox billItemsBox;
    @FXML private Label lblBillTotal;

    // ── Billing ──
    @FXML private TableView<BillingRecord> billingTable;
    @FXML private TableColumn<BillingRecord, String>  colBillDate;
    @FXML private TableColumn<BillingRecord, Integer> colBillRoom;
    @FXML private TableColumn<BillingRecord, String>  colBillGuest;
    @FXML private TableColumn<BillingRecord, String>  colBillPhone;
    @FXML private TableColumn<BillingRecord, String>  colBillType;
    @FXML private TableColumn<BillingRecord, Integer> colBillNights;
    @FXML private TableColumn<BillingRecord, Double>  colBillRoomAmt;
    @FXML private TableColumn<BillingRecord, Double>  colBillSvcAmt;
    @FXML private TableColumn<BillingRecord, Double>  colBillTotal;
    @FXML private Label lblTotalRevenue;

    // ── Services ──
    private RoomService roomService;
    private CustomerService customerService;
    private BookingService bookingService;
    private List<PaidService> paidServices;
    private List<BillingRecord> billingRecords;

    // ── State ──
    private String selectedRoomType = null;
    private int serviceTargetRoom   = -1;

    // ─────────────── INIT ───────────────

    @FXML
    public void initialize() {
        roomService     = new RoomService();
        customerService = new CustomerService();
        bookingService  = new BookingService(roomService);
        paidServices    = FileStorageManager.loadPaidServices();
        billingRecords  = FileStorageManager.loadBillingRecords();

        ensureRoomsExist();
        setupGuestTable();
        setupServiceTable();
        setupBillingTable();
        refreshAll();
    }

    private void ensureRoomsExist() {
        if (!roomService.getAllRooms().isEmpty()) return;
        int n = 100;
        String[] types  = {SS, SD, DS, GS, PS};
        double[] prices = {P_SS, P_SD, P_DS, P_GS, P_PS};
        int[] counts    = {CNT_SS, CNT_SD, CNT_DS, CNT_GS, CNT_PS};
        for (int t = 0; t < 5; t++)
            for (int i = 0; i < counts[t]; i++)
                roomService.addRoom(++n, types[t], prices[t]);
    }

    // ─────────────── TABLE SETUP ───────────────

    private void setupGuestTable() {
        colGuestRoom.setCellValueFactory(c ->
            new SimpleIntegerProperty(
                c.getValue().getRoom().getRoomNumber()).asObject());
        colGuestName.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getCustomer().getName()));
        colGuestPhone.setCellValueFactory(c ->
            new SimpleStringProperty(
                c.getValue().getCustomer().getContactNumber()));
        colGuestType.setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().getRoom().getRoomType()));
        colGuestNights.setCellValueFactory(new PropertyValueFactory<>("nights"));
        colGuestCheckIn.setCellValueFactory(
            new PropertyValueFactory<>("checkInDate"));
        colGuestServices.setCellValueFactory(c -> {
            int room = c.getValue().getRoom().getRoomNumber();
            long count = paidServices.stream()
                .filter(s -> s.getRoomNumber() == room).count();
            double total = paidServices.stream()
                .filter(s -> s.getRoomNumber() == room)
                .mapToDouble(PaidService::getPrice).sum();
            return new SimpleStringProperty(
                count > 0 ? count + " (₹" + (int)total + ")" : "—");
        });
    }

    private void setupServiceTable() {
        colSvcRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colSvcName.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        colSvcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colSvcStatus.setCellValueFactory(c ->
            new SimpleStringProperty("Added"));
    }

    private void setupBillingTable() {
        colBillDate.setCellValueFactory(
            new PropertyValueFactory<>("checkoutDate"));
        colBillRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        colBillGuest.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        colBillPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colBillType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colBillNights.setCellValueFactory(new PropertyValueFactory<>("nights"));
        colBillRoomAmt.setCellValueFactory(
            new PropertyValueFactory<>("roomCharges"));
        colBillSvcAmt.setCellValueFactory(
            new PropertyValueFactory<>("serviceCharges"));
        colBillTotal.setCellValueFactory(
            new PropertyValueFactory<>("totalBill"));
    }

    // ─────────────── BOOKING ───────────────

    @FXML
    private void onSelectRoomType(MouseEvent e) {
        VBox clicked = (VBox) e.getSource();
        resetChips();
        if      (clicked == chipSS) { selectedRoomType = SS; chipSS.getStyleClass().setAll("room-chip-active"); }
        else if (clicked == chipSD) { selectedRoomType = SD; chipSD.getStyleClass().setAll("room-chip-active"); }
        else if (clicked == chipDS) { selectedRoomType = DS; chipDS.getStyleClass().setAll("room-chip-active"); }
        else if (clicked == chipGS) { selectedRoomType = GS; chipGS.getStyleClass().setAll("room-chip-active"); }
        else                        { selectedRoomType = PS; chipPS.getStyleClass().setAll("room-chip-active"); }

        long avail = roomService.getAvailableRooms().stream()
            .filter(r -> r.getRoomType().equals(selectedRoomType)).count();
        bookRoomInfoLabel.setText(selectedRoomType + " selected — "
            + avail + " rooms available");
    }

    private void resetChips() {
        chipSS.getStyleClass().setAll("room-chip");
        chipSD.getStyleClass().setAll("room-chip");
        chipDS.getStyleClass().setAll("room-chip");
        chipGS.getStyleClass().setAll("room-chip");
        chipPS.getStyleClass().setAll("room-chip");
    }

    @FXML
    private void onBookRoom() {
        if (selectedRoomType == null) {
            setStatus(bookStatusLabel, "Please select a room category.", true);
            return;
        }
        String name    = bookNameField.getText().trim();
        String phone   = bookPhoneField.getText().trim();
        String daysStr = bookDaysField.getText().trim();

        if (name.isEmpty() || daysStr.isEmpty()) {
            setStatus(bookStatusLabel, "Guest name and nights are required.", true);
            return;
        }
        int nights;
        try {
            nights = Integer.parseInt(daysStr);
            if (nights <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            setStatus(bookStatusLabel, "Enter a valid number of nights.", true);
            return;
        }

        Room room = roomService.getAvailableRooms().stream()
            .filter(r -> r.getRoomType().equals(selectedRoomType))
            .findFirst().orElse(null);

        if (room == null) {
            setStatus(bookStatusLabel, "No rooms available for this category.", true);
            return;
        }

        int cid = customerService.generateCustomerId();
        customerService.addCustomer(cid, name,
            phone.isEmpty() ? "N/A" : phone);
        Customer customer = customerService.getCustomerById(cid);

        String checkIn = LocalDateTime.now().format(DT);
        int bid = bookingService.generateBookingId();
        bookingService.createBooking(bid, room, customer, checkIn, "—", nights);

        setStatus(bookStatusLabel,
            "✓ Room " + room.getRoomNumber() + " reserved for " + name
            + " — Room charges: ₹" + (int)(room.getPricePerNight() * nights), false);

        bookNameField.clear();
        bookPhoneField.clear();
        bookDaysField.clear();
        selectedRoomType = null;
        resetChips();
        bookRoomInfoLabel.setText("← Select a room category to continue");
        refreshAll();
    }

    // ─────────────── SERVICES ───────────────

    @FXML
    private void onLookupServiceRoom() {
        String roomStr = serviceRoomField.getText().trim();
        try {
            int roomNum = Integer.parseInt(roomStr);
            Booking booking = bookingService.getAllBookings().stream()
                .filter(b -> b.getRoom().getRoomNumber() == roomNum)
                .findFirst().orElse(null);
            if (booking == null) {
                serviceGuestLabel.setText("No active booking for room " + roomNum);
                serviceGuestLabel.setStyle("-fx-text-fill: #f87171;");
                serviceTargetRoom = -1;
            } else {
                serviceTargetRoom = roomNum;
                serviceGuestLabel.setText(
                    "Guest: " + booking.getCustomer().getName()
                    + "  |  " + booking.getRoom().getRoomType()
                    + "  |  " + booking.getNights() + " nights");
                serviceGuestLabel.setStyle("-fx-text-fill: #c9a84c;");
                refreshServiceTable();
            }
        } catch (NumberFormatException e) {
            serviceGuestLabel.setText("Enter a valid room number.");
            serviceGuestLabel.setStyle("-fx-text-fill: #f87171;");
        }
    }

    @FXML
    private void onAddService(MouseEvent e) {
        if (serviceTargetRoom == -1) {
            setStatus(serviceStatusLabel, "Look up a room first.", true);
            return;
        }
        VBox clicked = (VBox) e.getSource();
        String name;
        double price;

        if      (clicked == svcCleaning)   { name = SVC_CLEANING;  price = PR_CLEANING;  }
        else if (clicked == svcLaundry)    { name = SVC_LAUNDRY;   price = PR_LAUNDRY;   }
        else if (clicked == svcRoomService){ name = SVC_ROOMSVC;   price = PR_ROOMSVC;   }
        else if (clicked == svcSpa)        { name = SVC_SPA;       price = PR_SPA;       }
        else if (clicked == svcTransfer)   { name = SVC_TRANSFER;  price = PR_TRANSFER;  }
        else                               { name = SVC_EXTRABED;  price = PR_EXTRABED;  }

        paidServices.add(new PaidService(serviceTargetRoom, name, price));
        FileStorageManager.savePaidServices(paidServices);

        setStatus(serviceStatusLabel,
            "✓ " + name + " added to Room " + serviceTargetRoom, false);
        refreshServiceTable();
        refreshDashboard();
    }

    private void refreshServiceTable() {
        if (serviceTargetRoom == -1) return;
        int room = serviceTargetRoom;
        List<PaidService> roomServices = paidServices.stream()
            .filter(s -> s.getRoomNumber() == room)
            .collect(Collectors.toList());
        serviceTable.setItems(FXCollections.observableArrayList(roomServices));
    }

    // ─────────────── CHECKOUT ───────────────

    @FXML
    private void onLookupCheckout() {
        String roomStr = checkoutRoomField.getText().trim();
        try {
            int roomNum = Integer.parseInt(roomStr);
            Booking booking = bookingService.getAllBookings().stream()
                .filter(b -> b.getRoom().getRoomNumber() == roomNum)
                .findFirst().orElse(null);

            if (booking == null) {
                checkoutGuestLabel.setText("No active booking for room " + roomNum);
                checkoutGuestLabel.setStyle("-fx-text-fill: #f87171;");
                billItemsBox.getChildren().clear();
                lblBillTotal.setText("");
                return;
            }

            checkoutGuestLabel.setText(
                "Guest: " + booking.getCustomer().getName()
                + "  |  " + booking.getRoom().getRoomType()
                + "  |  Booked: " + booking.getNights() + " nights"
                + "  |  Check-in: " + booking.getCheckInDate());
            checkoutGuestLabel.setStyle("-fx-text-fill: #c9a84c;");

            buildBillPreview(booking);
        } catch (NumberFormatException e) {
            checkoutGuestLabel.setText("Enter a valid room number.");
            checkoutGuestLabel.setStyle("-fx-text-fill: #f87171;");
        }
    }

    private void buildBillPreview(Booking booking) {
        billItemsBox.getChildren().clear();
        int nights = booking.getNights();
        double roomCharge = booking.getRoom().getPricePerNight() * nights;

        // Room charge row
        addBillRow("Room — " + booking.getRoom().getRoomType()
            + " × " + nights + " nights", roomCharge);

        // Service rows
        double svcTotal = 0;
        for (PaidService s : paidServices) {
            if (s.getRoomNumber() == booking.getRoom().getRoomNumber()) {
                addBillRow(s.getServiceName(), s.getPrice());
                svcTotal += s.getPrice();
            }
        }

        lblBillTotal.setText("TOTAL:  ₹" + (int)(roomCharge + svcTotal));
    }

    private void addBillRow(String name, double price) {
        HBox row = new HBox();
        row.getStyleClass().add("bill-item");
        Label lName  = new Label(name);
        Label lPrice = new Label("₹" + (int) price);
        lName.getStyleClass().add("bill-item-name");
        lPrice.getStyleClass().add("bill-item-price");
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        row.getChildren().addAll(lName, spacer, lPrice);
        billItemsBox.getChildren().add(row);
    }

    @FXML
    private void onCheckout() {
        String roomStr = checkoutRoomField.getText().trim();
        try {
            int roomNum = Integer.parseInt(roomStr);
            Booking booking = bookingService.getAllBookings().stream()
                .filter(b -> b.getRoom().getRoomNumber() == roomNum)
                .findFirst().orElse(null);

            if (booking == null) {
                setStatus(checkoutStatusLabel,
                    "No active booking for room " + roomNum, true);
                return;
            }

            int nights = booking.getNights();
            String daysStr = checkoutDaysField.getText().trim();
            if (!daysStr.isEmpty()) {
                try { nights = Integer.parseInt(daysStr); }
                catch (NumberFormatException ignored) {}
            }

            double roomCharge = booking.getRoom().getPricePerNight() * nights;
            double svcCharge  = paidServices.stream()
                .filter(s -> s.getRoomNumber() == roomNum)
                .mapToDouble(PaidService::getPrice).sum();

            BillingRecord record = new BillingRecord(
                LocalDateTime.now().format(DT),
                roomNum,
                booking.getCustomer().getName(),
                booking.getCustomer().getContactNumber(),
                booking.getRoom().getRoomType(),
                nights,
                roomCharge,
                svcCharge
            );
            billingRecords.add(record);
            FileStorageManager.saveBillingRecords(billingRecords);

            // Remove services for this room
            paidServices.removeIf(s -> s.getRoomNumber() == roomNum);
            FileStorageManager.savePaidServices(paidServices);

            bookingService.checkout(booking.getBookingId());

            setStatus(checkoutStatusLabel,
                "✓ Room " + roomNum + " checked out — Total bill: ₹"
                + (int)(roomCharge + svcCharge), false);

            checkoutRoomField.clear();
            checkoutDaysField.clear();
            checkoutGuestLabel.setText("");
            billItemsBox.getChildren().clear();
            lblBillTotal.setText("");
            refreshAll();

        } catch (NumberFormatException e) {
            setStatus(checkoutStatusLabel, "Enter a valid room number.", true);
        }
    }

    // ─────────────── SEARCH ───────────────

    @FXML
    private void onSearchGuest(KeyEvent e) {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            guestTable.setItems(FXCollections.observableArrayList(
                bookingService.getAllBookings()));
            return;
        }
        List<Booking> filtered = bookingService.getAllBookings().stream()
            .filter(b -> b.getCustomer().getName().toLowerCase().contains(query))
            .collect(Collectors.toList());
        guestTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // ─────────────── REFRESH ───────────────

    private void refreshAll() {
        refreshDashboard();
        refreshBillingTable();
        refreshAvailChips();
    }

    private void refreshDashboard() {
        List<Booking> bookings = bookingService.getAllBookings();
        int total    = roomService.getAllRooms().size();
        int occupied = bookings.size();

        double revenue = billingRecords.stream()
            .mapToDouble(BillingRecord::getTotalBill).sum();

        lblTotalGuests.setText(String.valueOf(occupied));
        lblOccupied.setText(String.valueOf(occupied));
        lblAvailable.setText(String.valueOf(total - occupied));
        lblRevenue.setText("₹" + String.format("%.0f", revenue));

        updateBar(SS, pbSS, lblSSStat, CNT_SS, bookings);
        updateBar(SD, pbSD, lblSDStat, CNT_SD, bookings);
        updateBar(DS, pbDS, lblDSStat, CNT_DS, bookings);
        updateBar(GS, pbGS, lblGSStat, CNT_GS, bookings);
        updateBar(PS, pbPS, lblPSStat, CNT_PS, bookings);

        guestTable.setItems(FXCollections.observableArrayList(bookings));
    }

    private void updateBar(String type, ProgressBar pb,
                            Label stat, int total, List<Booking> bookings) {
        long occ = bookings.stream()
            .filter(b -> b.getRoom().getRoomType().equals(type)).count();
        pb.setProgress((double) occ / total);
        stat.setText(occ + " / " + total
            + " (" + (int)(occ * 100.0 / total) + "%)");
    }

    private void refreshAvailChips() {
        List<Room> avail = roomService.getAvailableRooms();
        availSS.setText(avail.stream().filter(r -> r.getRoomType().equals(SS)).count() + " available");
        availSD.setText(avail.stream().filter(r -> r.getRoomType().equals(SD)).count() + " available");
        availDS.setText(avail.stream().filter(r -> r.getRoomType().equals(DS)).count() + " available");
        availGS.setText(avail.stream().filter(r -> r.getRoomType().equals(GS)).count() + " available");
        availPS.setText(avail.stream().filter(r -> r.getRoomType().equals(PS)).count() + " available");
    }

    private void refreshBillingTable() {
        double total = billingRecords.stream()
            .mapToDouble(BillingRecord::getTotalBill).sum();
        lblTotalRevenue.setText("₹" + String.format("%.0f", total));
        billingTable.setItems(FXCollections.observableArrayList(billingRecords));
    }

    private void setStatus(Label label, String msg, boolean isError) {
        label.setText(msg);
        label.setStyle(isError
            ? "-fx-text-fill: #f87171;"
            : "-fx-text-fill: #4ade80;");
    }
}