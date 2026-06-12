package com.example.loginApp.jwtAuth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.loginApp.jwtAuth.dto.RegisterRequest;
import com.example.loginApp.jwtAuth.entity.User;
import com.example.loginApp.jwtAuth.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository repository;
	private final PasswordEncoder encoder;
	
	public UserService(UserRepository repository,PasswordEncoder encoder) {
		this.repository=repository;
		this.encoder=encoder;
	}
	
	public User Register(RegisterRequest request) throws Exception {
		if(repository.findByUserName(request.userName()).isPresent()) {
			throw new Exception("USer already exists");
		}
		
		User user = new User();
		user.setUserName(request.userName());
		user.setPassword(encoder.encode(request.password()));
		user.setRole("USER");
		return repository.save(user);
	}
}
