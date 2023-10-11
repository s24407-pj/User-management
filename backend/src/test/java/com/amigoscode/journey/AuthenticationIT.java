package com.amigoscode.journey;

import com.amigoscode.auth.AuthenticationRequest;
import com.amigoscode.auth.AuthenticationResponse;
import com.amigoscode.customer.CustomerRegistrationRequest;
import com.amigoscode.customer.Gender;
import com.amigoscode.jwt.JWTUtil;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthenticationIT {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private JWTUtil jwtUtil;
    private static final String AUTHENTICATION_PATH = "/api/v1/auth";
    private static final String CUSTOMER_PATH = "/api/v1/customers";

    @Test
    void canLogin() {
        //create registration customerRegistrationRequest
        Faker faker = new Faker();
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                faker.name().fullName(),
                faker.pokemon().name() + UUID.randomUUID() + "@mail.com",
                "password",
                faker.number().numberBetween(18, 99),
                Gender.MALE
        );

        var authenticationRequest = new AuthenticationRequest(
                customerRegistrationRequest.email(),
                customerRegistrationRequest.password()
        );

        webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isUnauthorized();

        //send a post customerRegistrationRequest
        webTestClient.post()
                .uri(CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        EntityExchangeResult<AuthenticationResponse> result = webTestClient.post()
                .uri(AUTHENTICATION_PATH + "/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(authenticationRequest), AuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(new ParameterizedTypeReference<AuthenticationResponse>() {
                })
                .returnResult();

        String jwtToken = result.getResponseHeaders()
                .get(AUTHORIZATION)
                .get(0);

        var customerDTO = result.getResponseBody().customerDTO();

        assertThat(jwtUtil.isTokenValid(
                jwtToken,
                customerDTO.username()));

        assertThat(customerDTO.email()).isEqualTo(customerRegistrationRequest.email());
        assertThat(customerDTO.age()).isEqualTo(customerRegistrationRequest.age());
        assertThat(customerDTO.name()).isEqualTo(customerRegistrationRequest.name());
        assertThat(customerDTO.username()).isEqualTo(customerRegistrationRequest.email());
        assertThat(customerDTO.roles()).isEqualTo(List.of("ROLE_USER"));
    }
}
