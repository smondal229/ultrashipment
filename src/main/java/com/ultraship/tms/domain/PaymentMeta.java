package com.ultraship.tms.domain;

public record PaymentMeta (
    String transactionId,
    String provider,
    String currency,
    String paymentMethod,
    String gatewayResponseCode
) {}
