package com.restro.restaurantservice.domain.enums;

public enum UserRole {
	OWNER,
	ADMIN,
	MANAGER,
	CHEF,
	WAITER,
	CASHIER,
	CLEANER,
	SECURITY,
	// Retained only so existing kitchen accounts can still sign in.
	KITCHEN
}
