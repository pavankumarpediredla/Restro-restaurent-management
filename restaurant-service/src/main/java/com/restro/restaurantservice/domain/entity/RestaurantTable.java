package com.restro.restaurantservice.domain.entity;

import com.restro.restaurantservice.domain.enums.TableStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "restaurant_tables", uniqueConstraints = @UniqueConstraint(columnNames = "tableNumber"))
public class RestaurantTable {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=30) private String tableNumber;
 @Column(nullable=false,length=100) private String tableName;
 @Column(nullable=false) private int capacity;
 @Column(nullable=false,length=60) private String tableType;
 @Column(length=60) private String section;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private TableStatus status = TableStatus.AVAILABLE;
 @Column(nullable=false) private boolean active = true;
 @Column(length=80) private String heldBy;
 private Instant heldAt;
 private Instant holdExpiresAt;
 @Column(length=300) private String holdReason;
 @Version private Long version;
 public Long getId(){return id;} public String getTableNumber(){return tableNumber;} public void setTableNumber(String v){tableNumber=v;}
 public String getTableName(){return tableName;} public void setTableName(String v){tableName=v;} public int getCapacity(){return capacity;} public void setCapacity(int v){capacity=v;}
 public String getTableType(){return tableType;} public void setTableType(String v){tableType=v;} public String getSection(){return section;} public void setSection(String v){section=v;}
 public TableStatus getStatus(){return status;} public void setStatus(TableStatus v){status=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
 public String getHeldBy(){return heldBy;} public void setHeldBy(String v){heldBy=v;} public Instant getHeldAt(){return heldAt;} public void setHeldAt(Instant v){heldAt=v;} public Instant getHoldExpiresAt(){return holdExpiresAt;} public void setHoldExpiresAt(Instant v){holdExpiresAt=v;} public String getHoldReason(){return holdReason;} public void setHoldReason(String v){holdReason=v;}
 public void clearHold(){heldBy=null;heldAt=null;holdExpiresAt=null;holdReason=null;}
}
