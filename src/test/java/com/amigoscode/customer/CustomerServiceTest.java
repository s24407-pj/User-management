package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerDao customerDaoMock;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDaoMock);
    }


    @Test
    void getAllCustomers() {
        //When
        underTest.getAllCustomers();
        //Then
        verify(customerDaoMock).selectAllCustomers();
    }

    @Test
    void getCustomer() {
        //Given
        long id = 1;
        Customer customer = new Customer(
                id, "Alex", "alex@mail.com", 15
        );
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));
        //When
        Customer actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        long id = 1;
        Customer customer = new Customer(
                id, "Alex", "alex@mail.com", 15
        );
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.empty());
        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "asdasd@dsad";

        when(customerDaoMock.existsCustomerWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, 15);

        //When
        underTest.addCustomer(request);

        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDaoMock).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        //Given
        String email = "asdasd@dsad";

        when(customerDaoMock.existsCustomerWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, 15);

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email already exists");

        //Then
        verify(customerDaoMock, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        //Given
        long id = 1;
        when(customerDaoMock.existsCustomerWithId(id)).thenReturn(true);
        //When
        underTest.deleteCustomerById(id);
        //Then
        verify(customerDaoMock).deleteCustomer(id);
    }

    @Test
    void willThrowWhenDeletingCustomerNotExists() {
        //Given
        long id = 1;
        when(customerDaoMock.existsCustomerWithId(id)).thenReturn(false);
        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("customer with id [%s] not found".formatted(id));
        //Then
        verify(customerDaoMock,never()).deleteCustomer(any());
    }

    @Test
    void updateCustomer() {
        //Given
        long id =1;
        Customer customer = new Customer("Alex","alex@mail.com",20);
        when(customerDaoMock.existsCustomerWithId(id)).thenReturn(true);
        //When
        underTest.updateCustomer(id,customer);
        //Then
        verify(customerDaoMock).updateCustomer(id,customer);
    }

    @Test
    void willThrowWhenUpdatingCustomerNotExists() {
        //Given
        long id = 1;
        when(customerDaoMock.existsCustomerWithId(id)).thenReturn(false);
        Customer customer = new Customer("Alex","alex@mail.com",20);
        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id,customer))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer with id [%s] not found".formatted(id));

        //Then
        verify(customerDaoMock,never()).updateCustomer(id,customer);
    }
}