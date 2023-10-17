package com.valloyd;

import com.github.javafaker.Faker;
import com.valloyd.customer.Customer;
import com.valloyd.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

	@Bean
	CommandLineRunner runner(CustomerRepository customerRepository) {
		return args -> {
			var faker = new Faker();

			var name = faker.name().firstName() + " " + faker.name().lastName();
			var email = name.toLowerCase().replace(' ','.') + "@gmail.com";
			var age = faker.random().nextInt(18,120);

			Customer customer = new Customer(name, email, age);

			customerRepository.save(customer);
		};
	}
}
