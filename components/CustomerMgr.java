package components;

import interfaces.ICustomerMgt;
import model.CustomerDetails;
import java.util.*;

public class CustomerMgr implements ICustomerMgt {
    private Map<String, CustomerDetails> customers = new HashMap<>();

    @Override
    public String registerCustomer(String name, String email) {
        // Cek apakah customer dengan email yang sama sudah ada
        for (CustomerDetails customer : customers.values()) {
            if (customer.email.equals(email)) {
                return customer.customerId; // Return ID yang sudah ada
            }
        }
        
        // Jika belum ada, buat customer baru
        String id = "CUST" + new Random().nextInt(10000);
        CustomerDetails customer = new CustomerDetails(id, name, email);
        customers.put(id, customer);
        return id;
    }

    @Override
    public CustomerDetails getCustomer(String customerId) {
        return customers.get(customerId);
    }

    @Override
    public boolean updateCustomer(String customerId, CustomerDetails details) {
        if (customers.containsKey(customerId)) {
            customers.put(customerId, details);
            return true;
        }
        return false;
    }

    @Override
    public List<CustomerDetails> listCustomers() {
        return new ArrayList<>(customers.values());
    }
}