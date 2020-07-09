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
import java.util.ResourceBundle;

public class CentralController implements Initializable {
    @FXML
    private Label welcome;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        welcome.setText("Welcome " + LogInController.currentUser.getName() + "!");
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
}
