package com.ultraship.tms.graphql.model;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.domain.LengthUnit;
import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.domain.WeightUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PricingRequest {

    @NotNull
    Carrier carrierName;

    @NotNull
    private ShipmentDeliveryType deliveryType;

    @NotNull
    @DecimalMin(value = "0.0001", message = "Length must be greater than 0")
    private BigDecimal itemLength;

    @NotNull
    @DecimalMin(value = "0.0001", message = "Width must be greater than 0")
    private BigDecimal itemWidth;

    @NotNull
    @DecimalMin(value = "0.0001", message = "Height must be greater than 0")
    private BigDecimal itemHeight;

    @NotNull(message = "Length unit is required")
    private LengthUnit lengthUnit;

    @NotNull
    @DecimalMin(value = "0.0001", message = "Weight must be greater than 0")
    private BigDecimal itemWeight;

    @NotNull(message = "Weight unit is required")
    private WeightUnit weightUnit;

    @NotNull
    @DecimalMin(value = "0.0", message = "Item value cannot be negative")
    private BigDecimal itemValue;

    @NotBlank(message = "Pickup country is required")
    @Size(max = 100)
    private String pickupCountry;

    @NotBlank(message = "Delivery country is required")
    @Size(max = 100)
    private String deliveryCountry;
}