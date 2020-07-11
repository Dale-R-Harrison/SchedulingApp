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
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.Customer;
import scheduler.utils.Database;
import scheduler.utils.MissingFieldException;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CustAddController implements Initializable {
    @FXML
    TextField firstName;
    @FXML
    TextField lastName;
    @FXML
    TextField streetAddress;
    @FXML
    TextField number;
    @FXML
    TextField zipCode;

    @FXML
    ComboBox<String>  city;

    ObservableList<String> cities = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cities.addAll("london", "pheonix", "new york");
        city.setItems(cities);
    }

    private int parseCityId(String city) {
        if(city.equals("london"))
            return 3;
        else if(city.equals("pheonix"))
            return 2;
        else
            return 1;
    }

    public void saveBttnPushed(ActionEvent event) throws IOException {
        try {
            if (validateInput()) {
                int addressId = Database.generateId(CentralController.addressIdList);
                String address = streetAddress.getText();
                int cityId = parseCityId(city.getValue());
                String zip = zipCode.getText();
                String number = this.number.getText();

                Database.addAddress(addressId, address, cityId, zip, number);

                int customerId = Database.generateId(CentralController.customerIdList);
                String name = firstName.getText() + " " + lastName.getText();

                Customer customer = new Customer(customerId, name, addressId);
                Database.addOrUpdateCustomer(customer);

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("Central.fxml"));
                Parent centralParent = loader.load();
                Scene centralScene = new Scene(centralParent);

                Stage newScreen = (Stage) ((Node) event.getSource()).getScene().getWindow();
                newScreen.setScene(centralScene);
                newScreen.show();
            }
            else
                throw new MissingFieldException("");
        }
        catch (MissingFieldException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setContentText("All Fields Must Be Filled Out");
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

    private boolean validateInput(){
        boolean validated = true;

        if (streetAddress.getText().isEmpty())
            validated = false;
        if(firstName.getText().isEmpty())
            validated = false;
        if(lastName.getText().isEmpty())
            validated = false;
        if(number.getText().isEmpty())
            validated = false;
        if(zipCode.getText().isEmpty())
            validated = false;
        if(city.getValue() == null)
            validated = false;

        return validated;
    }
}
