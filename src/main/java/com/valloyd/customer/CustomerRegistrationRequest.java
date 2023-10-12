package com.valloyd.customer;

public record CustomerRegistrationRequest (
		String name,
		String email,
		Integer age
){
}
