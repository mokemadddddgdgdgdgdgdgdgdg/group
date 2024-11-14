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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

public class AddModuleController implements Initializable {

    @FXML
    private Button Btn_Add;

    @FXML
    private Button btn_Back;

    @FXML
    private ComboBox<String> combo_AddModule;

    @FXML
    private TextField txt_Credits;

    // List of modules to populate the ComboBox
    private final String[] modules = {
            "Java Programming II",
            "Discrete Maths",
            "Internet Technology",
            "Computer Support",
            "Computer Architecture",
            "E-Commerce Systems",
            "Database Management System",
            "C++ 10"
    };


    private Connection connect;

    @FXML
    private void loadPrevious() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("admin.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Admin Portal");
            stage.setScene(new Scene(root));
            stage.show();

            btn_Back.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addModule() {
        String selectedModule = combo_AddModule.getSelectionModel().getSelectedItem();
        String creditsStr = txt_Credits.getText();

        if (selectedModule == null || creditsStr.isEmpty()) {
            System.out.println("Please select a module and enter credits.");
            return;
        }

        int credits;
        try {
            credits = Integer.parseInt(creditsStr);
        } catch (NumberFormatException e) {
            System.out.println("Invalid credits value. Please enter a number.");
            return;
        }


        String insertModuleSQL = "INSERT INTO modules (module_name, credits) VALUES (?, ?)";

        try {
            connect = Connect.connectDb();
            PreparedStatement prepare = connect.prepareStatement(insertModuleSQL);
            prepare.setString(1, selectedModule);
            prepare.setInt(2, credits);
            int result = prepare.executeUpdate();

            if (result > 0) {
                System.out.println("Module added successfully: " + selectedModule);

                combo_AddModule.getSelectionModel().clearSelection();

            } else {
                System.out.println("Failed to add the module.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> moduleList = FXCollections.observableArrayList(modules);
        combo_AddModule.setItems(moduleList);

        btn_Back.setOnAction(event -> loadPrevious());
        Btn_Add.setOnAction(event -> addModule());
    }
}
