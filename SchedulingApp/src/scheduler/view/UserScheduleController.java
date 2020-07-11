package scheduler.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.Appointment;

import java.io.IOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

public class UserScheduleController implements Initializable {
    @FXML
    TableView<Appointment> appointmentsTable;
    @FXML
    TableColumn<Appointment, ZonedDateTime> dateColumn;
    @FXML
    TableColumn<Appointment, String> nameColumn;
    @FXML
    TableColumn<Appointment, String> typeColumn;
    @FXML
    TableColumn<Appointment, String> locationColumn;
    @FXML
    TableColumn<Appointment, String> descriptionColumn;

    public void initialize(URL url, ResourceBundle rb) {
    }

    public void backBttnPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chooseUser.fxml"));
        Parent chooseUserParent = loader.load();
        Scene chooseUserScene = new Scene(chooseUserParent);

        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(chooseUserScene);
        newScreen.show();
    }
}
