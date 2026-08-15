package com.restro.restaurantservice.repository;
import com.restro.restaurantservice.domain.entity.Holiday;
import java.time.LocalDate; import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface HolidayRepository extends JpaRepository<Holiday, Long> { List<Holiday> findByHolidayDateBetweenOrderByHolidayDate(LocalDate from, LocalDate to); boolean existsByHolidayDate(LocalDate date); }
