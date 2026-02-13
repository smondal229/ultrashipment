ALTER TABLE shipments ADD CONSTRAINT uq_tracking_carrier UNIQUE (tracking_number, carrier_name);
