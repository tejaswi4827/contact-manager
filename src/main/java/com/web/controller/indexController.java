package com.web.controller;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.hibernate.annotations.common.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.entity.User;
import com.web.repo.UserRepo;

@Controller

public class indexController {
//	@Autowired
//	private UserRepo user;
//	User u = new User();
//	
//	@GetMapping("/test")
//	@ResponseBody
//	public String test()
//	{
//		u.setEmail("fgrrddg@gmail.com");
//		user.save(u);
//		return "working";
//	}
	
	
	@RequestMapping("/")
	public String home()
	{
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title","About page");
		return "about";
	}
	
	
	@RequestMapping("/signup/")
	public String signup(Model model)
	{
		model.addAttribute("title","Register - Smart Contract Manager");
		model.addAttribute("user",new User());
		return "signup";
	}
	
	//handler for login
	@GetMapping("/signin")
	public String login(Model model)
	{
		model.addAttribute("title","Login page");
		return "login";
	}
	
	
}
