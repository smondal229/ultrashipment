ALTER TABLE shipments ADD COLUMN current_location VARCHAR(255) DEFAULT 'NA';

ALTER TABLE shipment_tracking ADD COLUMN event_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_tracking_shipment_id_created_at
ON shipment_tracking(shipment_id, created_at DESC);
