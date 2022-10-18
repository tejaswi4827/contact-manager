package com.web.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.web.entity.User;
import com.web.repo.UserRepo;
import com.web.service.EmailService;

@Controller
public class ForgotPassController {
	Random random = new Random();
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	@RequestMapping("/forgot")
public String openEmailForm() {
	return "forgot_email_form";
	
	
}
	
@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session) {
	System.out.println(email);
	
	//generating otp of 4 digit
	
 int number = random.nextInt(999999);
 int otp =Integer.parseInt(String.format("%06d", number));
 //System.out.println(otp);
 
 //write code to send otp to email
 	String subject= "OTP from contact manager";
 	String message=""
 			+ "<div style=border:1px solid #e2e2e2; padding:20px'>"
 			+ "<h1>"
 			+ "OTP is: "
 			+ "<b>"+otp
 			+ "</n>"
 			+ "</h1>"
 			+ "</div>";
 	String to = email;
 	String from = "kumartejaswi1999@gmail.com";
 	User user = this.userRepo.getUserByUserName(email);
 	if(user == null) {
		//send error message if user email is not in database
		 session.setAttribute("message","User doesn't exist with this email!!!");
		return "forgot_email_form";
	}
 	else {
 boolean  flag = this.emailService.sendEmail(message,subject,to,from);
 if(flag) {
	 session.setAttribute("myotp", otp);
	 session.setAttribute("email", email);
	 return "verify_otp";
 }
 else {
	 session.setAttribute("message","check your email id");
	 
		return "forgot_email_form";
 }	
 	}
	}


@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session) {

int myotp = (int) session.getAttribute("myotp");
String email = (String) session.getAttribute("email");
	if(otp == myotp)
	{
	User user = this.userRepo.getUserByUserName(email);
		if(user == null) {
			//send error message if user email is not in database
			 session.setAttribute("message","User doesn't exist with this email!!!");
			return "forgot_email_form";
		}
		else {
		//send change password form
		return "change_password";
		}
	}
	else {
		
			session.setAttribute("message", "you have entered Incorrect otp!!! Try again ");
			return "verify_otp";
		}
	
		
			
}
@PostMapping("/change-password")
public String changePassword(@RequestParam("newPassword") String newPassword,HttpSession session) {
	String email = (String) session.getAttribute("email");
	User user = this.userRepo.getUserByUserName(email);
	user.setPassword(this.bcrypt.encode(newPassword));
	this.userRepo.save(user);
	
	return "redirect:/signin?change=Password changed successfully!!! ";
}
}
