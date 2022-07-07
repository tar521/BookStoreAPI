package com.cognixia.jump.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognixia.jump.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

	public Optional<Book> findByTitle(String title);
	
	public Optional<Book> findByAuthor(String author);
	
	@Transactional
	@Modifying
	@Query("update Book b set b.quantity= :quantity where b.id= :id")
	public int updateBookInventory(@Param(value="id")int id, @Param(value="quantity")int quantity);
}
