package scheduler.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import scheduler.User;
import scheduler.utils.Database;
import scheduler.utils.IncorrectUserOrPasswordException;


public class LogInController implements Initializable {

    @FXML
    private Label welcome;
    @FXML
    private Label user;
    @FXML
    private Label password;

    @FXML
    private PasswordField userField;
    @FXML
    private PasswordField passwordField;

    public static User currentUser;

    /* checks a result set for username + password combo. ResultSet should only be result of
     * query on user table with all columns. This is so same rs can be used for creating User object
     * and for validating login.
     */
    private boolean isValidLogin(String userName, String pass, ResultSet rs) {
        boolean isValid = false;

        try {
            while (rs.next()) {
                String enteredUser = rs.getString(2);
                String enteredPass = rs.getString(3);

                if (userName.equals(enteredUser) && pass.equals(enteredPass)) {
                    isValid = true;
                }
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }

        return isValid;
    }

    //validates login and opens program proper on validation. Else gives error
    public void logInButtonPressed(ActionEvent event) throws IncorrectUserOrPasswordException {
        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("select * from user")) {

            String name = userField.getText();
            String pass = passwordField.getText();

            if(isValidLogin(name, pass, rs)) {
                currentUser = Database.createUser(name);

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("Central.fxml"));
                Parent centralParent = loader.load();
                Scene centralScene = new Scene(centralParent);

                Stage newScreen = (Stage) ((Node)event.getSource()).getScene().getWindow();
                newScreen.setScene(centralScene);
                newScreen.show();
            }
            else { throw new IncorrectUserOrPasswordException(""); }
        }
        catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
        catch (IncorrectUserOrPasswordException e) {
            System.out.print("Incorrect Username or Password");
        }

        return;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Locale locale = Locale.getDefault();
        rb = ResourceBundle.getBundle("Login", locale);
        welcome.setText(rb.getString("welcomeMess"));
        user.setText(rb.getString("username"));
        password.setText(rb.getString("password"));
    }
}

