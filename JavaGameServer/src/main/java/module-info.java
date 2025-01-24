module com.mycompany.javagameserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.mariadb.jdbc;
    requires java.base;
    
    opens com.mycompany.javagameserver to javafx.fxml;
    exports com.mycompany.javagameserver;
}
