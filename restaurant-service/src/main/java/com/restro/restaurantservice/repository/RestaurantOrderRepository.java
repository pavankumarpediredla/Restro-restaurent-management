package com.restro.restaurantservice.repository;

import com.restro.restaurantservice.domain.entity.RestaurantOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {

	List<RestaurantOrder> findAllByOrderByCreatedAtDesc();

	Optional<RestaurantOrder> findFirstByRestaurantTableIdAndStatusNotOrderByCreatedAtDesc(Long tableId, com.restro.restaurantservice.domain.enums.OrderStatus status);
}
