package com.valloyd.customer;

import com.valloyd.exception.DuplicateResourceException;
import com.valloyd.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

	private final CustomerDao customerDao;

	public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
		this.customerDao = customerDao;
	}

	public List<Customer> getAllCustomers(){
		return customerDao.selectAllCustomers();
	}

	public Customer getCustomer(Integer id){
		return customerDao.selectCustomerById(id)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Customer with id %s not found.".formatted(id)
				));
	}

	public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
		String email = customerRegistrationRequest.email();

		if (customerDao.existsCustomerWithEmail(email)){
			throw new DuplicateResourceException("Email taken");
		}

		Customer customer = new Customer(
				customerRegistrationRequest.name(),
				customerRegistrationRequest.email(),
				customerRegistrationRequest.age()
		);

		customerDao.insertCustomer(customer);
	}
}
