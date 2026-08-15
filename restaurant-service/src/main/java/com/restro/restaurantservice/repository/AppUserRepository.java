package com.restro.restaurantservice.repository;

import com.restro.restaurantservice.domain.entity.AppUser;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByUsername(String username);

	boolean existsByUsername(String username);
}
