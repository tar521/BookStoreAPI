package com.cognixia.jump.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.model.Cart;
import com.cognixia.jump.repository.BookRepository;
import com.cognixia.jump.repository.CartRepository;
import com.cognixia.jump.repository.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "cart", description = "the API for seeing user carts")
public class CartController {
	
	@Autowired
	CartRepository cartRepo;
	
	
	// ADMIN ONLY
	@GetMapping("/cart")
	public List<Cart> getAllCarts() {
		return cartRepo.findAll();
	}
}
