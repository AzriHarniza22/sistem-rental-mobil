package interfaces;

import model.CustomerDetails;
import java.util.List;

public interface ICustomerMgt {
    String registerCustomer(String name, String email);
    CustomerDetails getCustomer(String customerId);
    boolean updateCustomer(String customerId, CustomerDetails details);
    List<CustomerDetails> listCustomers();
}