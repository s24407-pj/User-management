package com.amigoscode.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void updateCustomer() {
        //Given
        long id = 1;
        Customer customer = new Customer(
                "Michal",
                "mail@mail.com",
                20
        );
        //When
        underTest.updateCustomer(id, customer);
        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void existsCustomerWithId() {
        //Given
        long id = 1;
        //When
        underTest.existsCustomerWithId(id);
        //Then
        verify(customerRepository).existsById(id);
    }

    @Test
    void existsCustomerWithEmail() {
        //Given
        String email = "mail@mail.com";
        //When
        underTest.existsCustomerWithEmail(email);
        //Then
        verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomer() {
        //Given
        long id = 1;
        //When
        underTest.deleteCustomer(id);
        //Then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Customer customer = new Customer(
                "Michal",
                "mail@mail.com",
                20
        );

        //When
        underTest.insertCustomer(customer);

        //Then
        verify(customerRepository).save(customer);
    }

    @Test
    void selectAllCustomers() {
        //When
        underTest.selectAllCustomers();

        //Then
        verify(customerRepository)
                .findAll();
    }

    @Test
    void selectCustomerById() {
        //Given
        long id = 1;
        //When
        underTest.selectCustomerById(id);
        //Then
        verify(customerRepository).findById(id);
    }
}