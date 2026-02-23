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

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "pickup_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "pickup_postal_code")),
            @AttributeOverride(name = "state", column = @Column(name = "pickup_state")),
            @AttributeOverride(name = "country", column = @Column(name = "pickup_country")),
            @AttributeOverride(name = "street", column = @Column(name = "pickup_street")),
            @AttributeOverride(name = "contactNumber", column = @Column(name = "pickup_contact_number"))
    })
    private Address pickupAddress;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city", column = @Column(name = "delivery_city")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "delivery_postal_code")),
            @AttributeOverride(name = "state", column = @Column(name = "delivery_state")),
            @AttributeOverride(name = "country", column = @Column(name = "delivery_country")),
            @AttributeOverride(name = "street", column = @Column(name = "delivery_street")),
            @AttributeOverride(name = "contactNumber", column = @Column(name = "delivery_contact_number"))
    })
    private Address deliveryAddress;

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

    @Column(precision = 10, scale = 2)
    private BigDecimal itemValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal itemWeight;
    @Column(precision = 10, scale = 2)
    private BigDecimal itemLength;
    @Column(precision = 10, scale = 2)
    private BigDecimal itemWidth;
    @Column(precision = 10, scale = 2)
    private BigDecimal itemHeight;

    @Enumerated(EnumType.STRING)
    private LengthUnit dimUnit;

    @Enumerated(EnumType.STRING)
    private WeightUnit weightUnit;

    @Column(nullable = false)
    Boolean deleted = false;

    @Column(nullable = false)
    Boolean isFlagged = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private PaymentMeta paymentMeta;

    @OneToMany(mappedBy = "shipment", fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<ShipmentTrackingEntity> tracking = new ArrayList<>();
}
