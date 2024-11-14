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

public class PrincipalLecturerPortalController implements Initializable {

    @FXML
    private TextField PrincipalLecturer_ID;

    @FXML
    private Button PrincipalLecturer_LoginBtn;

    @FXML
    private PasswordField PrincipalLecturer_Password;

    @FXML
    private ComboBox<String> PrincipalLecturer_Select;

    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Alert alert;

    private void ErrorMessage(String message){
        alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void SuccessMessage(String message){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Hash the password using BCrypt
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public void loginAccount(){
        if (PrincipalLecturer_ID.getText().isEmpty() || PrincipalLecturer_Password.getText().isEmpty()) {
            ErrorMessage("Please fill in the blanks");
        } else {
            String selectData = "SELECT Password FROM principal WHERE name = ?";

            connect = Connect.connectDb();

            try {
                prepare = connect.prepareStatement(selectData);
                prepare.setString(1, PrincipalLecturer_ID.getText());
                result = prepare.executeQuery();

                if (result.next()) {
                    String storedHashedPassword = result.getString("Password");
                    String enteredPassword = PrincipalLecturer_Password.getText();

                    if (BCrypt.checkpw(enteredPassword, storedHashedPassword)) {
                        SuccessMessage("Login successful");
                        String currentTime = HelloController.getCurrentTime();
                        HelloController.logLoginDetails(PrincipalLecturer_ID.getText(), currentTime);

                        Parent root = FXMLLoader.load(getClass().getResource("Principaldash.fxml"));
                        Stage stage = new Stage();
                        stage.setTitle("Principal Lecturer form");
                        stage.setScene(new Scene(root));
                        stage.show();

                        PrincipalLecturer_LoginBtn.getScene().getWindow().hide();
                    } else {
                        ErrorMessage("Incorrect Username/Password");
                    }
                } else {
                    ErrorMessage("User not found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeConnections();
            }
        }
    }

    private void closeConnections() {
        try {
            if (result != null) result.close();
            if (prepare != null) prepare.close();
            if (connect != null) connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchform(){
        try {
            Parent root = null;
            String selectedPortal = PrincipalLecturer_Select.getSelectionModel().getSelectedItem();
            if ("Lecturer Portal".equals(selectedPortal)){
                root = FXMLLoader.load(getClass().getResource("Lecturer.fxml"));
            } else if ("Admin Portal".equals(selectedPortal)) {
                root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
            } else if ("Principal Lecturer Portal".equals(selectedPortal)) {
                root = FXMLLoader.load(getClass().getResource("PrincipalLecturerPortal.fxml"));
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            PrincipalLecturer_Select.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> users;

    public PrincipalLecturerPortalController() {
        users = new ArrayList<>();
        users.add("Admin Portal");
        users.add("Principal Lecturer Portal");
        users.add("Lecturer Portal");
    }

    public void selectuser() {
        ObservableList<String> ListData = FXCollections.observableArrayList(users);
        PrincipalLecturer_Select.setItems(ListData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectuser();
    }
}
