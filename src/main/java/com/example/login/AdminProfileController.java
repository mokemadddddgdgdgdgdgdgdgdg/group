package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AdminProfileController implements Initializable {


    @FXML
    private Label Password;

    @FXML
    private Label username;


    @FXML
    private Button btn_back;

    @FXML
    private Button profile;

    @FXML
    private Button update;

    @FXML
    private TextField profileUsername;

    @FXML
    private TextField profilePassword;


    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;

    private String adminUsername;
    private String adminPassword;


    public void setAdminCredentials(String username, String password) {
        this.adminUsername = username;
        this.adminPassword = password;
    }


    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Admin Portal");
            stage.setScene(new Scene(root));
            stage.show();

            btn_back.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void ErrorMessage(String message) {
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void SuccessMessage(String message) {
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void loadProfile() {
        String query = "SELECT * FROM admin WHERE name = ? AND Password = ?";

        connect = Connect.connectDb();

        if (connect != null) {
            System.out.println("Database connection successful.");
        } else {
            System.out.println("Failed to connect to the database.");
        }
        try {
            prepare = connect.prepareStatement(query);
            prepare.setString(1, adminUsername);
            prepare.setString(2, adminPassword);

            result = prepare.executeQuery();

            if (result.next()) {
                profileUsername.setText(result.getString("name"));
                profilePassword.setText(result.getString("Password"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        profile.setOnAction(event -> loadProfile());

        btn_back.setOnAction(event -> loadPrevious());
    }
}
