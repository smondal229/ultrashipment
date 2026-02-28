package com.ultraship.tms.service;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.graphql.utils.BaseRateCalculator;

import com.ultraship.tms.rule.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class CarrierRateFactory {

    public CarrierRateCalculator get(Carrier carrier) {

        return switch (carrier) {

            case DHL -> buildDhl();
            case FEDEX -> buildFedex();
            case UPS -> buildUps();
            case BLUEDART -> buildBlueDart();
            case DELHIVERY -> buildDelhivery();
        };
    }

    private CarrierRateCalculator buildFedex() {

        BaseRateCalculator base = new BaseRateCalculator(
                new BigDecimal("25"),
                new BigDecimal("20"),
                new BigDecimal("5000")
        );

        List<PricingRule> rules = List.of(
                new InternationalRule(new BigDecimal("32")),
                new FuelSurchargeRule(new BigDecimal("17.928")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.12"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.28")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(base, rules);
    }

    private CarrierRateCalculator buildDhl() {

        BaseRateCalculator base = new BaseRateCalculator(
                new BigDecimal("25"),   // base fee (higher premium)
                new BigDecimal("22"),   // per kg
                new BigDecimal("5000")  // volumetric divisor
        );

        List<PricingRule> rules = List.of(
                new InternationalRule(new BigDecimal("350")),
                new FuelSurchargeRule(new BigDecimal("14")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.15"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.30")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(base, rules);
    }

    private CarrierRateCalculator buildUps() {

        BaseRateCalculator base = new BaseRateCalculator(
                new BigDecimal("22"),   // base fee
                new BigDecimal("20"),   // per kg
                new BigDecimal("5000")  // volumetric divisor
        );

        List<PricingRule> rules = List.of(
                new InternationalRule(new BigDecimal("300")),
                new FuelSurchargeRule(new BigDecimal("18")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.10"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.25")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(base, rules);
    }

    private CarrierRateCalculator buildBlueDart() {

        BaseRateCalculator base = new BaseRateCalculator(
                new BigDecimal("18"),
                new BigDecimal("6"),
                new BigDecimal("4000")
        );

        List<PricingRule> rules = List.of(
                new InternationalRule(new BigDecimal("250")),
                new FuelSurchargeRule(new BigDecimal("15")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.12"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.30")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(base, rules);
    }

    private CarrierRateCalculator buildDelhivery() {

        BaseRateCalculator base = new BaseRateCalculator(
                new BigDecimal("28"),
                new BigDecimal("6"),
                new BigDecimal("4000")
        );

        List<PricingRule> rules = List.of(
                new InternationalRule(new BigDecimal("200")),
                new FuelSurchargeRule(new BigDecimal("18")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.08"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.20")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(base, rules);
    }
}
