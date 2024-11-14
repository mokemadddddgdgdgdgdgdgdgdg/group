// ViewAssignedController.java
package com.example.login;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ViewAssignedController implements Initializable {

    @FXML
    private TableView<Assigned> assignedTable;

    @FXML
    private TableColumn<Assigned, String> colLecturer;

    @FXML
    private TableColumn<Assigned, String> colModule;

    @FXML
    private TableColumn<Assigned, String> colRole;

    @FXML
    private TableColumn<Assigned, String> colClass;

    @FXML
    private Button btn_back;

    private Connection connect;

    private ObservableList<Assigned> assignedList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colLecturer.setCellValueFactory(new PropertyValueFactory<>("lecturer"));
        colModule.setCellValueFactory(new PropertyValueFactory<>("module"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colClass.setCellValueFactory(new PropertyValueFactory<>("className")); // Updated

        loadAssignedData();

        btn_back.setOnAction(event -> loadPrevious());
    }

    private void loadAssignedData() {
        String query = "SELECT lecturer, module, role, `class` FROM assigned";  // Added backticks around `class`

        try {
            connect = Connect.connectDb();  // Make sure this function establishes a proper connection
            PreparedStatement statement = connect.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Querying assigned data...");
            while (resultSet.next()) {
                String lecturer = resultSet.getString("lecturer");
                String module = resultSet.getString("module");
                String role = resultSet.getString("role");
                String className = resultSet.getString("class");  // Use "class" from database

                System.out.println("Row retrieved: " + lecturer + ", " + module + ", " + role + ", " + className);
                assignedList.add(new Assigned(lecturer, module, role, className));
            }

            assignedTable.setItems(assignedList);

            resultSet.close();
            statement.close();
            connect.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
}
