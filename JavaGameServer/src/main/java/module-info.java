module com.mycompany.javagameserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    
    opens com.mycompany.javagameserver to javafx.fxml;
    exports com.mycompany.javagameserver;
}
