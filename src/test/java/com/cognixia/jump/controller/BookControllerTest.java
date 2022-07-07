package com.cognixia.jump.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cognixia.jump.filter.JWTRequestFilter;
import com.cognixia.jump.model.Book;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.BookRepository;
import com.cognixia.jump.service.MyUserDetails;
import com.cognixia.jump.service.MyUserDetailsService;
import com.cognixia.jump.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@WebMvcTest(value = BookController.class, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtUtil.class) })
public class BookControllerTest {

	private static final String STARTING_URI = "http://localhost:8080/api";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private MyUserDetailsService myUserDetailsService;

	@MockBean
	private BookRepository repo;
	
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JWTRequestFilter filter;

	@Test
	void testGetBooks() throws Exception {
		String uri = STARTING_URI + "/books";
		User admin = new User(2, "admin2", "pass123", User.Role.ROLE_ADMIN, true);

		UserDetails dummy = new MyUserDetails(admin);
		String jwtToken = jwtUtil.generateTokens(dummy);
		RequestBuilder request = MockMvcRequestBuilders.get(uri).header("Authorization", "Bearer " + jwtToken);

		List<Book> allBooks = new ArrayList<Book>();
		Book book1 = new Book(1, "TestBook1", "TestAuthor1", "12ISBN21", 10.00, 5);
		Book book2 = new Book(2, "TestBook2", "TestAuthor2", "21ISBN12", 20.00, 10);
		allBooks.add(book1);
		allBooks.add(book2);
		
		when(repo.findAll()).thenReturn(allBooks);
		when(myUserDetailsService.loadUserByUsername("admin2")).thenReturn(dummy);
		
		mvc.perform(request).andDo(print()).andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(jsonPath("$.length()").value(allBooks.size())) // length of the list matches one above
		.andExpect(jsonPath("$[0].id").value(allBooks.get(0).getId())) 
		.andExpect(jsonPath("$[0].title").value(allBooks.get(0).getTitle()))
		.andExpect(jsonPath("$[0].author").value(allBooks.get(0).getAuthor()))
		.andExpect(jsonPath("$[0].isbn").value(allBooks.get(0).getIsbn()))
		.andExpect(jsonPath("$[0].price").value(allBooks.get(0).getPrice()))
		.andExpect(jsonPath("$[0].quantity").value(allBooks.get(0).getQuantity()))
		.andExpect(jsonPath("$[1].id").value(allBooks.get(1).getId())) 
		.andExpect(jsonPath("$[1].title").value(allBooks.get(1).getTitle()))
		.andExpect(jsonPath("$[1].author").value(allBooks.get(1).getAuthor()))
		.andExpect(jsonPath("$[1].isbn").value(allBooks.get(1).getIsbn()))
		.andExpect(jsonPath("$[1].price").value(allBooks.get(1).getPrice()))
		.andExpect(jsonPath("$[1].quantity").value(allBooks.get(1).getQuantity()));

		verify(repo, times(1)).findAll();
		verifyNoMoreInteractions(repo);
		
	}
	
	@Test
	void testGetBookById() throws Exception {
		String uri = STARTING_URI + "/books/{id}";
		User admin = new User(2, "admin2", "pass123", User.Role.ROLE_ADMIN, true);

		int id = 1;
		Book book = new Book(id, "TestBook1", "TestAuthor1", "12ISBN21", 10.00, 5);
		
		UserDetails dummy = new MyUserDetails(admin);
		String jwtToken = jwtUtil.generateTokens(dummy);
		RequestBuilder request = MockMvcRequestBuilders.get(uri, id)
				.header("Authorization", "Bearer " + jwtToken);

		when(repo.findById(id)).thenReturn(Optional.of(book));
		when(myUserDetailsService.loadUserByUsername("admin2")).thenReturn(dummy);
		
		mvc.perform(request).andDo(print()).andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(jsonPath("$.id").value(book.getId())) 
			.andExpect(jsonPath("$.title").value(book.getTitle()))
			.andExpect(jsonPath("$.author").value(book.getAuthor()))
			.andExpect(jsonPath("$.isbn").value(book.getIsbn()))
			.andExpect(jsonPath("$.price").value(book.getPrice()))
			.andExpect(jsonPath("$.quantity").value(book.getQuantity()));
		
		verify(repo, times(1)).findById(id);
		verifyNoMoreInteractions(repo);
	}
	
