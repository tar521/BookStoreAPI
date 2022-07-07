package com.cognixia.jump.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.exception.ZeroQuantityException;
import com.cognixia.jump.model.AuthenticationRequest;
import com.cognixia.jump.model.AuthenticationResponse;
import com.cognixia.jump.model.Book;
import com.cognixia.jump.model.Cart;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.UserRepository;
import com.cognixia.jump.service.UserService;
import com.cognixia.jump.util.JwtUtil;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "user", description = "the API for user flow and usage")
public class UserController {

	@Autowired
	UserRepository repo;

	@Autowired
	UserService service;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	PasswordEncoder encoder;

	// ADMIN ONLY
	@GetMapping("/user")
	public List<User> getUsers() {
		return repo.findAll();
	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {

		// try to catch the exception for bad credentials, just so we can set our own
		// message when this doesn't work
		try {
			// make sure we have a valid user by checking their username and password
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		} catch (BadCredentialsException e) {
			// provide our own message on why login didn't work
			throw new Exception("Incorrect username or password");
		}

		// as long as no exception was thrown, user is valid

		// load in the user details for that user
		final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

		// generate the token for that user
		final String jwt = jwtUtil.generateTokens(userDetails);

		// return the token
		return ResponseEntity.status(201).body(new AuthenticationResponse(jwt));
	}

	// ONLY ABLE TO MAKE USERS - NO ADMINS
	@PostMapping("/register")
	public ResponseEntity<?> createUser(@Valid @RequestBody User user) {

		user.setId(null);
		user.setRole(User.Role.ROLE_USER);

		// encode the password before it gets saved to the db, if not password will be
		// stored
		// as plain text and cause issues
		// security won't encode our password for us when we do our POST, so we do it
		// here on this line
		user.setPassword(encoder.encode(user.getPassword()));

		User created = repo.save(user);

		return ResponseEntity.status(201).body(created);

	}
	
	@PatchMapping("/change/password")
	public ResponseEntity<?> updatePassword(@PathParam(value = "password") String password) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Optional<User> found = repo.findByUsername(username);
		
		if (!found.isEmpty()) {
			int id = found.get().getId();
			
			
			String hashed = encoder.encode(password);
			
			found.get().setPassword(hashed);
			//repo.save(found.get());
			
			int result = repo.updateUserPassword(id, hashed);
			
			if (result > 0) {
				return ResponseEntity.status(200).body("User password updated  " + password + "  " + hashed);
			}
			else {
				return ResponseEntity.status(404).body("User password could not be updated");
			}

			
		} else {
			return ResponseEntity.status(404).body("User not found");
		}
	}

	// ADMIN ONLY
	@GetMapping("/user/{name}")
	public ResponseEntity<?> getUserByUsername(@PathVariable String name) {
		Optional<User> found = repo.findByUsername(name);

		if (found.isEmpty()) {
			return ResponseEntity.status(404).body("User not found");
		} else {
			return ResponseEntity.status(200).body(found.get());
		}
	}
	
	// ADMIN ONLY
	@GetMapping("/user/{id}")
	public ResponseEntity<?> getUserById(@PathVariable int id) {
		Optional<User> found = repo.findById(id);

		if (found.isEmpty()) {
			return ResponseEntity.status(404).body("User not found");
		} else {
			return ResponseEntity.status(200).body(found.get());
		}
	}

	@GetMapping("/user/cart")
	public ResponseEntity<?> getUserCart() {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Optional<User> found = repo.findByUsername(username);

		if (!found.isEmpty()) {
			HashMap<Integer, Book> userCart = service.getCart(found.get().getId());

			if (userCart.isEmpty()) {
				return ResponseEntity.status(200).body("Cart is Empty");
			} else {
				return ResponseEntity.status(200).body(userCart);
			}
		} else {
			return ResponseEntity.status(404).body("User not found");
		}
	}

	@PostMapping("/user/addToCart")
	public ResponseEntity<?> addBookToCart(@Valid @RequestBody Book book) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Optional<User> found = repo.findByUsername(username);

		if (!found.isEmpty()) {
			Cart cart = service.addToCart(found.get(), book);

			if (cart.getId() == null) {
				return ResponseEntity.status(404).body("Book couldn't be added");
			} else {
				return ResponseEntity.status(200).body(cart);
			}
		} else {
			return ResponseEntity.status(404).body("User not found");
		}
	}

	@PostMapping("/user/addToCart/{id}")
	public ResponseEntity<?> addBookToCartById(@PathVariable int id) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Optional<User> found = repo.findByUsername(username);

		if (!found.isEmpty()) {
			Cart cart = service.addToCart(found.get(), id);

			if (cart.getId() == null) {
				return ResponseEntity.status(404).body("Book couldn't be added");
			} else {
				return ResponseEntity.status(200).body(cart);
			}
		} else {
			return ResponseEntity.status(404).body("User not found");
		}

	}
	
	@DeleteMapping("/user/cart/checkout")
	public ResponseEntity<?> userCheckOut() throws ZeroQuantityException {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Optional<User> found = repo.findByUsername(username);

		if (!found.isEmpty()) {
			String result = service.userCheckOut(found.get().getId());
			
			if (result != null) {
				throw new ZeroQuantityException("Book(s)", result);
			}
			
			return ResponseEntity.status(200).body("Cart checked out");
			
			
		} else {
			return ResponseEntity.status(404).body("User not found");
		}
	}

	@DeleteMapping("/user/cart")
	public ResponseEntity<?> deleteCartItemByBook(@RequestBody Book book) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Optional<User> found = repo.findByUsername(username);

		if (!found.isEmpty()) {
			HashMap<Integer, Book> userCart = service.getCart(found.get().getId());

			if (userCart.isEmpty()) {
				return ResponseEntity.status(404).body("Cart is Empty");
			} else {
				int removed = -1;

				for (Map.Entry<Integer, Book> e : userCart.entrySet()) {
					if (book.getId().equals(e.getValue().getId())) {
						removed = service.editCart(e.getKey());
						break;
					}
				}

				if (removed > 0) {
					return ResponseEntity.status(200).body("Book removed from cart");
				} else {
					return ResponseEntity.status(404).body("Book not found");
				}
			}
		} else {
			return ResponseEntity.status(404).body("User not found");
		}

	}

	@DeleteMapping("/user/cart/{id}")
	public ResponseEntity<?> deleteCartItemById(@PathVariable int id) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Optional<User> found = repo.findByUsername(username);

		if (!found.isEmpty()) {
			HashMap<Integer, Book> userCart = service.getCart(found.get().getId());

			if (userCart.isEmpty()) {
				return ResponseEntity.status(404).body("Cart is Empty");
			} else {
				int removed = -1;

				for (Map.Entry<Integer, Book> e : userCart.entrySet()) {
					if (e.getKey().equals(id)) {
						removed = service.editCart(e.getKey());
						break;
					}
				}

				if (removed > 0) {
					return ResponseEntity.status(200).body("Book removed from cart");
				} else {
					return ResponseEntity.status(404).body("Book not found");
				}
			}
		} else {
			return ResponseEntity.status(404).body("User not found");
		}
	}
	
	//TODO
	// ADD FUNCTIONALITY FOR AN ADMIN TO CREATE AN ADMIN WITH A DEFAULT PASSWORD
	
	//TODO
	// ADD ADMIN ENDPOINTS TO EDIT USERS (ENABLE DISABLE REMOVE)
}
