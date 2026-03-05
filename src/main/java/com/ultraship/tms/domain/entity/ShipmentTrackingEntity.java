package com.ultraship.tms.domain.entity;

import java.time.Instant;
import java.util.UUID;

import com.ultraship.tms.domain.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "shipment_tracking", uniqueConstraints = @UniqueConstraint(name = "uq_event_id", columnNames = "event_id"))
@EntityListeners(AuditingEntityListener.class)
@Data
public class ShipmentTrackingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ShipmentStatus status;

  @Column(nullable = false)
  private String location;

  private String description;

  @CreatedDate
  private Instant createdAt;

  private Instant eventTime;

  @Column(name = "event_id", nullable = false, unique = true)
  private UUID eventId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shipment_id", nullable = false)
  private ShipmentEntity shipment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id", updatable = false)
  private User createdBy;
}
