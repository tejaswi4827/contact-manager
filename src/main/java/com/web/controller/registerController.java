package com.web.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.aspectj.weaver.NewConstructorTypeMunger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.entity.User;
import com.web.helper.Message;
import com.web.repo.UserRepo;
@Controller
public class registerController {
	@Autowired
	private BCryptPasswordEncoder PasswordEncoder;
	
	@Autowired
	private UserRepo userRepo;
	//handler for registering user
		@RequestMapping(value = "/do_register", method = RequestMethod.POST)
		public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,@RequestParam(value = "agreement",defaultValue = "false")boolean agreement,Model model,HttpSession session)
		{
			try {
				if(agreement == false)
				{
					System.out.println("You have not agreed terms and conditions!!!");
					throw new Exception("You have not agreed terms and conditions!!!");
				}
				if(result.hasErrors())
				{
					System.out.println("Error" + result.toString());
					model.addAttribute("user",user);
					return "signup";
				}
				
				user.setRole("ROLE_USER");
				user.setEnabled(true);
				user.setImageUrl("default.png");
				//store password in encoded mode
				user.setPassword(PasswordEncoder.encode(user.getPassword()));
				//System.out.println(agreement);
				//System.out.println("user"+user);
				//saving user to db
				userRepo.save(user);
				//to pass value to ui if anything like check box is missing 
				model.addAttribute("user",new User());
				session.setAttribute("message",new Message("Successfully Registered!!!","alert-success"));
				return "signup";
			}
			catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("user",user);
				session.setAttribute("message",new Message("Something went wrong!!!" + e.getMessage(),"alert-danger"));
				return "signup";
			}
			
		}
}
