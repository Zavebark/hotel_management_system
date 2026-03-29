module com.hotel {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.hotel to javafx.fxml;
    exports com.hotel;
}
