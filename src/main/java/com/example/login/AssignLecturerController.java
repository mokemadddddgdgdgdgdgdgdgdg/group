package com.example.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class AssignLecturerController implements Initializable {

    @FXML
    private Button assignButton;

    @FXML
    private ComboBox<String> classComboBox;

    @FXML
    private ComboBox<String> lecturerComboBox;

    @FXML
    private ComboBox<String> moduleComboBox;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label statusLabel;

    @FXML
    private Button btn_back;

    private Connection connect;

    // Method to fetch lecturer names from the database and populate the ComboBox
    private void loadLecturers() {
        ObservableList<String> lecturerList = FXCollections.observableArrayList();

        String query = "SELECT name FROM lecturers";

        try {
            connect = Connect.connectDb();
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String lecturerName = resultSet.getString("name");
                lecturerList.add(lecturerName);
            }

            lecturerComboBox.setItems(lecturerList);

            resultSet.close();
            statement.close();
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to fetch module names from the database and populate the ComboBox
    private void loadModules() {
        ObservableList<String> moduleList = FXCollections.observableArrayList();

        String query = "SELECT module_name FROM modules";

        try {
            connect = Connect.connectDb();
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String moduleName = resultSet.getString("module_name");
                moduleList.add(moduleName);
            }

            moduleComboBox.setItems(moduleList);

            resultSet.close();
            statement.close();
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoles() {
        ObservableList<String> roleList = FXCollections.observableArrayList();

        String query = "SELECT role_name FROM roles";

        try {
            connect = Connect.connectDb();
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String roleName = resultSet.getString("role_name");
                roleList.add(roleName);
            }

            roleComboBox.setItems(roleList);

            resultSet.close();
            statement.close();
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadClasses() {
        ObservableList<String> classList = FXCollections.observableArrayList();

        String query = "SELECT class_name FROM classes";

        try {
            connect = Connect.connectDb();
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String className = resultSet.getString("class_name");
                classList.add(className);
            }

            classComboBox.setItems(classList);

            resultSet.close();
            statement.close();
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void assign() {
        String lecturer = lecturerComboBox.getValue();
        String module = moduleComboBox.getValue();
        String role = roleComboBox.getValue();
        String className = classComboBox.getValue();

        if (lecturer == null || module == null || role == null || className == null) {
            statusLabel.setText("Please select all fields.");
            return;
        }

        String query = "INSERT INTO assigned (lecturer, module, role, class) VALUES (?, ?, ?, ?)";

        try {
            connect = Connect.connectDb();
            PreparedStatement statement = connect.prepareStatement(query);

            statement.setString(1, lecturer);
            statement.setString(2, module);
            statement.setString(3, role);
            statement.setString(4, className);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                statusLabel.setText("Assignment successful!");
            } else {
                statusLabel.setText("Failed to assign.");
            }

            statement.close();
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("An error occurred.");
        }
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLecturers();
        loadModules();
        loadRoles();
        loadClasses();

        btn_back.setOnAction(event -> loadPrevious());
        assignButton.setOnAction(event -> assign());
    }
}
