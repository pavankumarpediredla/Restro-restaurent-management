package com.restro.restaurantservice.domain.entity;

import com.restro.restaurantservice.domain.enums.PriceCycle;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "menu_items")
public class MenuItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 150)
	private String name;

	@Column(length = 1000)
	private String description;

	@Column(length = 100)
	private String category;

	@Column(nullable = false)
	private boolean active = true;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	@OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemPrice> prices = new ArrayList<>();

	public MenuItem() {
	}

	@PrePersist
	void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public List<ItemPrice> getPrices() {
		return prices;
	}

	public void replacePrices(List<ItemPrice> newPrices) {
		prices.clear();
		if (newPrices == null) {
			return;
		}
		newPrices.forEach(this::addPrice);
	}

	public void addPrice(ItemPrice price) {
		price.setMenuItem(this);
		prices.add(price);
	}

	public Optional<ItemPrice> findPrice(PriceCycle cycle) {
		return prices.stream()
				.filter(price -> price.getCycle() == cycle)
				.sorted(Comparator.comparing(ItemPrice::getCycle))
				.findFirst();
	}
}
