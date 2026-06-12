package com.example.loginApp.jwtAuth.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loginApp.jwtAuth.entity.User;
import com.example.loginApp.jwtAuth.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserRepository userRepo;
	
	@PostMapping
	public User createUser() {
		
		User user= User.builder().userName("admin").password("admin123").role("ADMIN").build();
		return userRepo.save(user);
		
	}
	
	@GetMapping
	public List<User> getAll(){
		return userRepo.findAll();
	}
	
	@GetMapping("/profile")
	public String profile(Authentication auth) {
		return "Welcome" + auth.getName();
	}

}
