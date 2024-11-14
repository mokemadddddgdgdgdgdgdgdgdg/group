package com.example.login;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML
    private Button btn_register;

    @FXML
    private TextField new_id;

    @FXML
    private PasswordField confirm_Password;

    @FXML
    private TextField new_name;

    @FXML
    private PasswordField new_password;

    @FXML
    private ComboBox<String> select_account;

    @FXML
    private ComboBox<String> select_role;

    private Alert alert;
    private Connection connect;
    private PreparedStatement prepare;

    private final String[] roles = {"lecturer", "admin", "principal lecturer"};
    private final List<String> users = new ArrayList<>(List.of("Admin Portal", "Principal Lecturer Portal", "Lecturer Portal"));

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        select_role.setItems(FXCollections.observableArrayList(roles));
        select_account.setItems(FXCollections.observableArrayList(users));
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

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    private void validateAndRegisterAccount() {
        // Check if fields are filled and passwords match
        if (new_name.getText().isEmpty() || new_password.getText().isEmpty() || new_id.getText().isEmpty() || confirm_Password.getText().isEmpty() || select_role.getSelectionModel().isEmpty()) {
            ErrorMessage("Please fill in all fields.");
            return;
        }

        // Check if passwords match
        if (!new_password.getText().equals(confirm_Password.getText())) {
            ErrorMessage("Passwords do not match.");
            return;
        }

        // Hash the password with BCrypt
        String hashedPassword = hashPassword(new_password.getText());

        // Validate that ID is an integer
        try {
            Integer.parseInt(new_id.getText());
        } catch (NumberFormatException e) {
            ErrorMessage("The ID must be an integer.");
            return;
        }

        // Check for empty name field
        if (new_name.getText().isEmpty()) {
            ErrorMessage("Name field cannot be empty.");
            return;
        }

        String selectedRole = select_role.getValue();
        String insertData = getInsertStatementForRole(selectedRole);

        if (insertData.isEmpty()) {
            ErrorMessage("Invalid role selected.");
            return;
        }

        // Connect to the database and insert data
        connect = Connect.connectDb(); // Ensure Connect.connectDb() is correctly configured

        try {
            // Prepare the SQL statement
            prepare = connect.prepareStatement(insertData);

            // For "admin", set the additional email parameter
            if ("admin".equalsIgnoreCase(selectedRole)) {
                String email = new_name.getText() + "admin@gmail.com";
                prepare.setString(1, new_name.getText());        // Name
                prepare.setString(2, new_id.getText());           // ID
                prepare.setString(3, hashedPassword);             // Hashed Password
                prepare.setString(4, email);                      // Email
            } else {
                prepare.setString(1, new_name.getText());        // Name
                prepare.setString(2, new_id.getText());           // ID
                prepare.setString(3, hashedPassword);             // Hashed Password
            }

            // Execute the update query
            int rowsAffected = prepare.executeUpdate();

            // Check if the insert was successful
            if (rowsAffected > 0) {
                SuccessMessage("Registration successful");

                // Navigate to the corresponding form based on role
                navigateToRoleForm(selectedRole);

                // Close the registration window
                btn_register.getScene().getWindow().hide();
            } else {
                ErrorMessage("Registration failed. Please try again.");
            }

        } catch (SQLException e) {
            ErrorMessage("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Always close the resources
            try {
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getInsertStatementForRole(String role) {
        switch (role.toLowerCase()) {
            case "lecturer":
                return "INSERT INTO lecturers (name, lecturer_id, password) VALUES (?, ?, ?)";
            case "admin":
                return "INSERT INTO admin (name, admin_id, password, email) VALUES (?, ?, ?, ?)";
            case "principal lecturer":
                return "INSERT INTO principal (name, principalID, password) VALUES (?, ?, ?)";
            default:
                return "";
        }
    }

    private void navigateToRoleForm(String role) throws IOException {
        String fxmlFile = "";
        String title = "";

        switch (role.toLowerCase()) {
            case "lecturer":
                fxmlFile = "LecturerFillForm.fxml";
                title = "Lecturer Form";
                break;
            case "admin":
                fxmlFile = "admin.fxml";
                title = "Admin Dashboard";
                break;
            case "principal lecturer":
                fxmlFile = "Principaldash.fxml";
                title = "Principal Lecturer Dashboard";
                break;
        }

        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void switchform() {
        try {
            Parent root = null;
            String selectedPortal = select_account.getSelectionModel().getSelectedItem();
            if (selectedPortal != null) {
                root = loadPortalForm(selectedPortal);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
                select_account.getScene().getWindow().hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorMessage("Error switching form.");
        }
    }

    private Parent loadPortalForm(String portal) throws Exception {
        switch (portal) {
            case "Lecturer Portal":
                return FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            case "Admin Portal":
                return FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            case "Principal Lecturer Portal":
                return FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            default:
                throw new IllegalArgumentException("Invalid portal");
        }
    }

    public void registerAccount() {
        validateAndRegisterAccount();
    }
}
