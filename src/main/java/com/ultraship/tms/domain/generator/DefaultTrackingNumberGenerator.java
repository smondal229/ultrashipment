package com.ultraship.tms.domain.generator;

import com.ultraship.tms.domain.enums.Carrier;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;


@Component
public class DefaultTrackingNumberGenerator implements TrackingNumberGenerator {

    private static final SecureRandom random = new SecureRandom();

    @Override
    public String generate(Carrier carrier) {
        String carrierCode = carrier.getCode().toUpperCase();

        long timestampPart = System.currentTimeMillis() % 1_000_000_000L; // 9 digits max
        int randomDigit = random.nextInt(10);

        String number = String.format("%09d%d", timestampPart, randomDigit);

        return carrierCode + number;
    }
}
