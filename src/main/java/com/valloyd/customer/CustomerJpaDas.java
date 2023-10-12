package com.valloyd.customer;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class CustomerJpaDas implements CustomerDao{

	private final CustomerRepository customerRepository;

	public CustomerJpaDas(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Override
	public List<Customer> selectAllCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public Optional<Customer> selectCustomerById(Integer id) {
		return customerRepository.findById(id);
	}

	@Override
	public void insertCustomer(Customer customer) {
		customerRepository.save(customer);
	}

	@Override
	public boolean existsCustomerWithEmail(String email) {
		return customerRepository.existsCustomerByEmail(email);
	}

	@Override
	public boolean existsCustomerWithId(Integer id) {
		return customerRepository.existsCustomerById(id);
	}

	@Override
	public void deleteCustomerById(Integer id) {
		customerRepository.deleteById(id);
	}
}
