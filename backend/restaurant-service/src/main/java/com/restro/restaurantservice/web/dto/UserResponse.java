package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.UserRole;
import java.time.Instant;

public record UserResponse(
		Long id,
		String username,
		String displayName,
		UserRole role,
		boolean enabled,
		Instant createdAt) {
}
