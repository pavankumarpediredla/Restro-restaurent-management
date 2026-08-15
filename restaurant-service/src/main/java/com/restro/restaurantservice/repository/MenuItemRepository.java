package com.restro.restaurantservice.repository;

import com.restro.restaurantservice.domain.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
