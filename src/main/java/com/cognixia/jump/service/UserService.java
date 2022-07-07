package com.cognixia.jump.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognixia.jump.exception.ZeroQuantityException;
import com.cognixia.jump.model.Book;
import com.cognixia.jump.model.Cart;
import com.cognixia.jump.model.User;
import com.cognixia.jump.repository.BookRepository;
import com.cognixia.jump.repository.CartRepository;
import com.cognixia.jump.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	BookRepository bookRepo;
	
	@Autowired
	CartRepository cartRepo;
	
	@Autowired
	UserRepository userRepo;
	
	// USER CART
	public HashMap<Integer, Book> getCart(int user_id) {
		HashMap<Integer, Book> cart = new HashMap<Integer, Book>();
		for (Cart c : cartRepo.userCart(user_id)) {
			//c.getBook().setQuantity(1);
			cart.put(c.getId(), c.getBook());
		}
		return cart;
	}
	
	// USER CHECKOUT
	public String userCheckOut(Integer user_id) {
		String result = new String("");
		HashMap<Integer, Book> cart = getCart(user_id);
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Book> e : cart.entrySet()) {
			
			if (counter.containsKey(e.getValue().getId())) {
				Integer count = counter.remove(e.getValue().getId());
				count += 1;
				counter.put(e.getValue().getId(), count);
			}
			else {
				counter.put(e.getValue().getId(), 1);
			}
			
		}
		
		
		for (Map.Entry<Integer, Integer> b : counter.entrySet()) {
			Optional<Book> book = bookRepo.findById(b.getKey());
			if (b.getValue() > book.get().getQuantity()) {
				bookRepo.updateBookInventory(book.get().getId(), 0);
				counter.put(book.get().getId(), counter.get(book.get().getId()) - book.get().getQuantity());
				continue;
				//return -1 * b.getKey();
			}
			bookRepo.updateBookInventory(book.get().getId(), book.get().getQuantity() - b.getValue());
			counter.remove(book.get().getId());
		}
		
		if(counter.isEmpty()) {
			result = null;
			cartRepo.checkOutCart(user_id);
			return result;
		}
		else {
			cartRepo.checkOutCart(user_id);
			for (Map.Entry<Integer, Integer> b : counter.entrySet()) {
				for (int i = 0; i < b.getValue(); i++) {
					addToCart(userRepo.findById(user_id).get(), bookRepo.findById(b.getKey()).get());
				}
				result += "[" + b.getKey() + " | " + b.getValue() + " items] ";
			}
			return result;
		}
	}
	
	
	// remove order from cart
	public int editCart(Integer cart_id) {
		return cartRepo.deleteCartEntryById(cart_id);
	}
	
	// USER ADD BOOKS
	public Cart addToCart(User user, Book book) {
		
		Cart cart = new Cart(null, user, book, LocalDateTime.now());
		
		Cart created = cartRepo.save(cart);
		
		return created;
	}
	
	public Cart addToCart(User user, int book_id) {
		
		Optional<Book> found = bookRepo.findById(book_id);
		
		if (!found.isEmpty()) {
			return addToCart(user, found.get());
		}
		else {
			return null;
		}		
	}
	
	

}
