package com.example.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private PasswordField Admin_Password;

    @FXML
    private ComboBox<String> Admin_User;

    @FXML
    private TextField Admin_UserName;

    @FXML
    private Button Admin_loginBtn;

    private List<String> users;
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Alert alert;

    private String loggedInUsername;

    private void ErrorMessage(String message) {
        alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void SuccessMessage(String message) {
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Method to hash a password with BCrypt
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Method to check if the entered password matches the stored hashed password
    private boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    // Method to get the current time in the format yyyy-MM-dd HH:mm:ss
    public static String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }

    // Method to log the login details to a file
    public static void logLoginDetails(String username, String time) {
        String logFilePath = "login_log.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write("Username: " + username + " | Login Time: " + time);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Failed to write login details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loginAccount() {

        if (Admin_UserName.getText().isEmpty() || Admin_Password.getText().isEmpty()) {
            ErrorMessage("Please fill in the blanks");
        } else {
            String selectData = "SELECT * FROM admin WHERE name = ?";
            connect = Connect.connectDb();

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, Admin_UserName.getText());
                result = prepare.executeQuery();

                if (result.next()) {
                    String storedHashedPassword = result.getString("Password");
                    String enteredPassword = Admin_Password.getText();

                    // Check if the entered password matches the stored hashed password
                    if (checkPassword(enteredPassword, storedHashedPassword)) {
                        loggedInUsername = Admin_UserName.getText();
                        SuccessMessage("Login successful");

                        // Log the login details
                        String currentTime = getCurrentTime();
                        logLoginDetails(loggedInUsername, currentTime);

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
                        Parent root = loader.load();

                        admin dashboardController = loader.getController();
                        dashboardController.setLoggedInCredentials(loggedInUsername, storedHashedPassword);

                        Stage stage = new Stage();
                        stage.setTitle("Admin form");
                        stage.setScene(new Scene(root));
                        stage.show();

                        Admin_loginBtn.getScene().getWindow().hide();
                    } else {
                        ErrorMessage("Incorrect Username/Password");
                    }
                } else {
                    ErrorMessage("Incorrect Username/Password");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void viewProfile(String username, String password) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminProfile.fxml"));
            Parent root = loader.load();

            AdminProfileController profileController = loader.getController();
            profileController.setAdminCredentials(username, password);

            profileController.loadProfile();

            Stage stage = new Stage();
            stage.setTitle("Admin Profile");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchform() {
        try {
            Parent root = null;
            String selection = Admin_User.getSelectionModel().getSelectedItem();

            if (selection.equals("Principal Lecturer Portal")) {
                root = FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            } else if (selection.equals("Lecturer Portal")) {
                root = FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            } else if (selection.equals("Admin Portal")) {
                root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            Admin_User.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HelloController() {
        users = new ArrayList<>();
        users.add("Admin Portal");
        users.add("Principal Lecturer Portal");
        users.add("Lecturer Portal");
    }

    public void SelectUser() {
        ObservableList<String> ListData = FXCollections.observableArrayList(users);
        Admin_User.setItems(ListData);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SelectUser();
    }
}
