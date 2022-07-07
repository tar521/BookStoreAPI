package com.cognixia.jump.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cognixia.jump.filter.JWTRequestFilter;
import com.cognixia.jump.model.Book;
import com.cognixia.jump.model.Cart;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.CartRepository;
import com.cognixia.jump.service.MyUserDetails;
import com.cognixia.jump.service.MyUserDetailsService;
import com.cognixia.jump.util.JwtUtil;

@WebMvcTest(value = CartController.class, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtil.class) })
public class CartControllerTest {

	private static final String STARTING_URI = "http://localhost:8080/api";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MyUserDetailsService myUserDetailsService;

	@MockBean
	private CartRepository repo;

	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private JWTRequestFilter filter;

	@Test
	void testGetAllCarts() throws Exception {
		String uri = STARTING_URI + "/cart";
		User admin = new User(1, "admin", "pass123", User.Role.ROLE_ADMIN, true);

		UserDetails dummy = new MyUserDetails(admin);
		String jwtToken = jwtUtil.generateTokens(dummy);
		RequestBuilder request = MockMvcRequestBuilders.get(uri).header("Authorization", "Bearer " + jwtToken);

		List<Cart> allCartItems = new ArrayList<Cart>();
		Book book1 = new Book(1, "TestBook1", "TestAuthor1", "12ISBN21", 10.00, 5);
		Book book2 = new Book(2, "TestBook2", "TestAuthor2", "21ISBN12", 20.00, 10);
		User user1 = new User(1, "user1", "pass1", User.Role.ROLE_USER, true);
		User user2 = new User(2, "user2", "pass2", User.Role.ROLE_USER, true);
		allCartItems.add(new Cart(1, user1, book1, LocalDateTime.now()));
		allCartItems.add(new Cart(2, user2, book2, LocalDateTime.now()));

		when(repo.findAll()).thenReturn(allCartItems);
		when(myUserDetailsService.loadUserByUsername("admin")).thenReturn(dummy);

		mvc.perform(request).andDo(print()).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.length()").value(allCartItems.size())) // length of the list matches one above
				.andExpect(jsonPath("$[0].id").value(allCartItems.get(0).getId())) 
				.andExpect(jsonPath("$[0].user.id").value(allCartItems.get(0).getUser().getId()))
				.andExpect(jsonPath("$[0].user.username").value(allCartItems.get(0).getUser().getUsername()))
				.andExpect(jsonPath("$[0].user.password").value(allCartItems.get(0).getUser().getPassword()))
				.andExpect(jsonPath("$[0].user.role").value(allCartItems.get(0).getUser().getRole().name()))
				.andExpect(jsonPath("$[0].user.enabled").value(allCartItems.get(0).getUser().isEnabled()))
				.andExpect(jsonPath("$[0].book.id").value(allCartItems.get(0).getBook().getId()))
				.andExpect(jsonPath("$[0].book.title").value(allCartItems.get(0).getBook().getTitle()))
				.andExpect(jsonPath("$[0].book.author").value(allCartItems.get(0).getBook().getAuthor()))
				.andExpect(jsonPath("$[0].book.isbn").value(allCartItems.get(0).getBook().getIsbn()))
				.andExpect(jsonPath("$[0].book.price").value(allCartItems.get(0).getBook().getPrice()))
				.andExpect(jsonPath("$[0].book.quantity").value(allCartItems.get(0).getBook().getQuantity()))
				.andExpect(jsonPath("$[1].id").value(allCartItems.get(1).getId())) 
				.andExpect(jsonPath("$[1].user.id").value(allCartItems.get(1).getUser().getId()))
				.andExpect(jsonPath("$[1].user.username").value(allCartItems.get(1).getUser().getUsername()))
				.andExpect(jsonPath("$[1].user.password").value(allCartItems.get(1).getUser().getPassword()))
				.andExpect(jsonPath("$[1].user.role").value(allCartItems.get(1).getUser().getRole().name()))
				.andExpect(jsonPath("$[1].user.enabled").value(allCartItems.get(1).getUser().isEnabled()))
				.andExpect(jsonPath("$[1].book.id").value(allCartItems.get(1).getBook().getId()))
				.andExpect(jsonPath("$[1].book.title").value(allCartItems.get(1).getBook().getTitle()))
				.andExpect(jsonPath("$[1].book.author").value(allCartItems.get(1).getBook().getAuthor()))
				.andExpect(jsonPath("$[1].book.isbn").value(allCartItems.get(1).getBook().getIsbn()))
				.andExpect(jsonPath("$[1].book.price").value(allCartItems.get(1).getBook().getPrice()))
				.andExpect(jsonPath("$[1].book.quantity").value(allCartItems.get(1).getBook().getQuantity()));

		verify(repo, times(1)).findAll();
		verifyNoMoreInteractions(repo);
	}

}
