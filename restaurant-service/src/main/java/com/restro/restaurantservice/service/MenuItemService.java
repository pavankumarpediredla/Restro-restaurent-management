package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.ItemPrice;
import com.restro.restaurantservice.domain.entity.MenuItem;
import com.restro.restaurantservice.domain.enums.PriceCycle;
import com.restro.restaurantservice.domain.exception.BusinessRuleException;
import com.restro.restaurantservice.domain.exception.ResourceNotFoundException;
import com.restro.restaurantservice.repository.MenuItemRepository;
import com.restro.restaurantservice.web.dto.ItemPriceRequest;
import com.restro.restaurantservice.web.dto.ItemPriceResponse;
import com.restro.restaurantservice.web.dto.MenuItemRequest;
import com.restro.restaurantservice.web.dto.MenuItemResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuItemService {

	private final MenuItemRepository menuItemRepository;

	public MenuItemService(MenuItemRepository menuItemRepository) {
		this.menuItemRepository = menuItemRepository;
	}

	@Transactional
	public MenuItemResponse create(MenuItemRequest request) {
		MenuItem item = new MenuItem();
		applyRequest(item, request, true);
		return toResponse(menuItemRepository.save(item));
	}

	@Transactional
	public MenuItemResponse update(Long id, MenuItemRequest request) {
		MenuItem item = menuItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));
		applyRequest(item, request, false);
		return toResponse(item);
	}

	@Transactional(readOnly = true)
	public MenuItemResponse get(Long id) {
		return toResponse(findItem(id));
	}

	@Transactional(readOnly = true)
	public List<MenuItemResponse> list() {
		return menuItemRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
				.map(this::toResponse)
				.toList();
	}

	private MenuItem findItem(Long id) {
		return menuItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + id));
	}

	private void applyRequest(MenuItem item, MenuItemRequest request, boolean isCreate) {
		item.setName(request.name().trim());
		item.setDescription(trimToNull(request.description()));
		item.setCategory(trimToNull(request.category()));
		if (isCreate) {
			item.setActive(request.active() == null || request.active());
		} else if (request.active() != null) {
			item.setActive(request.active());
		}
		applyStandardPrice(item, request.prices());
	}

	private void applyStandardPrice(MenuItem item, List<ItemPriceRequest> priceRequests) {
		if (priceRequests.size() != 1) {
			throw new BusinessRuleException("A menu item must have exactly one standard price.");
		}
		ItemPriceRequest request = priceRequests.getFirst();
		ItemPrice price = item.findPrice(PriceCycle.DAILY)
				.orElseGet(() -> item.getPrices().stream().findFirst().orElse(null));
		if (price == null) {
			price = new ItemPrice();
			item.addPrice(price);
		}
		// DAILY is retained internally for backwards-compatible order history.
		price.setCycle(PriceCycle.DAILY);
		price.setAmount(normalizeAmount(request.amount()));
		ItemPrice selectedPrice = price;
		item.getPrices().removeIf(existing -> existing != selectedPrice);
	}

	private MenuItemResponse toResponse(MenuItem item) {
		ItemPrice currentPrice = item.findPrice(PriceCycle.DAILY)
				.orElseGet(() -> item.getPrices().stream().findFirst().orElse(null));
		List<ItemPriceResponse> prices = currentPrice == null
				? List.of()
				: List.of(new ItemPriceResponse(PriceCycle.DAILY, normalizeAmount(currentPrice.getAmount())));
		return new MenuItemResponse(
				item.getId(),
				item.getName(),
				item.getDescription(),
				item.getCategory(),
				item.isActive(),
				prices,
				item.getCreatedAt(),
				item.getUpdatedAt());
	}

	private BigDecimal normalizeAmount(BigDecimal amount) {
		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
