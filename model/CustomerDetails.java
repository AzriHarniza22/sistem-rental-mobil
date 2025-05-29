package model;

public class CustomerDetails {
    public String customerId;
    public String name;
    public String email;
    public String phone;
    public String address;

    public CustomerDetails(String customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }

    public CustomerDetails(String customerId, String name, String email, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}