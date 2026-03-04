package com.ultraship.tms.service;

import com.ultraship.tms.domain.Carrier;
import com.ultraship.tms.domain.ShipmentDeliveryType;
import com.ultraship.tms.graphql.model.WeightSlab;
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

        BigDecimal volumetricDivisor = new BigDecimal("5000");

        List<CalculationRule> calculationRules = List.of(
                new ChargeableWeightRule(volumetricDivisor)
        );

        List<WeightSlab> slabs = List.of(
                new WeightSlab(
                        new BigDecimal("0"),
                        new BigDecimal("5"),
                        new BigDecimal("120")
                ),
                new WeightSlab(
                        new BigDecimal("5.01"),
                        new BigDecimal("20"),
                        new BigDecimal("95")
                ),
                new WeightSlab(
                        new BigDecimal("20.01"),
                        new BigDecimal("100"),
                        new BigDecimal("85")
                ),
                new WeightSlab(
                        new BigDecimal("100.01"),
                        new BigDecimal("999"),
                        new BigDecimal("75")
                ),
                new WeightSlab(
                        new BigDecimal("999.01"),
                        new BigDecimal("9999"),
                        new BigDecimal("60")
                )
        );

        List<PricingRule> pricingRules = List.of(
                new SlabPricingRule(slabs),
                new InternationalRule(new BigDecimal("320")),
                new FuelSurchargeRule(new BigDecimal("17.928")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.12"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.28")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(calculationRules, pricingRules);
    }

    private CarrierRateCalculator buildDhl() {

        BigDecimal volumetricDivisor = new BigDecimal("5000");

        List<CalculationRule> calculationRules = List.of(
                new ChargeableWeightRule(volumetricDivisor)
        );

        List<WeightSlab> slabs = List.of(
                new WeightSlab(
                        new BigDecimal("0"),
                        new BigDecimal("5"),
                        new BigDecimal("120")
                ),
                new WeightSlab(
                        new BigDecimal("5.01"),
                        new BigDecimal("20"),
                        new BigDecimal("105")
                ),
                new WeightSlab(
                        new BigDecimal("20.01"),
                        new BigDecimal("999"),
                        new BigDecimal("90")
                ),
                new WeightSlab(
                        new BigDecimal("999.01"),
                        new BigDecimal("9999"),
                        new BigDecimal("55")
                )
        );


        List<PricingRule> pricingRules = List.of(
                new SlabPricingRule(slabs),
                new InternationalRule(new BigDecimal("350")),
                new FuelSurchargeRule(new BigDecimal("14")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.15"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.30")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(calculationRules, pricingRules);
    }

    private CarrierRateCalculator buildUps() {
        BigDecimal volumetricDivisor = new BigDecimal("5000");

        List<CalculationRule> calculationRules = List.of(
                new ChargeableWeightRule(volumetricDivisor)
        );

        List<WeightSlab> slabs = List.of(
                new WeightSlab(
                        new BigDecimal("0"),
                        new BigDecimal("5"),
                        new BigDecimal("120")
                ),
                new WeightSlab(
                        new BigDecimal("5.01"),
                        new BigDecimal("50"),
                        new BigDecimal("100")
                ),
                new WeightSlab(
                        new BigDecimal("50.01"),
                        new BigDecimal("100"),
                        new BigDecimal("89.99")
                ),
                new WeightSlab(
                        new BigDecimal("100.01"),
                        new BigDecimal("500"),
                        new BigDecimal("69.99")
                ),
                new WeightSlab(
                        new BigDecimal("500.01"),
                        new BigDecimal("9999"),
                        new BigDecimal("55.99")
                )
        );

        List<PricingRule> pricingRules = List.of(
                new SlabPricingRule(slabs),
                new InternationalRule(new BigDecimal("300")),
                new FuelSurchargeRule(new BigDecimal("18")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.10"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.25")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(calculationRules, pricingRules);
    }

    private CarrierRateCalculator buildBlueDart() {
        BigDecimal volumetricDivisor = new BigDecimal("4000");

        List<CalculationRule> calculationRules = List.of(
                new ChargeableWeightRule(volumetricDivisor)
        );

        List<WeightSlab> slabs = List.of(
                new WeightSlab(
                        new BigDecimal("0"),
                        new BigDecimal("5"),
                        new BigDecimal("120")
                ),
                new WeightSlab(
                        new BigDecimal("5.01"),
                        new BigDecimal("20"),
                        new BigDecimal("105")
                ),
                new WeightSlab(
                        new BigDecimal("20.01"),
                        new BigDecimal("99"),
                        new BigDecimal("91.29")
                ),
                new WeightSlab(
                        new BigDecimal("99.01"),
                        new BigDecimal("999"),
                        new BigDecimal("81.29")
                ),
                new WeightSlab(
                        new BigDecimal("999.01"),
                        new BigDecimal("9999"),
                        new BigDecimal("61.29")
                )
        );

        List<PricingRule> pricingRules = List.of(
                new SlabPricingRule(slabs),
                new InternationalRule(new BigDecimal("250")),
                new FuelSurchargeRule(new BigDecimal("15")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.12"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.30")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(calculationRules, pricingRules);
    }

    private CarrierRateCalculator buildDelhivery() {
        BigDecimal volumetricDivisor = new BigDecimal("4000");

        List<CalculationRule> calculationRules = List.of(
                new ChargeableWeightRule(volumetricDivisor)
        );

        List<WeightSlab> slabs = List.of(
                new WeightSlab(
                        new BigDecimal("0"),
                        new BigDecimal("5"),
                        new BigDecimal("120")
                ),
                new WeightSlab(
                        new BigDecimal("5.01"),
                        new BigDecimal("49"),
                        new BigDecimal("105")
                ),
                new WeightSlab(
                        new BigDecimal("49.01"),
                        new BigDecimal("100"),
                        new BigDecimal("95")
                ),
                new WeightSlab(
                        new BigDecimal("100.01"),
                        new BigDecimal("999"),
                        new BigDecimal("85")
                ),
                new WeightSlab(
                        new BigDecimal("999.01"),
                        new BigDecimal("9999"),
                        new BigDecimal("65")
                )
        );

        List<PricingRule> pricingRules = List.of(
                new SlabPricingRule(slabs),
                new InternationalRule(new BigDecimal("200")),
                new FuelSurchargeRule(new BigDecimal("18")),
                new DeliveryPercentageRule(Map.of(
                        ShipmentDeliveryType.STANDARD, BigDecimal.ZERO,
                        ShipmentDeliveryType.EXPRESS, new BigDecimal("0.08"),
                        ShipmentDeliveryType.SAME_DAY, new BigDecimal("0.20")
                )),
                new GstRule(new BigDecimal("0.18"))
        );

        return new CarrierRateCalculator(calculationRules, pricingRules);
    }
}
