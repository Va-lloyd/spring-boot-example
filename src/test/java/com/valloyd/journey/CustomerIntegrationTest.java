package com.valloyd.journey;

import com.github.javafaker.Faker;
import com.valloyd.customer.Customer;
import com.valloyd.customer.CustomerRegistrationRequest;
import com.valloyd.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {
	@Autowired
	private WebTestClient webTestClient;
	private static final Random RANDOM = new Random();
	private static final String CUSTOMER_URI = "/api/v1/customers";

	@Test
	void canRegisterCustomer() {
		// Create registration request.
		var faker = new Faker();
		var fakerName = faker.name();

		var name = fakerName.fullName();
		var email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
		var age = RANDOM.nextInt(18, 120);

		CustomerRegistrationRequest request =
				new CustomerRegistrationRequest(name, email, age);
		// Send post request.

		webTestClient.post()
				.uri(CUSTOMER_URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(request), CustomerRegistrationRequest.class)
				.exchange()
				.expectStatus()
				.isOk();

		// Get all customers.
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

		// Make sure customer is present.
		Customer expectedCustomer = new Customer(
				name, email, age
		);

		assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
				.contains(expectedCustomer);

		var id = allCustomers.stream()
				.filter(customer -> customer.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		expectedCustomer.setId(id);

		// Get customer by ID.
		webTestClient.get()
				.uri(CUSTOMER_URI + "/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(new ParameterizedTypeReference<Customer>() {
				})
				.isEqualTo(expectedCustomer);
	}

	@Test
	void canDeleteCustomer() {
		// Create registration request.
		var faker = new Faker();
		var fakerName = faker.name();

		var name = fakerName.fullName();
		var email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
		var age = RANDOM.nextInt(18, 120);

		CustomerRegistrationRequest request =
				new CustomerRegistrationRequest(name, email, age);
		// Send post request.

		webTestClient.post()
				.uri(CUSTOMER_URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(request), CustomerRegistrationRequest.class)
				.exchange()
				.expectStatus()
				.isOk();

		// Get all customers.
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

		var id = allCustomers.stream()
				.filter(customer -> customer.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		// Delete customer
		webTestClient.delete()
				.uri(CUSTOMER_URI + "/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk();

		// Get customer by ID.
		webTestClient.get()
				.uri(CUSTOMER_URI + "/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isNotFound();
	}

	@Test
	void canUpdateCustomer() {
		// Create registration request.
		var faker = new Faker();
		var fakerName = faker.name();

		var name = fakerName.fullName();
		var email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
		var age = RANDOM.nextInt(18, 120);

		CustomerRegistrationRequest request =
				new CustomerRegistrationRequest(name, email, age);
		// Send post request.

		webTestClient.post()
				.uri(CUSTOMER_URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(request), CustomerRegistrationRequest.class)
				.exchange()
				.expectStatus()
				.isOk();

		// Get all customers.
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

		var id = allCustomers.stream()
				.filter(customer -> customer.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		// Update customer values

		String newName = "testName";
		CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, null, null);

		webTestClient.put()
				.uri(CUSTOMER_URI + "/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(Mono.just(updateRequest), CustomerUpdateRequest.class)
				.exchange()
				.expectStatus()
				.isOk();

		// Get customer by ID.
		Customer updatedCustomer = webTestClient.get()
				.uri(CUSTOMER_URI + "/{id}", id)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody(Customer.class)
				.returnResult()
				.getResponseBody();

		Customer expected = new Customer(
				id, newName, email, age
		);

		assertThat(updatedCustomer).isEqualTo(expected);
	}
}
