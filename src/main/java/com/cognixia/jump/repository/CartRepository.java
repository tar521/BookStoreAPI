package com.cognixia.jump.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cognixia.jump.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

	@Query("select c from Cart c where c.user.id = ?1")
	public List<Cart> userCart(int id);
	
	@Transactional
	@Modifying
	@Query("delete from Cart c where c.id = ?1")
	public int deleteCartEntryById(Integer id);
	
	@Transactional
	@Modifying
	@Query("delete from Cart c where c.user.id = ?1")
	public int checkOutCart(Integer id);
	
}
