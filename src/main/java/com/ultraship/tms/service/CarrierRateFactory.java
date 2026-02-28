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
                new BigDecimal("55"),
                new BigDecimal("0.89"),
                new BigDecimal("5000")
        );

        List<PricingRule> rules = List.of(
                new WeightSlabRule(new BigDecimal("15"), new BigDecimal("1.10")),

                new VolumeSlabRule(new BigDecimal("28"), new BigDecimal("0.035")),

                new InternationalRule(new BigDecimal("32")),

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
                new BigDecimal("60"),   // base fee (higher premium)
                new BigDecimal("0.79"),   // per kg
                new BigDecimal("5000")  // volumetric divisor
        );

        List<PricingRule> rules = List.of(

                // Weight over 12kg → $1.25 per extra kg
                new WeightSlabRule(new BigDecimal("12"), new BigDecimal("1.25")),

                // Volume over 30 (unit depends on your rule logic)
                new VolumeSlabRule(new BigDecimal("30"), new BigDecimal("0.04")),

                new InternationalRule(new BigDecimal("350")),

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
                new BigDecimal("50"),   // base fee
                new BigDecimal("0.95"),   // per kg
                new BigDecimal("5000")  // volumetric divisor
        );

        List<PricingRule> rules = List.of(

                // Weight slab rule
                new WeightSlabRule(new BigDecimal(10), new BigDecimal("1.15")),

                // Volume slab rule
                new VolumeSlabRule(new BigDecimal(25), new BigDecimal("0.03")),

                new InternationalRule(new BigDecimal("300")),

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
                new BigDecimal("40"),
                new BigDecimal("1.09"),
                new BigDecimal("5000")
        );

        List<PricingRule> rules = List.of(

                // Weight slab rule
                new WeightSlabRule(new BigDecimal(12), new BigDecimal("1.22")),

                // Volume slab rule
                new VolumeSlabRule(new BigDecimal(28), new BigDecimal("0.034")),

                new InternationalRule(new BigDecimal("250")),

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
                new BigDecimal("30"),
                new BigDecimal("0.99"),
                new BigDecimal("5000")
        );

        List<PricingRule> rules = List.of(

                // Weight slab rule
                new WeightSlabRule(new BigDecimal("10.54"), new BigDecimal("1.18")),

                // Volume slab rule
                new VolumeSlabRule(new BigDecimal("21.48"), new BigDecimal("0.0293")),

                new InternationalRule(new BigDecimal("200")),

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
