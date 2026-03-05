package com.ultraship.tms.domain.model;

import com.ultraship.tms.domain.enums.PaymentStatus;

public record PaymentMeta (
    String transactionId,
    String provider,
    String currency,
    String paymentMethod,
    String gatewayResponseCode,
    PaymentStatus status
) {}
