package com.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.entity.User;

public interface UserRepo extends JpaRepository<User,Integer>{
	//param to take dynamic input
	@Query("select u from User u  where u.email = :email")
	public User getUserByUserName(@Param("email") String email);
	
		
	
}
