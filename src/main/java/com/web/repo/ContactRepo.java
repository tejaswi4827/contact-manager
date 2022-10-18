package com.web.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.web.entity.Contact;
import com.web.entity.User;

public interface ContactRepo extends JpaRepository<Contact, Integer> {

	//pagination method
	
	
	//pageable have  two information current page and content per page
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactByUser(@Param("userId") int userId,Pageable pageable);
	
	
	public List<Contact>findByNameContainingAndUser(String name,User user);
	
}
