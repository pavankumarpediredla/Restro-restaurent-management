package com.restro.restaurantservice.domain.entity;

import com.restro.restaurantservice.domain.enums.PriceCycle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "item_prices", uniqueConstraints = @UniqueConstraint(name = "uk_item_prices_item_cycle", columnNames = {"menu_item_id", "price_cycle"}))
public class ItemPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "price_cycle", nullable = false, length = 20)
	private PriceCycle cycle;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItem menuItem;

	public ItemPrice() {
	}

	public ItemPrice(PriceCycle cycle, BigDecimal amount) {
		this.cycle = cycle;
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}

	public PriceCycle getCycle() {
		return cycle;
	}

	public void setCycle(PriceCycle cycle) {
		this.cycle = cycle;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}
}
