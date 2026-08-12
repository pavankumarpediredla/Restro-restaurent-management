package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
		@NotBlank String username,
		@NotBlank String password,
		String displayName,
		@NotNull UserRole role,
		Boolean enabled) {
}
