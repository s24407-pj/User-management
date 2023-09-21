package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao, PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Long id) {
        return customerDao.selectCustomerById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));

    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        if (customerDao.existsCustomerWithEmail(customerRegistrationRequest.email())) {
            throw new DuplicateResourceException("Customer with email already exists");
        }
        customerDao.insertCustomer(
                new Customer(
                        customerRegistrationRequest.name(),
                        customerRegistrationRequest.email(),
                        passwordEncoder.encode(customerRegistrationRequest.password()),
                        customerRegistrationRequest.age(),
                        customerRegistrationRequest.gender()
                )
        );
    }

    public void deleteCustomerById(Long customerId) {
        if (!customerDao.existsCustomerWithId(customerId)) {
            throw new ResourceNotFoundException("customer with id [%s] not found".formatted(customerId));
        }
        customerDao.deleteCustomer(customerId);
    }

    public void updateCustomer(Long customerId, Customer customer) {
        if (customerDao.existsCustomerWithId(customerId)) {
            customerDao.updateCustomer(customerId, customer);
        } else {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId));
        }
    }
}
