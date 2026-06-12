package com.example.loginApp.jwtAuth.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.loginApp.jwtAuth.dto.AuthResponse;
import com.example.loginApp.jwtAuth.dto.LoginRequest;
import com.example.loginApp.jwtAuth.dto.RegisterRequest;
import com.example.loginApp.jwtAuth.entity.User;
import com.example.loginApp.jwtAuth.security.JwtUtil;
import com.example.loginApp.jwtAuth.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final UserService service;
	private final AuthenticationManager authManager;
	private final JwtUtil jwtUtil;
	
	public AuthController(UserService service, AuthenticationManager authManager) {
		this.service = service;
		this.authManager = authManager;
		this.jwtUtil = new JwtUtil();
	}
	
	@PostMapping("/register")
	public User register(@RequestBody RegisterRequest request) throws Exception
	{
		return service.Register(request);
	}
	
	@PostMapping("/login")
	public AuthResponse login(@RequestBody LoginRequest request) {
		authManager.authenticate(new UsernamePasswordAuthenticationToken(request.userName(), request.password()));
		String token = jwtUtil.generateToken(request.userName());
		return new AuthResponse(token);
		
	}
}
