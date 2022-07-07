package com.cognixia.jump.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Book {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@NotBlank
	@Column(nullable = false)
	private String title;
	
	@NotBlank
	@Column(nullable = false)
	private String author;
	
	@Column(unique = true, nullable = true)
	private String isbn;
	
	@Column(nullable = false)
	private double price;
	
	@Column(nullable = false)
	private int quantity;
	
	@OneToMany(mappedBy = "book")
	@JsonIgnore
	private List<Cart> cartItems;
	
	public Book() {
		this(-1, "N/A", "N/A", "N/A", 0, 0);
	}
	
	public Book(Integer id, String title, String author, String isbn, double price, int quantity) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.isbn = isbn;
		this.price = price;
		this.quantity = quantity;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public List<Cart> getCartItems() {
		return cartItems;
	}

	public void setCartItems(List<Cart> cartItems) {
		this.cartItems = cartItems;
	}
	
	
}
