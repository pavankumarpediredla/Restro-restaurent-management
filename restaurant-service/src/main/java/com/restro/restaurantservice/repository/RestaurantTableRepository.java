package com.restro.restaurantservice.repository;
import com.restro.restaurantservice.domain.entity.RestaurantTable; import java.util.*;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable,Long>{
 Optional<RestaurantTable> findByTableNumber(String number); List<RestaurantTable> findByActiveTrueOrderByTableNumberAsc();
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select t from RestaurantTable t where t.id=:id") Optional<RestaurantTable> findByIdForUpdate(@Param("id") Long id);
}
