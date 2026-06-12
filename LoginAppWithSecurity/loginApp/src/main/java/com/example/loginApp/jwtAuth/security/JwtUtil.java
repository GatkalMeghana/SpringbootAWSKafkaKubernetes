package com.example.loginApp.jwtAuth.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private static final String SECRETE = "mysecretkeymysecretkeymysecretkey123456";
	private final SecretKey key = Keys.hmacShaKeyFor(SECRETE.getBytes(StandardCharsets.UTF_8));

	public String generateToken(String userName) {
		return Jwts.builder().subject(userName).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + 8640000)).signWith(key).compact();
	}
	
	public String extractUserName(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
	}

}
