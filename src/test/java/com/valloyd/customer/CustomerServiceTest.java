package com.valloyd.customer;

import com.valloyd.exception.DuplicateResourceException;
import com.valloyd.exception.RequestValidationException;
import com.valloyd.exception.ResourceNotFoundException;
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

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	@Mock
	private CustomerDao customerDao;
	private CustomerService underTest;

	@BeforeEach
	void setUp() {
		underTest = new CustomerService(customerDao);
	}

	@Test
	void getAllCustomers() {
		// When
		underTest.getAllCustomers();

		// Then
		verify(customerDao).selectAllCustomers();
	}

	@Test
	void canGetCustomer() {
		// Given
		var id = 1;
		Customer customer = new Customer(id, "Jip", "jip@gmail.com", 28);
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

		// When
		Customer actual = underTest.getCustomer(id);

		// Then
		assertThat(actual).isEqualTo(customer);
	}

	@Test
	void willThrowWhenGetCustomerReturnEmptyOptional() {
		// Given
		int id = 100;
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

		// When
		// Then
		assertThatThrownBy(() -> underTest.getCustomer(id))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Customer with id %s not found.".formatted(id));
	}

	@Test
	void addCustomer() {
		// Given
		String email = "gop@gmail.com";
		when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

		CustomerRegistrationRequest request = new CustomerRegistrationRequest("Gop", email, 28);

		// When
		underTest.addCustomer(request);

		// Then
		ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
		verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

		Customer capturedCustomer = customerArgumentCaptor.getValue();

		assertThat(capturedCustomer.getId()).isNull();
		assertThat(capturedCustomer.getName()).isEqualTo(request.name());
		assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
		assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
	}

	@Test
	void willThrowWhenEmailExistsWhileAddingCustomer() {
		// Given
		String email = "gop@gmail.com";
		when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

		CustomerRegistrationRequest request = new CustomerRegistrationRequest("Gop", email, 28);

		// When
		assertThatThrownBy(() -> underTest.addCustomer(request))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Email taken");

		// Then
		verify(customerDao, never()).insertCustomer(any());
	}

	@Test
	void deleteCustomerById() {
		// Given
		var id = 1;
		when(customerDao.existsCustomerWithId(id)).thenReturn(true);

		// When
		underTest.deleteCustomerById(id);

		// Then
		verify(customerDao).deleteCustomerById(id);
	}

	@Test
	void willThrowWhenDeleteCustomerByIdNotExist() {
		// Given
		var id = 1;
		when(customerDao.existsCustomerWithId(id)).thenReturn(false);

		// When
		assertThatThrownBy(() -> underTest.deleteCustomerById(id))
				.isInstanceOf(ResourceNotFoundException.class)
						.hasMessage("Customer with ID [%s] not found.".formatted(id));

		// Then
		verify(customerDao, never()).deleteCustomerById(id);
	}

	@Test
	void canUpdateAllCustomerProperties() {
		// Given
		var id = 1;
		Customer customer = new Customer(id, "Dil", "dil@gmail.com", 28);
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

		String newEmail = "dal@gmail.com";
		CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Dal", newEmail, 29);

		when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

		// When
		underTest.updateCustomer(id, updateRequest);

		// Then
		ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
		verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

		Customer capturedCustomer = customerArgumentCaptor.getValue();

		assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
		assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
		assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
	}

	@Test
	void canUpdateOnlyCustomerName() {
		// Given
		var id = 1;
		Customer customer = new Customer(id, "Dil", "dil@gmail.com", 28);
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

		CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Dal", null, null);

		// When
		underTest.updateCustomer(id, updateRequest);

		// Then
		ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
		verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

		Customer capturedCustomer = customerArgumentCaptor.getValue();

		assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
		assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
		assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
	}

	@Test
	void canUpdateOnlyCustomerEmail() {
		// Given
		var id = 1;
		Customer customer = new Customer(id, "Dil", "dil@gmail.com", 28);
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

		String newEmail = "dal@gmail.com";
		CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

		when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

		// When
		underTest.updateCustomer(id, updateRequest);

		// Then
		ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
		verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

		Customer capturedCustomer = customerArgumentCaptor.getValue();

		assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
		assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
		assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
	}

	@Test
	void canUpdateOnlyCustomerAge() {
		// Given
		var id = 1;
		Customer customer = new Customer(id, "Dil", "dil@gmail.com", 28);
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

		CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, 50);

		// When
		underTest.updateCustomer(id, updateRequest);

		// Then
		ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
		verify(customerDao).updateCustomer(customerArgumentCaptor.capture());

		Customer capturedCustomer = customerArgumentCaptor.getValue();

		assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
		assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
		assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
	}

	@Test
	void willThrowWhenTryingToUpdateCustomerEmailWhenTaken() {
		// Given
		var id = 1;
		Customer customer = new Customer(id, "Dil", "dil@gmail.com", 28);
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

		String newEmail = "dal@gmail.com";
		CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null);

		when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

		// When
		assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
				.isInstanceOf(DuplicateResourceException.class)
				.hasMessage("Email taken");

		// Then
		verify(customerDao, never()).updateCustomer(any());
	}

	@Test
	void willThrowWhenTryingToUpdateWhenThereAreNoChanges() {
		// Given
		var id = 1;
		Customer customer = new Customer(id, "Dil", "dil@gmail.com", 28);
		when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

		CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

		// When
		assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
				.isInstanceOf(RequestValidationException.class)
				.hasMessage("No data changes found.");

		// Then
		verify(customerDao, never()).updateCustomer(any());
	}
}