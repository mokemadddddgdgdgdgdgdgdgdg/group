package com.example.login;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.ResourceBundle;

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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddSemester implements Initializable {

    @FXML
    private ComboBox<String> Newsemester;

    @FXML
    private TextField start_date;

    @FXML
    private TextField end_date;

    @FXML
    private Label semester;

    @FXML
    private Button btn_back;

    @FXML
    private Button btn_save;

    @FXML
    private TextField feedback;


    private Connection connect;

    // Load the previous screen
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

    @FXML
    private void saveSemesterData() {
        String semester = Newsemester.getValue();
        String startdate = start_date.getText();
        String enddate = end_date.getText();

        // Validate that all fields have been selected
        if (semester == null || startdate.isEmpty() || enddate.isEmpty()) {
            System.out.println("Please select a semester and enter start and end date for that semester.");
            return;
        }

        String semesterdate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate date = null;
        boolean validDate = false;

        try{
            date = LocalDate.parse(startdate, formatter);
            date = LocalDate.parse(enddate, formatter);
            validDate = true;
        }catch (DateTimeParseException e) {
            feedback.setText("Invalid date format. Please use yyyy-MM-dd.");
        }

        // Insert data into the semester table
        String query = "INSERT INTO semester (semester_name, start_date, end_date) VALUES (?, ?, ?)";

        try (Connection connect = Connect.connectDb();
             PreparedStatement prepare = connect.prepareStatement(query)) {

            prepare.setString(1, semester);
            prepare.setString(2, startdate);
            prepare.setString(3, enddate);

            int rowsInserted = prepare.executeUpdate();
            if (rowsInserted > 0) {
                feedback.setText("Semester data saved successfully!");
            } else {
                feedback.setText("Failed to save semester data.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        ObservableList<String> semesterOptions = FXCollections.observableArrayList("S1", "S2");
        Newsemester.setItems(semesterOptions);

        btn_back.setOnAction(event -> loadPrevious());

        btn_save.setOnAction(event -> saveSemesterData());
    }
}
