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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import scheduler.Appointment;
import scheduler.Customer;
import scheduler.User;
import scheduler.utils.Database;
import scheduler.utils.OverlappingApptsException;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.util.ResourceBundle;

public class ApptAddController implements Initializable {
    @FXML
    final ToggleGroup type = new ToggleGroup();
    @FXML
    RadioButton faceToFace;
    @FXML
    RadioButton skype;
    @FXML
    RadioButton outOfOffice;

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> idColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    TextField location;
    @FXML
    TextField title;
    @FXML
    TextField description;

    @FXML
    private DatePicker datePicker;

    @FXML
    ComboBox<String> cbHours;
    @FXML
    ComboBox<String> cbMins;
    @FXML
    ComboBox<String> cbLength;

    ObservableList<Customer> customerList;
    ObservableList<String> hours = FXCollections.observableArrayList();
    ObservableList<String> minutes = FXCollections.observableArrayList();
    ObservableList<String> length = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        faceToFace.setToggleGroup(type);
        faceToFace.setSelected(true);
        skype.setToggleGroup(type);
        outOfOffice.setToggleGroup(type);

        customerList = Database.createCustList();
        //Business Hours are Mon-Fri from 6 AM to 5 PM. Forms are configured to only allow selection in these time frames
        hours.addAll("06", "07", "08", "09", "10", "11",
                "12", "13", "14", "15", "16");
        minutes.addAll("00", "15", "30", "45");
        length.addAll("15", "30", "45", "60");

        cbHours.setItems(hours);
        cbMins.setItems(minutes);
        cbLength.setItems(length);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("custId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerTable.setItems(customerList);

        /*this lambda is used by neccessity as the argument for setting the cell factory takes a lambda as an argument.
         * I am disabling days that are outside of business hours. By restricting times and day that can be chosen by form I can
         * ensure that an appointment is not scheduled outside business hours without having to validate input seperately.
         */
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            }
        });
        datePicker.setEditable(false);
    }

    private String checkType(){
        if(faceToFace.isSelected()) {
            return "face-to-face";
        }
        else if(skype.isSelected()) {
            return "skype";
        }
        else {
            return "out-of-office";
        }
    }

    private boolean isNotOverLapping(Appointment appt) {
        boolean flag = true;

        for (Appointment a : LogInController.currentUser.getAppointments()) {
            long startCheck = Duration.between(a.getStart(), appt.getStart()).toMinutes();
            System.out.println(startCheck);
            long endCheck = Duration.between(a.getStart(), appt.getEnd()).toMinutes();
            System.out.println(endCheck);
            long durationToCheck = Duration.between(a.getStart(), a.getEnd()).toMinutes();
            System.out.println(durationToCheck);
            long length = Duration.between(appt.getStart(), appt.getEnd()).toMinutes();

            if ((startCheck < durationToCheck && startCheck > (-length)) || (endCheck < durationToCheck && endCheck > 0))
                flag = false;
        }
        return flag;
    }

    public void saveBttnClicked(javafx.event.ActionEvent event) throws IOException{
        String type = checkType();
        String location = this.location.getText();
        String title = this.title.getText();
        String description = this.description.getText();

        Customer customer = customerTable.getSelectionModel().getSelectedItem();

        String hour = cbHours.getValue();
        String minute = cbMins.getValue();
        LocalDate date = datePicker.getValue();
        String length = cbLength.getValue();

        ZonedDateTime start = LocalDateTime.of(date.getYear(), date.getMonthValue(),
                date.getDayOfMonth(), Integer.parseInt(hour), Integer.parseInt(minute)).atZone(ZoneId.systemDefault());
        ZonedDateTime end = start.plusMinutes(Integer.parseInt(length));

        Appointment newAppt = new Appointment(customer, Database.generateId(CentralController.apptIdList), title, description, location, "123-456-7890",
                type, "", start, end);

        try {
            if (isNotOverLapping(newAppt)) {
                Database.addOrUpdateAppt(newAppt);
                LogInController.currentUser.getAppointments().add(newAppt);

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("Central.fxml"));
                Parent centralParent = loader.load();
                Scene centralScene = new Scene(centralParent);

                Stage newScreen = (Stage) ((Node) event.getSource()).getScene().getWindow();
                newScreen.setScene(centralScene);
                newScreen.show();
            }
            else
                throw new OverlappingApptsException("");
        }
        catch (OverlappingApptsException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Scheduling Error");
            alert.setContentText("The times chosen for this appointment overlap with another. Please choose a different time.");
            alert.showAndWait();
        }
    }

    public void backBttnPushed (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Central.fxml"));
        Parent centralParent = loader.load();
        Scene centralScene = new Scene(centralParent);

        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
        newScreen.setScene(centralScene);
        newScreen.show();
    }

    public void editBttnPushed(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("custUpdate.fxml"));
        Parent custUpdateParent = loader.load();
        Scene custUpdateScene = new Scene(custUpdateParent);

        CustUpdateController controller = loader.getController();
        Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();

        Customer selectedCust = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCust != null) {
            controller.custId = selectedCust.getCustId();
            controller.firstName.setText(selectedCust.getName().substring(0, selectedCust.getName().indexOf(' ')));
            controller.lastName.setText(selectedCust.getName().substring(selectedCust.getName().indexOf(' ') + 1));

            String query = "select * from address where addressId = " + selectedCust.getAddressId();
            try (Connection conn = Database.startConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(query)) {
                int cityId;

                if(rs.next()) {
                    controller.streetAddress.setText(rs.getString(2));
                    controller.number.setText(rs.getString(6));
                    controller.zipCode.setText(rs.getString(5));

                    cityId = rs.getInt(4);
                    ResultSet subRs = st.executeQuery("SELECT * FROM city WHERE cityId = " + cityId);

                    if(subRs.next()) {
                        controller.city.setValue(subRs.getString(2));
                    }
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }

            newScreen.setScene(custUpdateScene);
            newScreen.show();
        }
    }

    public void delBttnPushed(ActionEvent event) throws IOException {
        Customer selectedCust = customerTable.getSelectionModel().getSelectedItem();
        if (selectedCust != null) {
            Database.deleteCustomer(selectedCust.getCustId());

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Central.fxml"));
            Parent centralParent = loader.load();
            Scene centralScene = new Scene(centralParent);

            Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
            newScreen.setScene(centralScene);
            newScreen.show();
        }
    }
}
