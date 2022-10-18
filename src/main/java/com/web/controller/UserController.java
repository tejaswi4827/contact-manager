package com.web.controller;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.apache.catalina.filters.AddDefaultCharsetFilter;
import org.aspectj.weaver.NewConstructorTypeMunger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.MergedAnnotation.Adapt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.web.entity.Contact;
import com.web.entity.User;
import com.web.helper.Message;
import com.web.repo.ContactRepo;
import com.web.repo.UserRepo;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ContactRepo contactRepo;

	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;

	// MODEL ATTRIBUTE run this method for every handler
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("username" + " " + userName);
		// get the user using username
		User user = userRepo.getUserByUserName(userName);

		model.addAttribute("user", user);

	}

	// payment gateway creating order
	@PostMapping("/create_order")
	@ResponseBody // it return string not view
	public String createOrder(@RequestBody Map<String, Object> data) throws RazorpayException {
		int amount = Integer.parseInt(data.get("amount").toString());
		// key and paass of razorpay
		RazorpayClient client = new RazorpayClient("rzp_live_eNJbsJPiKkhdpM", "mPHWjHaU20Gm44kJdVK1VLBk");
		JSONObject json = new JSONObject();
		json.put("amount", amount * 100);
		json.put("currency", "INR");
		json.put("receipt", "txn_235425");
		// creating new orders
		Order order = client.Orders.create(json);

		//System.out.println(order);
		//save order information to database ....
		
		
		
		return order.toString();
	}

	// home dashboard
	@RequestMapping("/dashboard")
	public String dashboard(Model model, Principal principal) {
		// Principal to get unique id of db so that we can query db.
		model.addAttribute("title", "Home dashboard");
		return "normal/user_dashboard";
	}

	// add contact form
	@GetMapping("/add-contact")
	public String addContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";

	}

	// processing add contact form

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {
		try {

			// name is email of user class
			String name = principal.getName();
			// System.out.println("name"+name);
			User user = this.userRepo.getUserByUserName(name);

			// processing and uploading file

			if (file.isEmpty()) {
				// if file is empty
				System.out.println("file is empty");
				contact.setImage("default.jpg");
			} else {
				// find image and upload

				// setting file name of client system
				contact.setImage(file.getOriginalFilename());

				// updating profile image in static/profileImage
				File file2 = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(file2.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}

			contact.setUser(user);
			// System.out.println("user"+user);
			user.getContact().add(contact);
			this.userRepo.save(user);
			System.out.println("added to db");

			// message success
			session.setAttribute("message", new Message("Your data is saved!!!", "success"));

		} catch (Exception e) {
			// error message
			session.setAttribute("message", new Message("Something went wrong? try again!!!", "danger"));
			e.printStackTrace();
		}
		return "normal/add_contact_form";
	}

	// per page n item like 5,6 etc
	// current page = 0

	@GetMapping("/view-contact/{page}")
	public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "This is View contact page");

		// get username
		String username = principal.getName();
		// getting user object
		User user = this.userRepo.getUserByUserName(username);

		// it has two parameter
		// page is current page
		// 5 is per page 5 item
		Pageable pageable = PageRequest.of(page, 5);

		// finding all contact using user id
		Page<Contact> contacts = this.contactRepo.findContactByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		// to find total pages
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/view_contact";
	}

	// showing particular contact details
	@RequestMapping("/{cid}/contact")
	public String showContactDetails(@PathVariable("cid") Integer cid, Model model, Principal principal) {
		System.out.println(cid);
		model.addAttribute("title", "showing contact details");
		Optional<Contact> contacOptional = this.contactRepo.findById(cid);
		Contact contact = contacOptional.get();

		// fixing bugs #25 to show user contact list to that specific user if user id
		// matches to that user od contact list then send data

		String username = principal.getName();
		User user = this.userRepo.getUserByUserName(username);
		if (user.getId() == contact.getUser().getId())
			model.addAttribute("contact", contact);

		return "normal/contact_detail";
	}

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") int cid, Model model, Principal principal, HttpSession session) {
		Optional<Contact> contOptional = this.contactRepo.findById(cid);
		Contact contact = contOptional.get();

		// check to delete user of their own contact list only
		String username = principal.getName();
		User user = this.userRepo.getUserByUserName(username);
		if (user.getId() == contact.getUser().getId()) {
			contact.setUser(null);// for removing cascade all in database dependency
			this.contactRepo.delete(contact);
			session.setAttribute("message", new Message("Contact deleted successfully", "success"));

		} else {

			session.setAttribute("message",
					new Message("Can't delete this contact it is not in your contact list try again!!!", "danger"));
		}

		// delete image from path of project for the user

		return "redirect:/user/view-contact/0";
	}

	// update contact form

	@GetMapping("/add-contact/{cid}")
	public String updateContactForm(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepo.findById(cid).get();
		model.addAttribute("contact", contact);
		return "normal/add_contact_form";

	}

	// profile handler
	@GetMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title", "Profile page");
		return "normal/profile";
	}

	// settings handler
	@GetMapping("/settings")
	public String openSettings() {
		return "normal/settings";
	}

	// data cming in url use path variable // data coming from form user
	// requestparam

	// change password controller
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession httpSession) {
		// System.out.println(oldPassword);
		User user = this.userRepo.getUserByUserName(principal.getName());
		// if password from ui which is old password and encrypted password
		// user.getpassword() is equal
		if (this.bcryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
			// then change the password
			user.setPassword(this.bcryptPasswordEncoder.encode(newPassword));
			this.userRepo.save(user);
			httpSession.setAttribute("message", new Message("Your password is successfully changed!!!", "success"));
		} else {
			// error
			httpSession.setAttribute("message",
					new Message("Your password doesnot match.. pls try again!!!", "danger"));
			return "normal/settings";
		}
		return "normal/settings";
	}

//user details interface has many method ctrl+shift+T  to search interface
//user details service is interface  ctrl+shift+T to view interface

}
