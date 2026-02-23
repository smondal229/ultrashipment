package com.ultraship.tms.domain;

public interface TrackingNumberGenerator {
  String generate(Carrier carrier);
}
