module com.example.autoclicker {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.desktop;

    opens com.example.autoclicker to javafx.fxml;
    exports com.example.autoclicker;
}