	@Test
	void testCreateBook() throws Exception {
		String uri = STARTING_URI + "/books/add";
		User admin = new User(2, "admin2", "pass123", User.Role.ROLE_ADMIN, true);

		int id = 1;
		Book book = new Book(id, "TestBook1", "TestAuthor1", "12ISBN21", 10.00, 5);
		
		UserDetails dummy = new MyUserDetails(admin);
		String jwtToken = jwtUtil.generateTokens(dummy);
		RequestBuilder request = MockMvcRequestBuilders.post(uri)
											.content(asJsonString(book))
											.contentType(MediaType.APPLICATION_JSON_VALUE)
											.header("Authorization", "Bearer " + jwtToken);
		
		when(repo.save(Mockito.any(Book.class))).thenReturn(book);
		when(myUserDetailsService.loadUserByUsername("admin2")).thenReturn(dummy);
		
		mvc.perform(request).andDo(print()).andExpect(status().isCreated())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
		.andExpect(jsonPath("$.id").value(book.getId())) 
		.andExpect(jsonPath("$.title").value(book.getTitle()))
		.andExpect(jsonPath("$.author").value(book.getAuthor()))
		.andExpect(jsonPath("$.isbn").value(book.getIsbn()))
		.andExpect(jsonPath("$.price").value(book.getPrice()))
		.andExpect(jsonPath("$.quantity").value(book.getQuantity()));

		verify(repo, times(1)).save(Mockito.any(Book.class));
		verifyNoMoreInteractions(repo);
	}
	
	@Test
	void testUpdateInventory() throws Exception {
		String uri = STARTING_URI + "/book/inventory";
		User admin = new User(2, "admin2", "pass123", User.Role.ROLE_ADMIN, true);

		int id = 1;
		Book bookUpdated = new Book(id, "TestBook1", "TestAuthor1", "12ISBN21", 10.00, 5);
		
		UserDetails dummy = new MyUserDetails(admin);
		String jwtToken = jwtUtil.generateTokens(dummy);
		RequestBuilder request = MockMvcRequestBuilders.patch(uri)
				.param("id", "1")
				.param("quantity", "5")
				.header("Authorization", "Bearer " + jwtToken);
		
		when(repo.updateBookInventory(id, bookUpdated.getQuantity())).thenReturn(1);
		when(myUserDetailsService.loadUserByUsername("admin2")).thenReturn(dummy);
		
		mvc.perform(request).andDo(print()).andExpect(status().isOk());

		verify(repo, times(1)).updateBookInventory(id, bookUpdated.getQuantity());
		verifyNoMoreInteractions(repo);
	}
	
	@Test
	void testRemoveBook() throws Exception {
		String uri = STARTING_URI + "/book/{id}";
		User admin = new User(2, "admin2", "pass123", User.Role.ROLE_ADMIN, true);

		int id = 1;
		Book book = new Book(id, "TestBook1", "TestAuthor1", "12ISBN21", 10.00, 5);
		
		UserDetails dummy = new MyUserDetails(admin);
		String jwtToken = jwtUtil.generateTokens(dummy);
		RequestBuilder request = MockMvcRequestBuilders.delete(uri, id).header("Authorization", "Bearer " + jwtToken);

		when(repo.existsById(id)).thenReturn(true);
		when(myUserDetailsService.loadUserByUsername("admin2")).thenReturn(dummy);
		
		mvc.perform(request).andDo(print()).andExpect(status().isOk());
		
		verify(repo, times(1)).existsById(id);
		
	}
	
	
	public static String asJsonString(final Object obj) {
		
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			throw new RuntimeException();
		}
		
	}
}
