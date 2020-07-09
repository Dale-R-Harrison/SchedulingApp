package scheduler;

public class Customer {
    private int custId;
    private String name;
    private int addressId;

    public int getCustId() { return this.custId; }
    public String getName() { return name; }
    public int getAddressId() { return addressId; }

    public Customer(int custId, String name, int addressId) {
        this.custId = custId;
        this.name = name;
        this.addressId = addressId;
    }
}
