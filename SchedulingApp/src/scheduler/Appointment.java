package scheduler;

import scheduler.utils.Database;

import java.time.ZonedDateTime;

public class Appointment {
    private Customer customer;

    private int apptId;

    private String title;
    private String description;
    private String location;
    private String contact;
    private String type;
    private String url;

    private ZonedDateTime start;
    private ZonedDateTime end;

    public String getCustomer() { return customer.getName(); }
    public Customer getCustomerObject() { return this.customer; }
    public int getApptId() { return apptId; }
    public String getContact() { return contact; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getUrl() { return url; }
    public ZonedDateTime getEnd() { return end; }
    public ZonedDateTime getStart() { return start; }

    public Appointment(int custId, int apptId, String title, String description, String location, String contact,
                       String type, String url, ZonedDateTime start, ZonedDateTime end) {
        customer = Database.createCustomer(custId);
        this.apptId = apptId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.start = start;
        this.end = end;
    }
}
