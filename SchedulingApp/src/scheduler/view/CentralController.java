package scheduler.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import scheduler.Appointment;
import scheduler.User;
import scheduler.utils.Database;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CentralController implements Initializable {
    @FXML
    private Label welcome;

    public static ArrayList<Integer> apptIdList;
    public static ArrayList<Integer> addressIdList;
    public static ArrayList<Integer> customerIdList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        welcome.setText("Welcome " + LogInController.currentUser.getName() + "!");

        apptIdList = Database.createApptIdList();
        addressIdList = Database.createAddressIdList();
        customerIdList = Database.createCustomerIdList();
    }

    public void viewApptButtonPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("apptView.fxml"));
        Parent apptViewParent = loader.load();
        Scene apptViewScene = new Scene(apptViewParent);

        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(apptViewScene);
        newScreen.show();
    }

    public void addApptButtonPressed(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("apptAdd.fxml"));
        Parent addApptParent = loader.load();
        Scene addApptScene = new Scene(addApptParent);


        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(addApptScene);
        newScreen.show();
    }

    public void addCustButtonPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("custAdd.fxml"));
        Parent custAddParent = loader.load();
        Scene custAddScene = new Scene(custAddParent);

        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(custAddScene);
        newScreen.show();
    }
}
