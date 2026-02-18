ALTER TABLE shipment_tracking ADD COLUMN event_id UUID;

ALTER TABLE shipment_tracking ADD CONSTRAINT uq_event_id UNIQUE (event_id);

DROP INDEX IF EXISTS idx_tracking_shipment_id;