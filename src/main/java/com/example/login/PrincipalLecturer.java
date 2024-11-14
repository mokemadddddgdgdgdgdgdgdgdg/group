package com.example.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PrincipalLecturer implements Initializable {

    @FXML
    private Label chall_id;

    @FXML
    private TextArea chall_textarea_id;

    @FXML
    private ComboBox<String> class_id;

    @FXML
    private ComboBox<String> module_id;

    @FXML
    private Label recomm_id;

    @FXML
    private TextArea recomm_textarea_id;

    @FXML
    private Button submit_id;

    @FXML
    private ComboBox<String> week_id;

    @FXML
    private Button btn_back;

    @FXML
    private ListView<String> lecturerListView;

    @FXML
    private TextArea lecturerFormDetails;

    private ObservableList<String> lecturerNames = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateComboBoxes();
        populateLecturerListView();

        lecturerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadLecturerFormDetails(newValue);
            }
        });

        btn_back.setOnAction(event -> loadPrevious());
    }

    private void populateComboBoxes() {
        ObservableList<String> classes = FXCollections.observableArrayList("BSCBIT", "BSCIT", "BSCSM");
        class_id.setItems(classes);

        ObservableList<String> modules = FXCollections.observableArrayList(
                "Java Programming", "E-commerce System", "Software Project Management",
                "Decision Support System", "Internet Payment");
        module_id.setItems(modules);

        ObservableList<String> weeks = FXCollections.observableArrayList(
                "Wk 1", "Wk 2", "Wk 3", "Wk 4", "Wk 5", "Wk 6", "Wk 7",
                "Wk 8", "Wk 9", "Wk 10", "Wk 11", "Wk 12", "Wk 13",
                "Wk 14", "Wk 15", "Wk 16", "Wk 17");
        week_id.setItems(weeks);
    }

    private void populateLecturerListView() {
        ObservableList<String> lecturerNames = FXCollections.observableArrayList();
        try (Connection connection = Connect.connectDb();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM lecturerfillform")) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                lecturerNames.add(resultSet.getString("name"));
            }

            lecturerListView.setItems(lecturerNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void buttonSubmitReport(ActionEvent event) {
        String challengeText = chall_textarea_id.getText();
        String recommendationText = recomm_textarea_id.getText();
        String selectedClass = class_id.getValue();
        String selectedModule = module_id.getValue();
        String selectedWeek = week_id.getValue();

        if (challengeText.isEmpty() || recommendationText.isEmpty() ||
                selectedClass == null || selectedModule == null || selectedWeek == null) {
            showAlert("All fields are required!", Alert.AlertType.WARNING);
            return;
        }

        saveDataToDatabase(challengeText, recommendationText, selectedClass, selectedModule, selectedWeek);

        chall_textarea_id.clear();
        recomm_textarea_id.clear();
        class_id.getSelectionModel().clearSelection();
        module_id.getSelectionModel().clearSelection();
        week_id.getSelectionModel().clearSelection();
    }

    private void saveDataToDatabase(String challenge, String recommendation, String classId, String moduleId, String weekId) {
        String query = "INSERT INTO PrincipalLecturer (challenge, recommendation, class_id, module_id, week_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = Connect.connectDb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, challenge);
            preparedStatement.setString(2, recommendation);
            preparedStatement.setString(3, classId);
            preparedStatement.setString(4, moduleId);
            preparedStatement.setString(5, weekId);

            preparedStatement.executeUpdate();
            showAlert("Data saved successfully!", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error saving data!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PrincipalLecturerPortal.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Principal Portal");
            stage.setScene(new Scene(root));
            stage.show();

            btn_back.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load previous page!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void loadLecturerFormDetails(String lecturerName) {
        String query = "SELECT * FROM lecturerfillform WHERE name = ?";
        try (Connection connection = Connect.connectDb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, lecturerName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                String presenceStatus = resultSet.getString("presence_status");
                String chapter = resultSet.getString("chapter");
                String learningOutcomes = resultSet.getString("learning_outcomes");
                String week = resultSet.getString("week");

                lecturerFormDetails.setText(
                        "Student name: " + studentName + "\n" +
                                "Present status: " + presenceStatus + "\n" +
                                "Chapter: " + chapter + "\n" +
                                "Learning outcomes: " + learningOutcomes + "\n" +
                                "Week: " + week
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error loading lecturer details!", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.show();
    }
}