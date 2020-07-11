package scheduler.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import scheduler.User;
import scheduler.utils.Database;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseUserController implements Initializable {
    @FXML
    TableView<User> userTable;
    @FXML
    TableColumn<User, String> nameColumn;

    ObservableList<User> userList = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle rb) {
        userList = Database.createUserList();
        userTable.setItems(userList);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    public void selectBttnPressed(ActionEvent event) throws IOException {
        User user = userTable.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("userSchedule.fxml"));
        Parent userScheduleParent = loader.load();
        Scene userScheduleScene = new Scene(userScheduleParent);

        if (user != null) {
            UserScheduleController controller = loader.getController();

            controller.dateColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
            controller.nameColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
            controller.typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            controller.locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
            controller.descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

            controller.appointmentsTable.setItems(Database.createApptList(user.getUserId()));

            Stage newScreen = (Stage) ((Node) event.getSource()).getScene().getWindow();
            newScreen.setScene(userScheduleScene);
            newScreen.show();
        }
    }

    public void backBttnPressed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("apptView.fxml"));
        Parent apptViewParent = loader.load();
        Scene apptViewScene = new Scene(apptViewParent);

        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(apptViewScene);
        newScreen.show();
    }
}
