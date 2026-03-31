package com.hotel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    // ── Tab Pane ──────────────────────────────────────────────
    @FXML private TabPane mainTabPane;

    // ── Dashboard ─────────────────────────────────────────────
    @FXML private Label lblTotalGuests;
    @FXML private Label lblOccupied;
    @FXML private Label lblAvailable;
    @FXML private Label lblRevenue;

    @FXML private Label lblRSStat;   // Regular Single
    @FXML private Label lblRDStat;   // Regular Double
    @FXML private Label lblDSStat;   // Deluxe Single
    @FXML private Label lblDDStat;   // Deluxe Double

    @FXML private ProgressBar pbRS;
    @FXML private ProgressBar pbRD;
    @FXML private ProgressBar pbDS;
    @FXML private ProgressBar pbDD;

    @FXML private TableView<GuestRow> guestTable;
    @FXML private TableColumn<GuestRow, String> colGuestRoom;
    @FXML private TableColumn<GuestRow, String> colGuestName;
    @FXML private TableColumn<GuestRow, String> colGuestPhone;
    @FXML private TableColumn<GuestRow, String> colGuestType;
    @FXML private TableColumn<GuestRow, String> colGuestDays;
    @FXML private TableColumn<GuestRow, String> colGuestCheckIn;

    // ── Booking ───────────────────────────────────────────────
    @FXML private VBox chipRS, chipRD, chipDS, chipDD;
    @FXML private Label availRS, availRD, availDS, availDD;
    @FXML private TextField bookNameField;
    @FXML private TextField bookPhoneField;
    @FXML private TextField bookDaysField;
    @FXML private Label bookStatusLabel;
    @FXML private Label bookRoomInfoLabel;

    private RoomType selectedRoomType = null;
    private static final String CHIP_BASE    = "room-select-chip";
    private static final String CHIP_ACTIVE  = "room-select-chip-active";
    private static final String CHIP_SOLD    = "room-select-chip-sold";

    // ── Checkout ──────────────────────────────────────────────
    @FXML private TextField checkoutRoomField;
    @FXML private TextField checkoutDaysField;
    @FXML private Label checkoutStatusLabel;
    @FXML private Label checkoutGuestLabel;

    // ── Billing ───────────────────────────────────────────────
    @FXML private TableView<BillingRow> billingTable;
    @FXML private TableColumn<BillingRow, String> colBillDate;
    @FXML private TableColumn<BillingRow, String> colBillRoom;
    @FXML private TableColumn<BillingRow, String> colBillGuest;
    @FXML private TableColumn<BillingRow, String> colBillPhone;
    @FXML private TableColumn<BillingRow, String> colBillType;
    @FXML private TableColumn<BillingRow, String> colBillDays;
    @FXML private TableColumn<BillingRow, String> colBillAmount;

    @FXML private Label lblTotalRevenue;

    // ─────────────────────────────────────────────────────────

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupGuestTable();
        setupBillingTable();
        refreshDashboard();
        refreshBilling();
        refreshBookingChips();
    }

    // ══════════════════ DASHBOARD ═════════════════════════════

    private void setupGuestTable() {
        colGuestRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colGuestName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colGuestPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colGuestType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colGuestDays.setCellValueFactory(new PropertyValueFactory<>("days"));
        colGuestCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
    }

    private void refreshDashboard() {
        int guests = DataBase.occupiedRooms();
        int occupied = DataBase.occupiedRooms();
        int available = DataBase.availableRooms();
        int revenue = DataBase.totalRevenue();

        lblTotalGuests.setText(String.valueOf(guests));
        lblOccupied.setText(String.valueOf(occupied));
        lblAvailable.setText(String.valueOf(available));
        lblRevenue.setText("₹" + String.format("%,d", revenue));

        // Per-type occupancy
        for (RoomType type : RoomType.values()) {
            int total = DataBase.totalByType(type);
            int occ = DataBase.occupiedByType(type);
            double pct = total == 0 ? 0 : (double) occ / total;
            String label = String.format("%d / %d  (%.0f%%)", occ, total, pct * 100);

            switch (type) {
                case RegularSingle : { lblRSStat.setText(label); pbRS.setProgress(pct); break;}
                case RegularDouble : { lblRDStat.setText(label); pbRD.setProgress(pct); break;}
                case DeluxeSingle  : { lblDSStat.setText(label); pbDS.setProgress(pct); break;}
                case DeluxeDouble  : { lblDDStat.setText(label); pbDD.setProgress(pct); break;}
            }
        }

        // Guest table
        ObservableList<GuestRow> rows = FXCollections.observableArrayList();
        for (HotelRoom room : DataBase.rooms) {
            if (room.guest != null) {
                rows.add(new GuestRow(
                        String.valueOf(room.roomNumber),
                        room.guest.name,
                        room.guest.phone,
                        room.roomType.displayName(),
                        String.valueOf(room.guest.daysBooked),
                        room.guest.checkInDate
                ));
            }
        }
        guestTable.setItems(rows);
    }

    // ══════════════════ BOOKING ═══════════════════════════════

    /** Called on initialize and after every booking to refresh chip availability counts. */
    private void refreshBookingChips() {
        VBox[]   chips = { chipRS, chipRD, chipDS, chipDD };
        Label[]  avail = { availRS, availRD, availDS, availDD };
        RoomType[] types = RoomType.values();

        for (int i = 0; i < types.length; i++) {
            int free = DataBase.totalByType(types[i]) - DataBase.occupiedByType(types[i]);
            avail[i].setText(free > 0 ? free + " available" : "Fully booked");

            VBox chip = chips[i];
            chip.getStyleClass().removeAll(CHIP_BASE, CHIP_ACTIVE, CHIP_SOLD);
            if (free == 0) {
                chip.getStyleClass().add(CHIP_SOLD);
                chip.setDisable(true);
            } else if (types[i] == selectedRoomType) {
                chip.getStyleClass().add(CHIP_ACTIVE);
                chip.setDisable(false);
            } else {
                chip.getStyleClass().add(CHIP_BASE);
                chip.setDisable(false);
            }
        }
    }

    @FXML
    private void onSelectRoomType(javafx.scene.input.MouseEvent e) {
        VBox clicked = (VBox) e.getSource();
        RoomType[] types = RoomType.values();
        VBox[] chips = { chipRS, chipRD, chipDS, chipDD };
        for (int i = 0; i < chips.length; i++) {
            if (chips[i] == clicked) {
                selectedRoomType = types[i];
                break;
            }
        }
        refreshBookingChips();

        // Find next available room of this type and show info
        HotelRoom preview = DataBase.nextAvailableOfType(selectedRoomType);
        if (preview != null) {
            bookRoomInfoLabel.setText("✓  " + selectedRoomType.displayName()
                    + "  ·  ₹" + selectedRoomType.ratePerNight + " / night"
                    + "  ·  Next available: Room " + preview.roomNumber);
            bookRoomInfoLabel.setStyle("-fx-text-fill: #4ade80;");
        }
    }

    @FXML
    private void onBookRoom() {
        if (selectedRoomType == null) {
            setStatus(bookStatusLabel, "Please select a room type first.", false); return;
        }
        String name    = bookNameField.getText().trim();
        String phone   = bookPhoneField.getText().trim();
        String daysStr = bookDaysField.getText().trim();

        if (name.isEmpty() || daysStr.isEmpty()) {
            setStatus(bookStatusLabel, "Please fill in all required fields.", false); return;
        }

        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days <= 0) { setStatus(bookStatusLabel, "Nights must be at least 1.", false); return; }
        } catch (NumberFormatException e) {
            setStatus(bookStatusLabel, "Invalid number of nights.", false); return;
        }

        HotelRoom room = DataBase.nextAvailableOfType(selectedRoomType);
        if (room == null) {
            setStatus(bookStatusLabel, "No rooms available for this type.", false); return;
        }

        Guest guest = new Guest(name, phone.isEmpty() ? "N/A" : phone, days);
        room.bookRoom(guest);
        DataBase.save();

        setStatus(bookStatusLabel,
                "✓ Room " + room.roomNumber + " (" + selectedRoomType.displayName()
                + ") booked for " + name + " — " + days + " night(s).", true);

        // Reset form
        bookNameField.clear();
        bookPhoneField.clear();
        bookDaysField.clear();
        selectedRoomType = null;
        bookRoomInfoLabel.setText("← Select a room type above to continue");
        bookRoomInfoLabel.setStyle("");
        refreshBookingChips();
        refreshDashboard();
    }

    // ══════════════════ CHECKOUT ══════════════════════════════

    @FXML
    private void onLookupCheckout() {
        String input = checkoutRoomField.getText().trim();
        if (input.isEmpty()) { checkoutGuestLabel.setText("Enter a room number first."); return; }
        try {
            int num = Integer.parseInt(input);
            HotelRoom room = DataBase.getRoomByNumber(num);
            if (room == null) {
                checkoutGuestLabel.setText("Room not found.");
            } else if (room.isAvailable) {
                checkoutGuestLabel.setText("Room " + num + " is currently vacant.");
            } else {
                checkoutGuestLabel.setText(
                        "Guest: " + room.guest.name + " | Phone: " + room.guest.phone
                        + " | Booked for: " + room.guest.daysBooked + " nights"
                        + " | Check-in: " + room.guest.checkInDate
                );
            }
        } catch (NumberFormatException e) {
            checkoutGuestLabel.setText("Invalid room number.");
        }
    }

    @FXML
    private void onCheckout() {
        String roomStr = checkoutRoomField.getText().trim();
        String daysStr = checkoutDaysField.getText().trim();

        if (roomStr.isEmpty()) { setStatus(checkoutStatusLabel, "Enter a room number.", false); return; }

        try {
            int roomNum = Integer.parseInt(roomStr);
            HotelRoom room = DataBase.getRoomByNumber(roomNum);
            if (room == null)  { setStatus(checkoutStatusLabel, "Room not found.", false); return; }
            if (room.isAvailable) { setStatus(checkoutStatusLabel, "Room is already vacant.", false); return; }

            BillingRecord record;
            if (daysStr.isEmpty()) {
                record = room.checkOut();
            } else {
                int actualDays = Integer.parseInt(daysStr);
                if (actualDays <= 0) { setStatus(checkoutStatusLabel, "Days must be at least 1.", false); return; }
                record = room.checkOut(actualDays);
            }

            if (record != null) {
                DataBase.billingHistory.add(record);
                DataBase.save();
                setStatus(checkoutStatusLabel,
                        "✓ Checked out " + record.guestName + " from room " + roomNum
                        + ". Bill: ₹" + String.format("%,d", record.totalBill), true);
                checkoutRoomField.clear();
                checkoutDaysField.clear();
                checkoutGuestLabel.setText("");
                refreshDashboard();
                refreshBilling();
            } else {
                setStatus(checkoutStatusLabel, "Checkout failed.", false);
            }
        } catch (NumberFormatException e) {
            setStatus(checkoutStatusLabel, "Invalid number format.", false);
        }
    }

    // ══════════════════ BILLING ═══════════════════════════════

    private void setupBillingTable() {
        colBillDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colBillRoom.setCellValueFactory(new PropertyValueFactory<>("room"));
        colBillGuest.setCellValueFactory(new PropertyValueFactory<>("guest"));
        colBillPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colBillType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colBillDays.setCellValueFactory(new PropertyValueFactory<>("days"));
        colBillAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void refreshBilling() {
        ObservableList<BillingRow> rows = FXCollections.observableArrayList();
        List<BillingRecord> history = new ArrayList<>(DataBase.billingHistory);
        Collections.reverse(history); // newest first
        for (BillingRecord r : history) {
            rows.add(new BillingRow(
                    r.checkOutDate,
                    String.valueOf(r.roomNumber),
                    r.guestName,
                    r.guestPhone,
                    r.roomType,
                    String.valueOf(r.daysStayed),
                    "₹" + String.format("%,d", r.totalBill)
            ));
        }
        billingTable.setItems(rows);
        lblTotalRevenue.setText("₹" + String.format("%,d", DataBase.totalRevenue()));
    }

    // ══════════════════ HELPERS ═══════════════════════════════

    private void setStatus(Label label, String msg, boolean success) {
        label.setText(msg);
        label.setStyle(success
                ? "-fx-text-fill: #4ade80; -fx-font-weight: bold;"
                : "-fx-text-fill: #f87171; -fx-font-weight: bold;");
    }

    // ══════════════════ TABLE ROW MODELS ══════════════════════

    public static class GuestRow {
        private final String room, name, phone, type, days, checkIn;
        public GuestRow(String room, String name, String phone, String type, String days, String checkIn) {
            this.room = room; this.name = name; this.phone = phone;
            this.type = type; this.days = days; this.checkIn = checkIn;
        }
        public String getRoom()    { return room; }
        public String getName()    { return name; }
        public String getPhone()   { return phone; }
        public String getType()    { return type; }
        public String getDays()    { return days; }
        public String getCheckIn() { return checkIn; }
    }

    public static class BillingRow {
        private final String date, room, guest, phone, type, days, amount;
        public BillingRow(String date, String room, String guest, String phone, String type, String days, String amount) {
            this.date = date; this.room = room; this.guest = guest;
            this.phone = phone; this.type = type; this.days = days; this.amount = amount;
        }
        public String getDate()   { return date; }
        public String getRoom()   { return room; }
        public String getGuest()  { return guest; }
        public String getPhone()  { return phone; }
        public String getType()   { return type; }
        public String getDays()   { return days; }
        public String getAmount() { return amount; }
    }
}
