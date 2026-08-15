package com.restro.restaurantservice.domain.entity;
import com.restro.restaurantservice.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="table_reservations") public class Reservation {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="table_id") private RestaurantTable table;
 @Column(nullable=false,length=100) private String customerName; @Column(length=30) private String phone;
 @Column(nullable=false) private Instant startsAt; @Column(nullable=false) private Instant endsAt; @Column(nullable=false) private int guestCount;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private ReservationStatus status=ReservationStatus.BOOKED; @Column(length=1000) private String notes;
 @Column(nullable=false,updatable=false) private Instant createdAt;
 @PrePersist void created(){createdAt=Instant.now();}
 public Long getId(){return id;} public RestaurantTable getTable(){return table;} public void setTable(RestaurantTable v){table=v;} public String getCustomerName(){return customerName;} public void setCustomerName(String v){customerName=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public Instant getStartsAt(){return startsAt;} public void setStartsAt(Instant v){startsAt=v;} public Instant getEndsAt(){return endsAt;} public void setEndsAt(Instant v){endsAt=v;} public int getGuestCount(){return guestCount;} public void setGuestCount(int v){guestCount=v;} public ReservationStatus getStatus(){return status;} public void setStatus(ReservationStatus v){status=v;} public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public Instant getCreatedAt(){return createdAt;}
}
