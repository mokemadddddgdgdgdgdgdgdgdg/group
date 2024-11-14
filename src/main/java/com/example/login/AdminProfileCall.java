package com.example.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminProfileCall {

    @FXML
    private Button backbttn;

    @FXML
    private TextField emailtxtfield;

    @FXML
    private TextField idtxtfield;

    @FXML
    private TextField nametxtfield;

    // Initialize and populate fields with admin details
    public void initialize() {
        loadAdminDetails();
    }

    // Method to load admin details from the database
    private void loadAdminDetails() {
        try (Connection connection = Connect.connectDb();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT admin_id, name, email FROM admin WHERE admin_id = ?")) {

            // Set the admin ID here; replace '1' with the actual logged-in admin's ID
            preparedStatement.setInt(1, 1);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // Populate the text fields with data from the database
                idtxtfield.setText(resultSet.getString("admin_id"));
                nametxtfield.setText(resultSet.getString("name"));
                emailtxtfield.setText(resultSet.getString("email"));
            }
            resultSet.close();

        } catch (Exception e) {
            System.out.println("Failed to load admin details: " + e.getMessage());
            showErrorAlert("Error Loading Admin Profile", "Could not load admin details from the database.");
        }
    }

    // Method to handle the back button action
    @FXML
    private void handleBackButton(ActionEvent event) {
        try {
            // Load the admin.fxml file with the correct path
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/login/admin.fxml"));
            Parent root = loader.load();

            // Get the current stage and set the new scene
            Stage stage = (Stage) backbttn.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.out.println("Failed to load the admin view: " + e.getMessage());
            showErrorAlert("Error", "Could not return to the previous screen.");
        }
    }

    // Utility method to show error alerts
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
