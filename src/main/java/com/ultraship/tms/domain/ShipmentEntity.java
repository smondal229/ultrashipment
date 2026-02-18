package com.ultraship.tms.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "shipments")
@EntityListeners(AuditingEntityListener.class)
public class ShipmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String shipperName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Carrier carrierName;

    @Column(nullable = false)
    private String pickupLocation;
    @Column(nullable = false)
    private String deliveryLocation;

    private String currentLocation;

    @Column(unique = true)
    private String trackingNumber;

    private BigDecimal rate;

    @CreatedDate
    Instant createdAt;

    @LastModifiedDate
    Instant updatedAt;
    Instant pickedUpAt;
    Instant deliveredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentDeliveryType shipmentDeliveryType;

    private Double weightGm;
    private Double lengthCm;
    private Double widthCm;
    private Double heightCm;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PaymentMeta paymentMeta;

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<ShipmentTrackingEntity> tracking = new ArrayList<>();
}
