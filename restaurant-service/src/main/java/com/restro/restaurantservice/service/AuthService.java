package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.domain.exception.UnauthorizedException;
import com.restro.restaurantservice.domain.exception.ResourceNotFoundException;
import com.restro.restaurantservice.repository.AppUserRepository;
import com.restro.restaurantservice.web.dto.LoginRequest;
import com.restro.restaurantservice.web.dto.LoginResponse;
import com.restro.restaurantservice.web.dto.UserResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final AuthenticationManager authenticationManager;
	private final AppUserRepository userRepository;

	public AuthService(AuthenticationManager authenticationManager, AppUserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.username(), request.password()));
		} catch (AuthenticationException ex) {
			throw new UnauthorizedException("Invalid username or password.");
		}
		AppUser user = userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + authentication.getName()));
		String token = "Basic " + Base64.getEncoder().encodeToString(
				(request.username() + ":" + request.password()).getBytes(StandardCharsets.UTF_8));
		return new LoginResponse(token, map(user));
	}

	@Transactional(readOnly = true)
	public UserResponse currentUser(String username) {
		return map(requireUser(username));
	}

	private AppUser requireUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
	}

	private UserResponse map(AppUser user) {
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getDisplayName(),
				user.getRole(),
				user.isEnabled(),
				user.getCreatedAt());
	}
}
