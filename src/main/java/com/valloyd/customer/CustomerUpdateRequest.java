package com.valloyd.customer;

public record CustomerUpdateRequest(
		String name,
		String email,
		Integer age
){
}
