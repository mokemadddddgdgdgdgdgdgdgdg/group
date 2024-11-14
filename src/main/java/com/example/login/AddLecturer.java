package com.example.login;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddLecturer implements Initializable {

    @FXML
    private TextField dept;

    @FXML
    private TextField lecturerId;

    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnBack;

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3307/limkokwing";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "khopolo";

    @FXML
    private void handleAddLecturer() {
        String lecturerName = name.getText().trim();
        String department = dept.getText().trim();
        String lecturerIdText = lecturerId.getText().trim();

        // Generate the email address based on the entered name
        String generatedEmail = lecturerName.toLowerCase().replaceAll(" ", "_") + "@limko.ls";

        // Validation
        if (lecturerName.isEmpty() || department.isEmpty() || lecturerIdText.isEmpty()) {
            showAlert("Validation Error", "All fields are required", "Please fill in all fields before submitting.");
            return;
        }

        // Connect to the database and insert the lecturer with the generated email
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            String sql = "INSERT INTO lecturers (name, lecturer_id, department, email) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, lecturerName);
            statement.setString(2, lecturerIdText);
            statement.setString(3, department);
            statement.setString(4, generatedEmail);  // Use generated email

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                showAlert("Add Lecturer", "Lecturer Added", "Lecturer " + lecturerName + " has been added successfully!");
                clearFields(); // Clear fields after insertion
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Error Adding Lecturer", "An error occurred while adding the lecturer. Please try again.");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean isDatabaseConnected() {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD)) {
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void clearFields() {
        name.clear();
        lecturerId.clear();
        dept.clear();
        email.clear();
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

            btnBack.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAdd.setOnAction(event -> handleAddLecturer());
        btnBack.setOnAction(event -> loadPrevious());
    }
}
