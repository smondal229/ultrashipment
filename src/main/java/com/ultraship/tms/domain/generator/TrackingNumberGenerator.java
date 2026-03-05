package com.ultraship.tms.domain.generator;

import com.ultraship.tms.domain.enums.Carrier;

public interface TrackingNumberGenerator {
  String generate(Carrier carrier);
}
