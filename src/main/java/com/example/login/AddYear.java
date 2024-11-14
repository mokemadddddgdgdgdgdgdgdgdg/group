package com.example.login;

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
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddYear implements Initializable {

    @FXML
    private ComboBox<String> combo_newyear;

    @FXML
    private Button btn_back;

    @FXML
    private Button btn_add;

    @FXML
    private Label year;

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
            System.out.println("Please select a year to add.");
        }
    }

    private void saveYearToDatabase(String year) {
        String query = "INSERT INTO years (year) VALUES (?)";

        try (Connection connection = Connect.connectDb();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, year);
            preparedStatement.executeUpdate();
            //System.out.println("Year added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btn_back.setOnAction(event -> loadPrevious());
        btn_add.setOnAction(event -> addYear());


        for (int startYear = 2010; startYear <= 2030; startYear++) {
            int endYear = startYear + 1;
            combo_newyear.getItems().add(startYear + "/" + endYear);
        }
    }
}
