package com.example.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LecturerController implements Initializable {

    @FXML private TextField Lecturer_ID;
    @FXML private Button Lecturer_LoginBtn;
    @FXML private PasswordField Lecturer_Password;
    @FXML private ComboBox<String> Lecturer_Select;
    @FXML private TextField lecturer_name;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Alert alert;

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

    @FXML
    public void loginAccount() {

        if (lecturer_name.getText().isEmpty() || Lecturer_ID.getText().isEmpty() || Lecturer_Password.getText().isEmpty()) {
            ErrorMessage("Please fill in the blanks");
            return;
        }

        String selectData = "SELECT password FROM lecturers WHERE name = ? and lecturer_id = ?";
        connect = Connect.connectDb();

        try {
            // Query to retrieve the hashed password for the given name and ID
            prepare = connect.prepareStatement(selectData);
            prepare.setString(1, lecturer_name.getText());
            prepare.setString(2, Lecturer_ID.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                String storedHashedPassword = result.getString("password");

                // Check if the provided password matches the stored hashed password
                if (BCrypt.checkpw(Lecturer_Password.getText(), storedHashedPassword)) {
                    SuccessMessage("Login successful");

                    // Log login details
                    String currentTime = HelloController.getCurrentTime();
                    HelloController.logLoginDetails(lecturer_name.getText(), currentTime);

                    // Load the lecturer form
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("LecturerFillForm.fxml"));
                    Parent root = loader.load();

                    LecturerFillForm lecturerFillFormController = loader.getController();
                    lecturerFillFormController.setLoginDetails(lecturer_name.getText(), Lecturer_ID.getText(), Lecturer_Password.getText());

                    Stage stage = new Stage();
                    stage.setTitle("Lecturer form");
                    stage.setScene(new Scene(root));
                    stage.show();

                    // Close the current login window
                    Lecturer_LoginBtn.getScene().getWindow().hide();

                } else {
                    ErrorMessage("Incorrect UserName/Password");
                }
            } else {
                ErrorMessage("User not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            ErrorMessage("An error occurred during login.");
        } finally {
            try {
                if (prepare != null) prepare.close();
                if (connect != null) connect.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    public void switchform() {
        try {
            Parent root = null;
            String selectedPortal = Lecturer_Select.getSelectionModel().getSelectedItem();
            if ("Admin Portal".equals(selectedPortal)) {
                root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            } else if ("Principal Lecturer Portal".equals(selectedPortal)) {
                root = FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            } else if ("Lecturer Portal".equals(selectedPortal)) {
                root = FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            }
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            Lecturer_Select.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> users;

    public LecturerController() {
        users = new ArrayList<>();
        users.add("Admin Portal");
        users.add("Principal Lecturer Portal");
        users.add("Lecturer Portal");
    }

    public void selectuser() {
        ObservableList<String> ListData = FXCollections.observableArrayList(users);
        Lecturer_Select.setItems(ListData);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectuser();
    }
}
