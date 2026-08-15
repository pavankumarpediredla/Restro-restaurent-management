package com.restro.restaurantservice.repository;
import com.restro.restaurantservice.domain.entity.WeekOff;
import java.util.List; import org.springframework.data.jpa.repository.JpaRepository;
public interface WeekOffRepository extends JpaRepository<WeekOff, Long> { List<WeekOff> findByStaff_Id(Long staffId); List<WeekOff> findByStaff_IdIn(List<Long> staffIds); void deleteByStaff_Id(Long staffId); }
