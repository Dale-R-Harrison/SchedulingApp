package scheduler.view;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.Appointment;
import scheduler.utils.Database;


import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ApptViewController implements Initializable {
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, ZonedDateTime> dateColumn;
    @FXML
    private TableColumn<Appointment, String> nameColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableColumn<Appointment, String> locationColumn;
    @FXML
    private TableColumn<Appointment, String> descriptionColumn;

    @FXML
    final ToggleGroup timePeriod = new ToggleGroup();
    @FXML
    RadioButton all;
    @FXML
    RadioButton monthly;
    @FXML
    RadioButton weekly;

    public void backBttnPushed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Central.fxml"));
        Parent centralParent = loader.load();
        Scene centralScene = new Scene(centralParent);

        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(centralScene);
        newScreen.show();
    }

    public void updateBttnPushed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("apptUpdate.fxml"));
        Parent apptUpdateParent = loader.load();
        Scene apptUpdateScene = new Scene(apptUpdateParent);

        Appointment selectedAppt = appointmentsTable.getSelectionModel().getSelectedItem();

        ApptUpdateController controller = loader.getController();
        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();

        if (selectedAppt != null) {
            controller.appointmentToChange = selectedAppt;
            controller.description.setText(selectedAppt.getDescription());
            controller.title.setText(selectedAppt.getTitle());
            controller.location.setText(selectedAppt.getLocation());
            if (selectedAppt.getType().equals("skype"))
                controller.skype.setSelected(true);
            else if (selectedAppt.getType().equals("out-of-office"))
                controller.outOfOffice.setSelected(true);
            controller.cbHours.setValue(String.valueOf(selectedAppt.getStart().getHour()));
            controller.cbMins.setValue(String.valueOf(selectedAppt.getStart().getMinute()));
            controller.cbLength.setValue(String.valueOf(Duration.between(selectedAppt.getStart(), selectedAppt.getEnd()).toMinutes()));

            newScreen.setScene(apptUpdateScene);
            newScreen.show();
        }
    }

    public void deleteBttnPushed(ActionEvent event) throws IOException {
        Appointment selectedAppt = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selectedAppt != null) {
            Database.deleteAppt(selectedAppt.getApptId());

            LogInController.currentUser.setAppointments(Database.createApptList(LogInController.currentUser.getUserId()));

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Central.fxml"));
            Parent centralParent = loader.load();
            Scene centralScene = new Scene(centralParent);

            Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
            newScreen.setScene(centralScene);
            newScreen.show();
        }
    }

    public void typesButtonPushed() {
        int num = 0;
        ArrayList<String> typeList = new ArrayList<>();

        ObservableList<Appointment> appts = LogInController.currentUser.getAppointments().filtered(a -> a.getStart().getMonth() == ZonedDateTime.now().getMonth());
        for (Appointment a : appts) {
            if (!typeList.contains(a.getType())) {
                typeList.add(a.getType());
                num += 1;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setContentText("There are " + num + " appointment types this month.");
        alert.showAndWait();
    }

    public void numberOfCustBttnPushed() {
        int num = 0;
        ArrayList<Integer> typeList = new ArrayList<>();

        ObservableList<Appointment> appts = LogInController.currentUser.getAppointments().filtered(a -> a.getStart().getMonth() == ZonedDateTime.now().getMonth());
        for (Appointment a : appts) {
            if (!typeList.contains(a.getCustomerObject().getCustId())) {
                typeList.add(a.getCustomerObject().getCustId());
                num += 1;
            }
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Report");
        alert.setContentText("There are " + num + " different customer scheduled this month.");
        alert.showAndWait();
    }

    public void scheduleBttnPushed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("chooseUser.fxml"));
        Parent chooseUserParent = loader.load();
        Scene chooseUserScene = new Scene(chooseUserParent);

        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(chooseUserScene);
        newScreen.show();
    }

    public void initialize(URL url, ResourceBundle rb) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        appointmentsTable.setItems(LogInController.currentUser.getAppointments());

        all.setToggleGroup(timePeriod);
        monthly.setToggleGroup(timePeriod);
        weekly.setToggleGroup(timePeriod);
        all.setSelected(true);
    }

    public void monthlySelected() {
        /*lambda allows efficient filtering of list to only include appointments in current month. What would otherwise take
         * an enhanced for loop and multiple lines of code can be done in one line elegantly with a stream and a lambda.
         */
        ObservableList<Appointment> monthlyAppt = LogInController.currentUser.getAppointments().filtered(a -> isMonthly(a.getStart()));

        appointmentsTable.setItems(monthlyAppt);
    }

    public void weeklySelected() {
        ObservableList<Appointment> weeklyAppt = LogInController.currentUser.getAppointments().filtered(a -> isWeekly(a.getStart()));

        appointmentsTable.setItems(weeklyAppt);
    }

    public void allSelected() {
        appointmentsTable.setItems(LogInController.currentUser.getAppointments());
    }

    private boolean isMonthly(ZonedDateTime time) {
        ZonedDateTime now = ZonedDateTime.now();
        return now.getMonthValue() == time.getMonthValue();
    }

    private boolean isWeekly(ZonedDateTime time) {
        ZonedDateTime now = ZonedDateTime.now();
        Duration d = Duration.between(now, time);
        return d.toDays() < 7;
    }
}
