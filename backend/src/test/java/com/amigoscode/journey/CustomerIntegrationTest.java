package com.amigoscode.journey;

import com.amigoscode.customer.Customer;
import com.amigoscode.customer.CustomerDTO;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.amigoscode.customer.Gender;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.*;

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
                "password",
                faker.number().numberBetween(18, 99),
                Gender.MALE
        );
        //send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);


        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        long id = allCustomers.stream()
                .filter(customer -> customer.email().equals(request.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();

        //make sure that customer is present
        CustomerDTO expected = new CustomerDTO(
                id,
                request.name(),
                request.email(),
                request.gender(),
                request.age(),
                List.of("ROLE_USER"),
                request.email()
        );
        assertThat(allCustomers).contains(expected);

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<CustomerDTO>() {
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
                "password",
                faker.number().numberBetween(18, 99),
                Gender.MALE
        );

        CustomerRegistrationRequest request2 = new CustomerRegistrationRequest(
                faker.name().fullName(),
                faker.pokemon().name() + UUID.randomUUID() + "2@mail.com",
                "password",
                faker.number().numberBetween(18, 99),
                Gender.MALE
        );
        //send a post request
        webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        //send a post request to create customer 2
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request2), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);


        //get all customers
        List<CustomerDTO> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<CustomerDTO>() {
                })
                .returnResult()
                .getResponseBody();

        assertThat(allCustomers).isNotEmpty();

        long id = allCustomers.stream()
                .filter(customer -> customer.email().equals(request.email()))
                .map(CustomerDTO::id)
                .findFirst()
                .orElseThrow();
        



        webTestClient.delete()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        //get customer by id
        webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
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
                "password",
                faker.number().numberBetween(18, 99),
                Gender.MALE
        );
        //send a post request
        String jwtToken = webTestClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Void.class)
                .getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);


        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
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
                "password",
                request.age()+1,
                request.gender()
        );
        webTestClient.put()
                .uri(CUSTOMER_URI+ "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(update), Customer.class)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        //get customer by id
        Customer updated = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header(AUTHORIZATION, String.format("Bearer %s", jwtToken))
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
