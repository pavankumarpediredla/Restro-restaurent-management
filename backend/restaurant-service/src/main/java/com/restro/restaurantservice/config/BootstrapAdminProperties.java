package com.restro.restaurantservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "restro.bootstrap-admin")
public record BootstrapAdminProperties(
		String username,
		String password,
		String name) {
}
