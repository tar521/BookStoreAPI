package com.cognixia.jump.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cognixia.jump.model.Book;
import com.cognixia.jump.repository.BookRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "book", description = "the API for managing books")
public class BookController {
	
	@Autowired
	BookRepository repo;
	
	@Operation(summary = "Get all the books in the book table.", 
			   description = "Gets all the books from the book table in the database. Each book grabbed has an id, title, author, isbn, price, and quantity."
			)
	@GetMapping("/books")
	public List<Book> getBooks() {
		return repo.findAll();
	}
	
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Book has been found", 
						 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class) ) ),
			@ApiResponse(responseCode = "201", description = "Book has been created",
						 content = @Content(mediaType = "application/json", schema = @Schema(implementation = Book.class))),
			@ApiResponse(responseCode = "404", description = "Book was not found", 
			 			 content = @Content)
		}
	)
	
	@Operation(summary = "Get a single book from the book table.", 
	   description = "Gets a book specified by its ID from the book table in the database. The book grabbed has an id, title, author, isbn, price, and quantity."
	)
	@GetMapping("/books/{id}")
	public ResponseEntity<?> getBookById(@PathVariable int id) {
		Optional<Book> found = repo.findById(id);
		
		if(found.isEmpty()) {
			return ResponseEntity.status(404).body("Book with id " + id + " could not be found");
		}
		else {
			return ResponseEntity.status(200).body(found.get());
		}
	}
	
	@Operation(summary = "ADMIN ONLY - Add a book to the database.", 
			   description = "Insert a book into the table with all required fields. Each book inserted has an id, title, author, isbn, price, and quantity."
			)
	// ADMIN ONLY
	@PostMapping("/books/add")
	public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
		
		book.setId(null);
		
		Book created = repo.save(book);
		
		return ResponseEntity.status(201).body(created);
	}
	
	@Operation(summary = "ADMIN ONLY - Update quantity of a book.", 
			   description = "Updates the quantity of a book specified by its ID and desired amount."
			)
	// ADMIN ONLY
	@PatchMapping("/book/inventory")
	public ResponseEntity<?> updateInventory(@PathParam(value = "id") int id, @PathParam(value = "quantity") int quantity) {
		
		int count = repo.updateBookInventory(id, quantity);
		
		if (count > 0) {
			return ResponseEntity.status(200).body("Quantity for book was updated");
		}
		else {
			return ResponseEntity.status(404).body("Can't perfrom update, book doesn't exist");
		}
	}
	
	@Operation(summary = "ADMIN ONLY - Removes book from database.", 
			   description = "Removes a book specified by its ID."
			)
	// ADMIN ONLY
	@DeleteMapping("/book/{id}")
	public ResponseEntity<?> removeBook(@PathVariable int id) {
		
		boolean exists = repo.existsById(id);
		
		if(!exists) {
			return ResponseEntity.status(404).body("Book not found");
		}
		else {
			
			repo.deleteById(id);
			return ResponseEntity.status(200).body("Book was deleted");
		}
		
	}
	
}
