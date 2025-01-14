module com.mycompany.javagameserver {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.javagameserver to javafx.fxml;
    exports com.mycompany.javagameserver;
}
