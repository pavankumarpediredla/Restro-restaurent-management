package com.restro.restaurantservice.domain.entity;

import com.restro.restaurantservice.domain.enums.OrderStatus;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurant_orders")
public class RestaurantOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 40)
	private String orderNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@Column(length = 50)
	private String tableNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "table_id")
	private RestaurantTable restaurantTable;

	@Column(length = 100)
	private String customerName;

	@Column(length = 30)
	private String customerPhone;

	@Column(length = 1000)
	private String notes;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	private Instant acceptedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_user_id", nullable = false)
	private AppUser createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accepted_by_user_id")
	private AppUser acceptedBy;

	@OneToMany(mappedBy = "restaurantOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderLine> lines = new ArrayList<>();

	public RestaurantOrder() {
	}

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public String getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(String tableNumber) {
		this.tableNumber = tableNumber;
	}

	public RestaurantTable getRestaurantTable() { return restaurantTable; }
	public void setRestaurantTable(RestaurantTable restaurantTable) { this.restaurantTable = restaurantTable; }

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getAcceptedAt() {
		return acceptedAt;
	}

	public void setAcceptedAt(Instant acceptedAt) {
		this.acceptedAt = acceptedAt;
	}

	public AppUser getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(AppUser createdBy) {
		this.createdBy = createdBy;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public AppUser getAcceptedBy() {
		return acceptedBy;
	}

	public void setAcceptedBy(AppUser acceptedBy) {
		this.acceptedBy = acceptedBy;
	}

	public List<OrderLine> getLines() {
		return lines;
	}

	public void replaceLines(List<OrderLine> newLines) {
		lines.clear();
		if (newLines == null) {
			return;
		}
		newLines.forEach(this::addLine);
	}

	public void addLine(OrderLine line) {
		line.setRestaurantOrder(this);
		lines.add(line);
	}
}
