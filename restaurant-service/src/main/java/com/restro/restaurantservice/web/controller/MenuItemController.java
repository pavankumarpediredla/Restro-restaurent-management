package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.MenuItemService;
import com.restro.restaurantservice.web.dto.MenuItemRequest;
import com.restro.restaurantservice.web.dto.MenuItemResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/items")
public class MenuItemController {

	private final MenuItemService menuItemService;

	public MenuItemController(MenuItemService menuItemService) {
		this.menuItemService = menuItemService;
	}

	@GetMapping
	public List<MenuItemResponse> list() {
		return menuItemService.list();
	}

	@GetMapping("/{id}")
	public MenuItemResponse get(@PathVariable Long id) {
		return menuItemService.get(id);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest request) {
		MenuItemResponse response = menuItemService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	public ResponseEntity<MenuItemResponse> update(@PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(menuItemService.update(id, request));
	}
}
