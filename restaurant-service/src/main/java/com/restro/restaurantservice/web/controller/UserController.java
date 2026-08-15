package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.UserService;
import com.restro.restaurantservice.web.dto.CreateUserRequest;
import com.restro.restaurantservice.web.dto.UserResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
	public List<UserResponse> list() {
		return userService.list();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
	public UserResponse get(@PathVariable Long id) {
		return userService.get(id);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
	public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
		UserResponse response = userService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@PatchMapping("/{id}/enabled")
	@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
	public ResponseEntity<UserResponse> updateEnabled(@PathVariable Long id, @RequestBody boolean enabled) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.setEnabled(id, enabled));
	}
}
