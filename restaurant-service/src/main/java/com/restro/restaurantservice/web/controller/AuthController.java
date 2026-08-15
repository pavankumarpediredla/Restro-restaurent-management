package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.AuthService;
import com.restro.restaurantservice.web.dto.LoginRequest;
import com.restro.restaurantservice.web.dto.LoginResponse;
import com.restro.restaurantservice.web.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}

	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public UserResponse me(Authentication authentication) {
		return authService.currentUser(authentication.getName());
	}
}
