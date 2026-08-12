package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.domain.enums.UserRole;
import com.restro.restaurantservice.domain.exception.BusinessRuleException;
import com.restro.restaurantservice.domain.exception.ResourceNotFoundException;
import com.restro.restaurantservice.repository.AppUserRepository;
import com.restro.restaurantservice.web.dto.CreateUserRequest;
import com.restro.restaurantservice.web.dto.UserResponse;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

	private final AppUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UserResponse create(CreateUserRequest request) {
		if (userRepository.existsByUsername(request.username())) {
			throw new BusinessRuleException("Username already exists: " + request.username());
		}
		String displayName = request.displayName() == null || request.displayName().isBlank()
				? request.username()
				: request.displayName().trim();
		AppUser user = new AppUser(
				request.username().trim(),
				passwordEncoder.encode(request.password()),
				displayName,
				request.role());
		user.setEnabled(request.enabled() == null || request.enabled());
		return map(userRepository.save(user));
	}

	@Transactional(readOnly = true)
	public List<UserResponse> list() {
		return userRepository.findAll(Sort.by(Sort.Direction.ASC, "username")).stream()
				.map(this::map)
				.toList();
	}

	@Transactional(readOnly = true)
	public UserResponse get(Long id) {
		return map(requireUser(id));
	}

	@Transactional
	public UserResponse setEnabled(Long id, boolean enabled) {
		AppUser user = requireUser(id);
		user.setEnabled(enabled);
		return map(user);
	}

	private AppUser requireUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
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
