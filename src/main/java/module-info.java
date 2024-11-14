module com.example.login {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.desktop;
    requires java.sql;
    requires jbcrypt;

    //requires mysql.connector.j;

    opens com.example.login to javafx.fxml;
    exports com.example.login;
}