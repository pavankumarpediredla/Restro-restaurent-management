package com.restro.restaurantservice.repository;

import com.restro.restaurantservice.domain.entity.Invoice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

	Optional<Invoice> findByOrderId(Long orderId);

	List<Invoice> findAllByOrderByCreatedAtDesc();
}
