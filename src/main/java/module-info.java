module com.hotel {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.hotel to javafx.fxml;
    opens com.hotel.controller to javafx.fxml;
    opens com.hotel.model to javafx.fxml, javafx.base;
    opens com.hotel.service to javafx.fxml;
    opens com.hotel.storage to javafx.fxml;

    exports com.hotel;
}