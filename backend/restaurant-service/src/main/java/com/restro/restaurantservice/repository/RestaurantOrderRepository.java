package com.restro.restaurantservice.repository;

import com.restro.restaurantservice.domain.entity.RestaurantOrder;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {

	List<RestaurantOrder> findAllByOrderByCreatedAtDesc();
}
