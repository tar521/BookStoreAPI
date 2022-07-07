package com.cognixia.jump;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info( title="BookStore API", version="1.0",
				description = "API that allows a user to buy books in the application.",
				contact = @Contact(name = "Tristram Reed", email = "notreal@email.com",
									url = "mywebpage.com")))
public class ProductProject2ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductProject2ApiApplication.class, args);
	}

}
