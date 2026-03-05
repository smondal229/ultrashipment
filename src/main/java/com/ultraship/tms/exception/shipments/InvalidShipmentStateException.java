package com.ultraship.tms.exception.shipments;

public class InvalidShipmentStateException extends RuntimeException {
    public InvalidShipmentStateException(String message) {
        super(message);
    }
}
