package com.amigoscode.journey;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    private static final String CUSTOMER_URI = "/api/v1/customers";

    @Test
    void canRegisterACustomer() {
        //create registration request
        Faker faker = new Faker();
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                faker.name().fullName(),
                faker.pokemon().name() + UUID.randomUUID() + "@mail.com",
                faker.number().numberBetween(18, 99)
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //make sure that customer is present
        Customer expected = new Customer(
                request.name(),
                request.email(),
                request.age()
        );
        assertThat(allCustomers)
                .isNotEmpty()
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expected);

        long id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(expected.getEmail()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expected.setId(id);

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expected);
    }

    @Test
    void canDeleteCustomer() {
        //create registration request
        Faker faker = new Faker();
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                faker.name().fullName(),
                faker.pokemon().name() + UUID.randomUUID() + "@mail.com",
                faker.number().numberBetween(18, 99)
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();
        
        assertThat(allCustomers).isNotEmpty();

        long id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(request.email()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        //create registration request
        Faker faker = new Faker();
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                faker.name().fullName(),
                faker.pokemon().name() + UUID.randomUUID() + "@mail.com",
                faker.number().numberBetween(18, 99)
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();


        long id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(request.email()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //update customer
        Customer update = new Customer(
                faker.name().fullName(),
                request.email(),
                request.age()+1
        );
        webTestClient.put()
                .uri(CUSTOMER_URI+ "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(update), Customer.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        //get customer by id
        Customer updated = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        update.setId(id);

        assertThat(updated).isEqualTo(update);
    }
}
