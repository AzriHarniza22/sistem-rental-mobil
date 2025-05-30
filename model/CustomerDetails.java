package model;

public class CustomerDetails {
    public String customerId;
    public String name;
    public String email;

    public CustomerDetails(String customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }

    public CustomerDetails(String customerId, String name, String email, String phone, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }
}