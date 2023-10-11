package com.amigoscode.customer;

import com.amigoscode.exception.DuplicateResourceException;
import com.amigoscode.exception.RequestValidationException;
import com.amigoscode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerDao customerDaoMock;
    @Mock
    private PasswordEncoder passwordEncoder;
    private CustomerService underTest;
    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDaoMock, passwordEncoder, customerDTOMapper);
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
                id, "Alex", "alex@mail.com", "password", Gender.FEMALE, 15
        );
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerDTO expected = customerDTOMapper.apply(customer);
        //When
        CustomerDTO actual = underTest.getCustomer(id);
        //Then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        long id = 1;

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
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email, "password", 15, Gender.MALE);

        String passwordHash = "srfdghedstg3425!";

        when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);
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
        CustomerRegistrationRequest request = new CustomerRegistrationRequest("Alex", email,"password", 15,Gender.MALE);

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
        verify(customerDaoMock, never()).deleteCustomer(any());
    }

    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password", Gender.MALE, 19);
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Alexandro", newEmail, 23);

        when(customerDaoMock.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDaoMock).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password", Gender.MALE, 19);
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Alexandro", null, null);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDaoMock).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password", Gender.MALE, 19);
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDaoMock.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDaoMock).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password", Gender.MALE, 19);
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, 22);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDaoMock).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password", Gender.MALE, 19);
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@amigoscode.com";

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDaoMock.existsCustomerWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest)).isInstanceOf(DuplicateResourceException.class).hasMessage("email already taken");

        // Then
        verify(customerDaoMock, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        long id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password", Gender.MALE, 19);
        when(customerDaoMock.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest)).isInstanceOf(RequestValidationException.class).hasMessage("no data changes found");

        // Then
        verify(customerDaoMock, never()).updateCustomer(any());
    }
}