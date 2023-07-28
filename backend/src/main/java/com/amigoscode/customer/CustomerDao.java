package com.amigoscode.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Long id);

    void insertCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    boolean existsCustomerWithId(Long id);

    void deleteCustomer(Long customerId);

    void updateCustomer(Long customerId, Customer customer);
}
