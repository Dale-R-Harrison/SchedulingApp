package scheduler.utils;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import scheduler.Appointment;
import scheduler.Customer;
import scheduler.User;
import scheduler.view.LogInController;

import java.lang.*;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Database {

    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String ipAddress = "//3.227.166.251/U07jVP";

    private static final String jdbcURL = protocol + vendorName + ipAddress;
    private static final String mySQLJDCBDriver = "com.mysql.cj.jdbc.Driver";
    private static Connection conn = null;

    private static final String username = "U07jVP";
    private static final String password = "53689048360";

    public static ObservableList<Appointment> createApptList(int userId) {
        ObservableList<Appointment> appList = FXCollections.observableArrayList();
        String query = "select * from appointment where userId = " + userId;
        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while(rs.next()) {
                appList.add(createAppt(userId));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return appList;
    }

    public static User createUser(String user) {
        String query = "select * from user where userName = \"" + user + "\"";
        User newUser = null;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if(rs.next()) {
                String name = rs.getString(2);
                String pass = rs.getString(3);
                int id = rs.getInt(1);

                newUser = new User(name, pass, id);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return newUser;
    }

    public static ZonedDateTime convertInstantZonedDateTime(Timestamp ts) {
        Instant instant = ts.toInstant();
        return instant.atZone(ZoneId.systemDefault());
    }

    public static Appointment createAppt(int userId) {
        String query = "select * from appointment where userId = " + userId;
        Appointment newAppt = null;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if(rs.next()) {
                int custId = rs.getInt(2);
                int apptId = rs.getInt(1);
                String title = rs.getString(4);
                String description = rs.getString(5);
                String location = rs.getString(6);
                String contact = rs.getString(7);
                String type = rs.getString(8);
                String url = rs.getString(9);
                ZonedDateTime start = convertInstantZonedDateTime(rs.getTimestamp(10));
                ZonedDateTime end = convertInstantZonedDateTime(rs.getTimestamp(11));

                newAppt = new Appointment(custId, apptId, title, description, location,
                                          contact, type, url, start, end);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return newAppt;
    }

    public static Customer createCustomer(int custId) {
        String query = "select * from customer where customerId = " + custId;
        Customer newCust = null;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if(rs.next()) {
                String name = rs.getString(2);
                int addressId = rs.getInt(3);
                int id = rs.getInt(1);

                newCust = new Customer(id, name, addressId);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return newCust;
    }

    public static int generateApptId() {
        int id = 1;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("select * from appointment")) {
            if(rs.last()) {
                id = rs.getRow() + 1;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return id;
    }

    public static void addOrUpdateAppt(Appointment appt) {
        int id = appt.getApptId();
        int custId = appt.getCustomerObject().getCustId();
        int userID = LogInController.currentUser.getUserId();
        String title = "\"" + appt.getTitle() + "\"" ;
        String description = "\"" + appt.getDescription() + "\"";
        String location = "\"" + appt.getLocation() + "\"";
        String contact = "\"" + appt.getContact() + "\"";
        String type = "\"" + appt.getType() + "\"";
        String url = "\"\"";
        Instant start = appt.getStart().toInstant();
        Instant end = appt.getEnd().toInstant();
        String name = LogInController.currentUser.getName();

        String query = "INSERT INTO appointment VALUES(" + id + ", " +custId + ", " + userID + ", " + title + ", " + description + ", " + location +
                       ", " + contact + ", " + type + ", " + url + ", " + start + ", " + start + ", " + end + ", NOW(), " + name + ", NOW(), " + name + ") ";

        String queryPt2 = "ON DUPLICATE KEY UPDATE customerId=" + custId + ", title=" + title + ", description=" + description + ", location=" + location +
                           ", contact=" + contact + ", type=" + type + ", start=" + start + ", end=" + end + ", lastUpdate=NOW(), lastUpdatedBy=" + name;

        String finalQuery = query + queryPt2;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement()) {
            Boolean updated = st.execute(finalQuery)

            if(updated) {
               System.out.println("Appointment added.");
            }
            else {
                System.out.println("No update.");
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection startConnection() {
        try {
            Class.forName(mySQLJDCBDriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Connection was successful.");
        }
        catch(ClassNotFoundException | SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
