package com.restro.restaurantservice.domain.entity;

import com.restro.restaurantservice.domain.enums.PriceCycle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "order_lines")
public class OrderLine {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "restaurant_order_id", nullable = false)
	private RestaurantOrder restaurantOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_item_id", nullable = false)
	private MenuItem menuItem;

	@Enumerated(jakarta.persistence.EnumType.STRING)
	@Column(name = "price_cycle", nullable = false, length = 20)
	private PriceCycle priceCycle;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal unitPrice;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal lineTotal;

	public OrderLine() {
	}

	public Long getId() {
		return id;
	}

	public RestaurantOrder getRestaurantOrder() {
		return restaurantOrder;
	}

	public void setRestaurantOrder(RestaurantOrder restaurantOrder) {
		this.restaurantOrder = restaurantOrder;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public PriceCycle getPriceCycle() {
		return priceCycle;
	}

	public void setPriceCycle(PriceCycle priceCycle) {
		this.priceCycle = priceCycle;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}
}
