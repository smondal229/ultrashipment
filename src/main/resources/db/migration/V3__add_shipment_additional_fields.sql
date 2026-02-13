-- Add audit fields
ALTER TABLE shipments ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Add lifecycle timestamps
ALTER TABLE shipments
    ADD COLUMN picked_up_at TIMESTAMP,
    ADD COLUMN delivered_at TIMESTAMP;

-- Add delivery type enum (stored as VARCHAR)
ALTER TABLE shipments
    ADD COLUMN shipment_delivery_type VARCHAR(50) NOT NULL DEFAULT 'STANDARD';

-- Add physical dimensions
ALTER TABLE shipments
    ADD COLUMN weight_gm DOUBLE PRECISION,
    ADD COLUMN length_cm DOUBLE PRECISION,
    ADD COLUMN width_cm DOUBLE PRECISION,
    ADD COLUMN height_cm DOUBLE PRECISION;

-- Add payment metadata as JSONB
ALTER TABLE shipments
    ADD COLUMN payment_meta JSONB;
