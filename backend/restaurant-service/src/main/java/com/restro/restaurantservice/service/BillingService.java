package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.Customer;
import com.restro.restaurantservice.domain.entity.Invoice;
import com.restro.restaurantservice.domain.entity.RestaurantOrder;
import com.restro.restaurantservice.domain.enums.InvoiceStatus;
import com.restro.restaurantservice.domain.enums.OrderStatus;
import com.restro.restaurantservice.domain.exception.BusinessRuleException;
import com.restro.restaurantservice.domain.exception.ResourceNotFoundException;
import com.restro.restaurantservice.repository.InvoiceRepository;
import com.restro.restaurantservice.repository.RestaurantOrderRepository;
import com.restro.restaurantservice.web.dto.InvoiceCreateRequest;
import com.restro.restaurantservice.web.dto.InvoiceResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingService {

	private final InvoiceRepository invoiceRepository;
	private final RestaurantOrderRepository orderRepository;

	public BillingService(InvoiceRepository invoiceRepository, RestaurantOrderRepository orderRepository) {
		this.invoiceRepository = invoiceRepository;
		this.orderRepository = orderRepository;
	}

	@Transactional
	public InvoiceResponse create(InvoiceCreateRequest request) {
		RestaurantOrder order = requireOrder(request.orderId());
		if (invoiceRepository.findByOrderId(order.getId()).isPresent()) {
			throw new BusinessRuleException("Invoice already exists for order " + order.getOrderNumber());
		}
		if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new BusinessRuleException("Cannot bill a cancelled order.");
		}

		BigDecimal subtotal = normalize(order.getTotalAmount());
		BigDecimal taxAmount = normalize(request.taxAmount() == null ? BigDecimal.ZERO : request.taxAmount());
		BigDecimal discountAmount = normalize(request.discountAmount() == null ? BigDecimal.ZERO : request.discountAmount());
		BigDecimal total = normalize(subtotal.add(taxAmount).subtract(discountAmount));

		Invoice invoice = new Invoice();
		invoice.setInvoiceNumber(generateInvoiceNumber());
		invoice.setOrder(order);
		invoice.setCustomer(order.getCustomer());
		invoice.setSubtotal(subtotal);
		invoice.setTaxAmount(taxAmount);
		invoice.setDiscountAmount(discountAmount);
		invoice.setTotalAmount(total);
		invoice.setStatus(InvoiceStatus.ISSUED);
		invoice.setNotes(trimToNull(request.notes()));
		return map(invoiceRepository.save(invoice));
	}

	@Transactional
	public InvoiceResponse markPaid(Long id) {
		Invoice invoice = requireInvoice(id);
		invoice.setStatus(InvoiceStatus.PAID);
		invoice.setPaidAt(Instant.now());
		return map(invoice);
	}

	@Transactional(readOnly = true)
	public List<InvoiceResponse> list() {
		return invoiceRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::map)
				.toList();
	}

	@Transactional(readOnly = true)
	public InvoiceResponse get(Long id) {
		return map(requireInvoice(id));
	}

	private RestaurantOrder requireOrder(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
	}

	private Invoice requireInvoice(Long id) {
		return invoiceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + id));
	}

	private InvoiceResponse map(Invoice invoice) {
		Customer customer = invoice.getCustomer();
		RestaurantOrder order = invoice.getOrder();
		return new InvoiceResponse(
				invoice.getId(),
				invoice.getInvoiceNumber(),
				order.getId(),
				order.getOrderNumber(),
				customer == null ? null : customer.getId(),
				customer == null ? null : customer.getFullName(),
				invoice.getStatus(),
				normalize(invoice.getSubtotal()),
				normalize(invoice.getTaxAmount()),
				normalize(invoice.getDiscountAmount()),
				normalize(invoice.getTotalAmount()),
				invoice.getCreatedAt(),
				invoice.getPaidAt(),
				invoice.getNotes());
	}

	private BigDecimal normalize(BigDecimal value) {
		return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
	}

	private String generateInvoiceNumber() {
		return "INV-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
