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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LecturerFillForm implements Initializable {
    @FXML
    private Label ID;

    @FXML
    private Label Passw;

    @FXML
    private Label lec;

    @FXML
    private TextField Lecname;


    @FXML
    private TextField Lecture_ID;

    @FXML
    private PasswordField passwordField;;

    @FXML
    private ComboBox<String> studentComboBox;

    @FXML
    private ComboBox<Integer> weekComboBox;

    @FXML
    private Button markPresentButton;

    @FXML
    private TextField chapterField;

    @FXML
    private TextArea learningOutcomesArea;

    @FXML
    private Button saveButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Button btn_logout;

    private Connection connection;

    public void setLoginDetails(String lecturerName, String lecturerID, String lecturerPassword) {
        Lecname.setText(lecturerName);
        Lecture_ID.setText(lecturerID);
        passwordField.setText(lecturerPassword);
    }



    // Load students from the database into the ComboBox
    private void loadStudents() {
        try {
            String query = "SELECT student_name FROM students";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                studentComboBox.getItems().add(resultSet.getString("student_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // marking a student as present
    @FXML
    void markStudentPresent() {
        String selectedStudent = studentComboBox.getValue();
        if (selectedStudent != null) {
            statusLabel.setText(selectedStudent + " marked as present.");
        } else {
            showAlert("No Student Selected", "Please select a student to mark as present.");
        }
    }

    // saving the form data to the database
    @FXML
    void saveChapterAndOutcomes() {
        String student_name = studentComboBox.getValue();
        String chapter = chapterField.getText();
        String learningOutcomes = learningOutcomesArea.getText();
        Integer week = weekComboBox.getValue();
        String presenceStatus = (statusLabel.getText().contains("absent")) ? "Absent" : "Present";
        String lecturerID = Lecture_ID.getText();
        String lecturerPassword = passwordField.getText();
        String lecturerName = Lecname.getText();

        if (validateInputs(student_name, chapter, learningOutcomes, week, lecturerID, lecturerPassword, lecturerName)) {
            try {
                String query = "INSERT INTO LecturerFillForm (student_name, week, chapter, learning_outcomes, presence_status, lecturer_id,name) VALUES (?, ?, ?, ?, ?, ?,?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, student_name);
                preparedStatement.setInt(2, week);
                preparedStatement.setString(3, chapter);
                preparedStatement.setString(4, learningOutcomes);
                preparedStatement.setString(5, presenceStatus);
                preparedStatement.setString(6, lecturerID);
                preparedStatement.setString(7,lecturerName);


                preparedStatement.executeUpdate();
                statusLabel.setText("Form saved successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }




    private boolean validateInputs(String student_name, String chapter, String learningOutcomes, Integer week, String lecturerID, String lecturerPassword, String lecturerName) {
        if (student_name == null || chapter.isEmpty() || learningOutcomes.isEmpty() || week == null || lecturerID.isEmpty() ||  lecturerName == null) {
            showAlert("Input Error", "All fields must be filled.");
            return false;
        }
        return true;
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Lecturer.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Lecturer Login Portal");
            stage.setScene(new Scene(root));
            stage.show();

            btn_logout.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        connection = Connect.connectDb();

        if (connection != null) {
            loadStudents();

            for (int i = 1; i <= 17; i++) {
                weekComboBox.getItems().add(i);
            }
        } else {
            showAlert("Database Connection Error", "Failed to connect to the database.");
        }

        btn_logout.setOnAction(event -> loadPrevious());
    }
}
