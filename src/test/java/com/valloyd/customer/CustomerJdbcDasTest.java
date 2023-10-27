package com.valloyd.customer;

import com.valloyd.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJdbcDasTest extends AbstractTestcontainers {

	private CustomerJdbcDas underTest;
	private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

	@BeforeEach
	void setUp() {
		underTest = new CustomerJdbcDas(getJdbcTemplate(), customerRowMapper);
	}

	@Test
	void selectAllCustomers() {
		// Given
		Customer customer = new Customer(
				FAKER.name().fullName(),
				FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
				28
		);
		underTest.insertCustomer(customer);

		// When
		List<Customer> customers = underTest.selectAllCustomers();

		// Then
		assertThat(customers).isNotEmpty();
	}

	@Test
	void selectCustomerById() {
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		// When
		Optional<Customer> actual = underTest.selectCustomerById(id);

		// Then
		assertThat(actual).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo(customer.getName());
			assertThat(c.getEmail()).isEqualTo(customer.getEmail());
			assertThat(c.getAge()).isEqualTo(customer.getAge());
		});
	}

	@Test
	void willReturnEmptyWhenSelectCustomerById() {
		// Given
		int id = -1;

		// When
		var actual = underTest.selectCustomerById(id);

		// Then
		assertThat(actual).isEmpty();
	}

	@Test
	void existsCustomerWithEmail() {
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		// When
		boolean actual = underTest.existsCustomerWithEmail(email);

		// Then
		assertThat(actual).isTrue();
	}

	@Test
	void existsPersonWithEmailReturnsFalseWhenDoesNotExist(){
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

		// When
		boolean actual = underTest.existsCustomerWithEmail(email);

		// Then
		assertThat(actual).isFalse();
	}

	@Test
	void existsCustomerWithId() {
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		// When
		boolean actual = underTest.existsCustomerWithId(id);

		// Then
		assertThat(actual).isTrue();
	}

	@Test
	void existsCustomerWithIdWillReturnFalseWhenIdNotPresent() {
		// Given
		int id = -1;

		// When
		boolean actual = underTest.existsCustomerWithId(id);

		// Then
		assertThat(actual).isFalse();
	}

	@Test
	void deleteCustomerById() {
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		// When
		underTest.deleteCustomerById(id);

		// Then
		Optional<Customer> actual = underTest.selectCustomerById(id);
		assertThat(actual).isNotPresent();
	}

	@Test
	void updateCustomerName(){
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		var newName = "foo";

		// When
		Customer update = new Customer();
		update.setId(id);
		update.setName(newName);

		underTest.updateCustomer(update);

		// Then
		Optional<Customer> actual = underTest.selectCustomerById(id);

		assertThat(actual).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo(newName);
			assertThat(c.getEmail()).isEqualTo(customer.getEmail());
			assertThat(c.getAge()).isEqualTo(customer.getAge());
		});
	}

	@Test
	void updateCustomerEmail(){
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		var newEmail = "y65y6y5rggrf5b";

		// When
		Customer update = new Customer();
		update.setId(id);
		update.setEmail(newEmail);

		underTest.updateCustomer(update);

		// Then
		Optional<Customer> actual = underTest.selectCustomerById(id);

		assertThat(actual).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo(customer.getName());
			assertThat(c.getEmail()).isEqualTo(newEmail);
			assertThat(c.getAge()).isEqualTo(customer.getAge());
		});
	}

	@Test
	void updateCustomerAge(){
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		var newAge = 50;

		// When
		Customer update = new Customer();
		update.setId(id);
		update.setAge(newAge);

		underTest.updateCustomer(update);

		// Then
		Optional<Customer> actual = underTest.selectCustomerById(id);

		assertThat(actual).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo(customer.getName());
			assertThat(c.getEmail()).isEqualTo(customer.getEmail());
			assertThat(c.getAge()).isEqualTo(newAge);
		});
	}

	@Test
	void updateAllCustomerProperties() {
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		// When
		Customer update = new Customer();
		update.setId(id);
		update.setName("foo");
		update.setEmail(UUID.randomUUID().toString());
		update.setAge(60);

		underTest.updateCustomer(update);

		// Then
		Optional<Customer> actual = underTest.selectCustomerById(id);
		assertThat(actual).isPresent().hasValue(update);
	}

	@Test
	void willNotUpdateCustomerWhenNothingToUpdate() {
		// Given
		String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
		Customer customer = new Customer(
				FAKER.name().fullName(),
				email,
				28
		);
		underTest.insertCustomer(customer);

		int id = underTest.selectAllCustomers()
				.stream()
				.filter(c -> c.getEmail().equals(email))
				.map(Customer::getId)
				.findFirst()
				.orElseThrow();

		// When attempt update with no changes
		Customer update = new Customer();
		update.setId(id);

		underTest.updateCustomer(update);

		// Then
		Optional<Customer> actual = underTest.selectCustomerById(id);

		assertThat(actual).isPresent().hasValueSatisfying(c -> {
			assertThat(c.getId()).isEqualTo(id);
			assertThat(c.getName()).isEqualTo(customer.getName());
			assertThat(c.getEmail()).isEqualTo(customer.getEmail());
			assertThat(c.getAge()).isEqualTo(customer.getAge());
		});
	}
}