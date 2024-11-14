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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class YearSemesterModule implements Initializable {

    @FXML private ComboBox<String> combo_newyear;
    @FXML private Button btn_back, btn_add, btn_save, Btn_Add;
    @FXML private Label year, semester;
    @FXML private ComboBox<String> Newsemester, combo_AddModule;
    @FXML private TextField txt_Credits, feedback;
    @FXML private DatePicker start_date, end_date;

    private Connection connect;
    private final String[] modules = {
            "Java Programming II", "Strategic Marketing Management", "Internet Payment",
            "Software Project Management", "Decision Support System", "E-Commerce Systems",
            "Concepts of Organisation", "Major Project"
    };

    @FXML
    private void addModule() {
        String selectedModule = combo_AddModule.getSelectionModel().getSelectedItem();
        String creditsStr = txt_Credits.getText();

        if (selectedModule == null || creditsStr.isEmpty()) {
            feedback.setText("Please select a module and enter credits.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsStr);
        } catch (NumberFormatException e) {
            feedback.setText("Invalid credits value. Please enter a number.");
            return;
        }

        String insertModuleSQL = "INSERT INTO modules (module_name, credits) VALUES (?, ?)";
        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(insertModuleSQL);
            prepare.setString(1, selectedModule);
            prepare.setInt(2, credits);
            int result = prepare.executeUpdate();

            feedback.setText(result > 0 ? "Module added successfully: " + selectedModule : "Failed to add the module.");
            combo_AddModule.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
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

    @FXML
    private void addYear() {
        String selectedYear = combo_newyear.getValue();
        if (selectedYear != null) {
            saveYearToDatabase(selectedYear);
        } else {
            feedback.setText("Please select a year to add.");
        }
    }

    private void saveYearToDatabase(String year) {
        String query = "INSERT INTO years (year) VALUES (?)";
        try (Connection connection = Connect.connectDb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, year);
            preparedStatement.executeUpdate();
            feedback.setText("Year added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void saveSemesterData() {
        String semester = Newsemester.getValue();
        LocalDate startDate = start_date.getValue();
        LocalDate endDate = end_date.getValue();

        if (semester == null || startDate == null || endDate == null) {
            feedback.setText("Please select a semester and enter start and end dates.");
            return;
        }

        String start_dateString = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String end_dateString = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String query = "INSERT INTO semester (semester_name, start_date, end_date) VALUES (?, ?, ?)";
        try (Connection connect = Connect.connectDb();
             PreparedStatement prepare = connect.prepareStatement(query)) {

            prepare.setString(1, semester);
            prepare.setString(2, start_dateString);
            prepare.setString(3, end_dateString);

            int rowsInserted = prepare.executeUpdate();
            feedback.setText(rowsInserted > 0 ? "Semester data saved successfully!" : "Failed to save semester data.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> moduleList = FXCollections.observableArrayList(modules);
        combo_AddModule.setItems(moduleList);
        Btn_Add.setOnAction(event -> addModule());
        btn_back.setOnAction(event -> loadPrevious());
        btn_add.setOnAction(event -> addYear());

        for (int startYear = 2010; startYear <= 2030; startYear++) {
            combo_newyear.getItems().add(startYear + "/" + (startYear + 1));
        }

        ObservableList<String> semesterOptions = FXCollections.observableArrayList("S1", "S2");
        Newsemester.setItems(semesterOptions);
        btn_save.setOnAction(event -> saveSemesterData());
    }
}
