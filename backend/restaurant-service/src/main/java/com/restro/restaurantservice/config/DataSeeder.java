package com.restro.restaurantservice.config;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.domain.enums.UserRole;
import com.restro.restaurantservice.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

	private final AppUserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final BootstrapAdminProperties bootstrapAdminProperties;

	public DataSeeder(
			AppUserRepository userRepository,
			PasswordEncoder passwordEncoder,
			BootstrapAdminProperties bootstrapAdminProperties) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.bootstrapAdminProperties = bootstrapAdminProperties;
	}

	@Override
	public void run(String... args) {
		if (isBlank(bootstrapAdminProperties.username())
				|| isBlank(bootstrapAdminProperties.password())) {
			return;
		}
		ensureUser(
				bootstrapAdminProperties.username(),
				bootstrapAdminProperties.password(),
				bootstrapAdminProperties.name(),
				UserRole.ADMIN);
	}

	private void ensureUser(String username, String rawPassword, String displayName, UserRole role) {
		if (userRepository.existsByUsername(username)) {
			return;
		}
		String safeDisplayName = isBlank(displayName) ? username : displayName;
		userRepository.save(new AppUser(username, passwordEncoder.encode(rawPassword), safeDisplayName, role));
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
