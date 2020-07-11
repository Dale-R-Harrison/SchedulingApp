package scheduler.utils;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.Appointment;
import scheduler.Customer;
import scheduler.User;
import scheduler.view.CentralController;
import scheduler.view.LogInController;

import java.lang.*;
import java.sql.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                appList.add(createAppt(rs.getInt(1)));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return appList;
    }

    public static ObservableList<Customer> createCustList() {
        ObservableList<Customer> custList = FXCollections.observableArrayList();
        String query = "select * from customer";
        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while(rs.next()) {
                int custId = rs.getInt(1);
                custList.add(createCustomer(custId));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return custList;
    }

    public static ObservableList<User> createUserList() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        String query = "select * from user";
        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while(rs.next()) {
                String userName = rs.getString(2);
                userList.add(createUser(userName));
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return userList;
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

    public static ArrayList<Integer> createApptIdList() {
        String query = "SELECT * FROM appointment";
        ArrayList<Integer> apptList = new ArrayList<>();

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                apptList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return apptList;
    }

    public static ArrayList<Integer> createAddressIdList() {
        String query = "SELECT * FROM address";
        ArrayList<Integer> addressList = new ArrayList<>();

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                addressList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return addressList;
    }

    public static ArrayList<Integer> createCustomerIdList() {
        String query = "SELECT * FROM customer";
        ArrayList<Integer> customerIdList = new ArrayList<>();

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                customerIdList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return customerIdList;
    }

    public static ZonedDateTime convertInstantZonedDateTime(Timestamp ts) {
        Instant instant = ts.toInstant();
        return instant.atZone(ZoneId.systemDefault());
    }

    public static Appointment createAppt(int apptId) {
        String query = "select * from appointment where appointmentId = " + apptId;
        Appointment newAppt = null;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                int custId = rs.getInt(2);
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

    public static int generateId(ArrayList<Integer> idList) {
        int id = 1;

        while(idList.contains(id)) {
            id += 1;
        }

        idList.add(id);
        return id;
    }

    public static void addAddress(int addressId, String address, int cityId, String zipCode, String number) {
        String name = LogInController.currentUser.getName();
        String query = "INSERT INTO address VALUES (" +addressId + ", '" + address + "', '', " + cityId + ", '" + zipCode + "', '" + number + "', NOW(), '" + name + "', NOW(), '" + name + "')";

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement()) {
            st.execute(query);
            CentralController.addressIdList.add(addressId);
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static void addOrUpdateCustomer(Customer customer) {
        int id = customer.getCustId();
        int addressId = customer.getCustId();

        String custName = "'" + customer.getName() + "'" ;
        String name = "'" + LogInController.currentUser.getName() + "'" ;

        String query = "INSERT INTO customer VALUES(" + id + ", " + custName + ", " + addressId + ", 0, NOW(), " + name + ", NOW(), " + name + ") ";
        String queryPt2 = "ON DUPLICATE KEY UPDATE customerName=" + custName + ", addressId=" + addressId + ", lastUpdate=NOW(), lastUpdateBy=" + name;

        String finalQuery = query + queryPt2;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement()) {
            st.execute(finalQuery);

            System.out.println("Customer added.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //creates string to make a query based on Appoinment object parameter. Submits query to update database and prints to screen whether it was successful.
    public static void addOrUpdateAppt(Appointment appt) {
        int id = appt.getApptId();
        int custId = appt.getCustomerObject().getCustId();
        int userID = LogInController.currentUser.getUserId();
        String title = "'" + appt.getTitle() + "'" ;
        String description = "'" + appt.getDescription() + "'";
        String location = "'" + appt.getLocation() + "'";
        String contact = "'" + appt.getContact() + "'";
        String type = "'" + appt.getType() + "'";
        String url = "''";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"));
        Instant startTime = appt.getStart().toInstant();
        Instant endTime = appt.getEnd().toInstant();
        String start = dateTimeFormatter.format(startTime);
        String end = dateTimeFormatter.format(endTime);
        String name = LogInController.currentUser.getName();

        String query = "INSERT INTO appointment VALUES(" + id + ", " +custId + ", " + userID + ", " + title + ", " + description + ", " + location +
                       ", " + contact + ", " + type + ", " + url + ", \"" + start + "\", \"" + end + "\", NOW(), '" + name + "', NOW(), '" + name + "') ";

        String queryPt2 = "ON DUPLICATE KEY UPDATE customerId=" + custId + ", title=" + title + ", description=" + description + ", location=" + location +
                           ", contact=" + contact + ", type=" + type + ", start=\"" + start + "\", end=\"" + end + "\", lastUpdate=NOW(), lastUpdateBy='" + name + "'";

        String finalQuery = query + queryPt2;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement()) {
            st.execute(finalQuery);

            System.out.println("Appointment added.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteCustomer(int custId) {
        String query = "DELETE FROM customer WHERE customerId = " + custId;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement()) {
            st.execute(query);

            System.out.println("Customer deleted.");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteAppt(int apptId) {
        String query = "DELETE FROM appointment WHERE appointmentId = " + apptId;

        try (Connection conn = Database.startConnection();
             Statement st = conn.createStatement()) {
            st.execute(query);

            System.out.println("Appointment deleted.");
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
