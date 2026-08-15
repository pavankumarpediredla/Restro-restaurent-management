package com.restro.restaurantservice.repository;
import com.restro.restaurantservice.domain.entity.Reservation; import java.time.Instant; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param;
public interface ReservationRepository extends JpaRepository<Reservation,Long>{
 @Query("select count(r)>0 from Reservation r where r.table.id=:tableId and r.status='BOOKED' and r.startsAt < :end and r.endsAt > :start") boolean hasOverlap(@Param("tableId") Long tableId,@Param("start") Instant start,@Param("end") Instant end);
}
