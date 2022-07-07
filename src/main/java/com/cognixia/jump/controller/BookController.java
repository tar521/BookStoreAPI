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

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "book", description = "the API for managing books")
public class BookController {
	
	@Autowired
	BookRepository repo;
	
	@GetMapping("/books")
	public List<Book> getBooks() {
		return repo.findAll();
	}
	
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
	
	// ADMIN ONLY
	@PostMapping("/books/add")
	public ResponseEntity<?> createBook(@Valid @RequestBody Book book) {
		
		book.setId(null);
		
		Book created = repo.save(book);
		
		return ResponseEntity.status(201).body(created);
	}
	
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
