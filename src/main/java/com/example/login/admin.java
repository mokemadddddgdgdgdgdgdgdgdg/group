    package com.example.login;

    import javafx.fxml.FXML;
    import javafx.fxml.FXMLLoader;
    import javafx.fxml.Initializable;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Button;
    import javafx.stage.Stage;

    import java.io.IOException;
    import java.net.URL;
    import java.util.ResourceBundle;

    public class admin implements Initializable {
        @FXML
        public Button profilebttn;
        @FXML
        private Button btn_AddLecturer;

        @FXML
        private Button btn_YearSemesterModule;

        @FXML
        private Button btn_AssignLec;

        @FXML
        private Button btn_ViewAssigned;

        @FXML
        private Button btn_ViewProfile;

        @FXML
        private Button btnlogout;

        private String loggedInUsername;
        private String loggedInPassword;

        public void setLoggedInCredentials(String username, String password) {
            this.loggedInUsername = username;
            this.loggedInPassword = password;
        }



       /* @FXML
        private void loadViewProfileForm() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminProfile.fxml"));
                Parent root = loader.load();


                AdminProfileController profileController = loader.getController();

                profileController.setAdminCredentials(loggedInUsername, loggedInPassword);
                profileController.loadProfile();

                Stage stage = new Stage();
                stage.setTitle("View Profile");
                stage.setScene(new Scene(root));
                stage.show();

                btn_ViewProfile.getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
*/
        @FXML
        private void loadAddLecturerForm() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddLecturer.fxml"));
                Parent root = loader.load();


                Stage stage = new Stage();
                stage.setTitle("Add Lecturer");
                stage.setScene(new Scene(root));
                stage.show();

                btn_AddLecturer.getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void loadNewYearForm() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("YearSemesterModule.fxml"));
                Parent root = loader.load();


                Stage stage = new Stage();
                stage.setTitle("Add Year Semester and Module");
                stage.setScene(new Scene(root));
                stage.show();

                btn_YearSemesterModule.getScene().getWindow().hide();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        public void loadAddModuleForm(){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddModule.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Add New Module");
                stage.setScene(new Scene(root));
                stage.show();

                btn_AddLecturer.getScene().getWindow().hide();

            } catch (IOException e){
                e.printStackTrace();
            }
        }
        @FXML
        private void loadAssignLecturerForm() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AssignLecturer.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Assign Lecturer");
                stage.setScene(new Scene(root));
                stage.show();

                btn_AssignLec.getScene().getWindow().hide();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void loadViewAssignedForm() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AssignedLecturer.fxml"));
                Parent root = loader.load();


                Stage stage = new Stage();
                stage.setTitle("Assigned Lecturer");
                stage.setScene(new Scene(root));
                stage.show();

                btn_ViewAssigned.getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @FXML
        private void loadPreviousForm() {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                Parent root = loader.load();


                Stage stage = new Stage();
                stage.setTitle("login portal");
                stage.setScene(new Scene(root));
                stage.show();

                btnlogout.getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void initialize(URL location, ResourceBundle resources) {

            //btn_ViewProfile.setOnAction(actionEvent -> loadViewProfileForm());
            btn_AddLecturer.setOnAction(event -> loadAddLecturerForm());
            btn_YearSemesterModule.setOnAction(actionEvent -> loadNewYearForm());
            btn_AssignLec.setOnAction(event -> loadAssignLecturerForm());
            btn_ViewAssigned.setOnAction(event -> loadViewAssignedForm());
            btnlogout.setOnAction(event -> loadPreviousForm());

        }
    }
