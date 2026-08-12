package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.Customer;
import com.restro.restaurantservice.domain.exception.BusinessRuleException;
import com.restro.restaurantservice.domain.exception.ResourceNotFoundException;
import com.restro.restaurantservice.repository.CustomerRepository;
import com.restro.restaurantservice.web.dto.CustomerRequest;
import com.restro.restaurantservice.web.dto.CustomerResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	@Transactional
	public CustomerResponse create(CustomerRequest request) {
		Customer customer = new Customer();
		apply(customer, request);
		return map(customerRepository.save(customer));
	}

	@Transactional
	public CustomerResponse update(Long id, CustomerRequest request) {
		Customer customer = requireCustomer(id);
		apply(customer, request);
		return map(customer);
	}

	@Transactional(readOnly = true)
	public List<CustomerResponse> list() {
		return customerRepository.findAll(Sort.by(Sort.Direction.ASC, "fullName")).stream()
				.map(this::map)
				.toList();
	}

	@Transactional(readOnly = true)
	public CustomerResponse get(Long id) {
		return map(requireCustomer(id));
	}

	@Transactional
	public Customer resolveForOrder(Long customerId, String fullName, String phone, String email, String address) {
		if (customerId != null) {
			return requireCustomer(customerId);
		}

		String name = trimToNull(fullName);
		String phoneValue = trimToNull(phone);
		String emailValue = trimToNull(email);
		String addressValue = trimToNull(address);

		if (name == null && phoneValue == null && emailValue == null) {
			return null;
		}

		Optional<Customer> existing = phoneValue == null
				? Optional.empty()
				: customerRepository.findByPhone(phoneValue);

		Customer customer = existing.orElseGet(Customer::new);
		if (name != null) {
			customer.setFullName(name);
		} else if (customer.getFullName() == null) {
			throw new BusinessRuleException("Customer name is required when creating a new customer.");
		}
		customer.setPhone(phoneValue);
		customer.setEmail(emailValue);
		customer.setAddress(addressValue);
		customer.setActive(true);
		return customerRepository.save(customer);
	}

	private void apply(Customer customer, CustomerRequest request) {
		customer.setFullName(request.fullName().trim());
		customer.setPhone(trimToNull(request.phone()));
		customer.setEmail(trimToNull(request.email()));
		customer.setAddress(trimToNull(request.address()));
		if (request.active() != null) {
			customer.setActive(request.active());
		}
	}

	private Customer requireCustomer(Long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
	}

	private CustomerResponse map(Customer customer) {
		return new CustomerResponse(
				customer.getId(),
				customer.getFullName(),
				customer.getPhone(),
				customer.getEmail(),
				customer.getAddress(),
				customer.isActive(),
				customer.getCreatedAt(),
				customer.getUpdatedAt());
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
