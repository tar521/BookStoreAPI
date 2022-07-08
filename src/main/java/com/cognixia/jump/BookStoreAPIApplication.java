package com.cognixia.jump;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@SecurityScheme(name = "v3/api-docs", scheme = "basic", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@OpenAPIDefinition(
		info = @Info( title="BookStore API", version="1.0",
				description = "API that allows a user to buy books in the application.",
				contact = @Contact(name = "Tristram Reed", email = "notreal@email.com",
									url = "mywebpage.com")))
public class BookStoreAPIApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStoreAPIApplication.class, args);
	}

}
