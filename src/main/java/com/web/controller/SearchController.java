package com.web.controller;

import java.security.Principal;
import java.util.List;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.web.entity.Contact;
import com.web.repo.ContactRepo;
import com.web.repo.UserRepo;

@RestController
public class SearchController {
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ContactRepo contactRepo;

	// search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		
		com.web.entity.User user = this.userRepo.getUserByUserName(principal.getName());
	List<Contact> contacts =	this.contactRepo.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
}
