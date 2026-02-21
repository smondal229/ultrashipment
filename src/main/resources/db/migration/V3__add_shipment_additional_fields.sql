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
    ADD COLUMN item_weight NUMERIC(10,2),
    ADD COLUMN item_length NUMERIC(10,2),
    ADD COLUMN item_width NUMERIC(10,2),
    ADD COLUMN item_height NUMERIC(10,2),
    ADD COLUMN weight_unit VARCHAR(20),
    ADD COLUMN dim_unit VARCHAR(20);

ALTER TABLE shipments
    ADD COLUMN item_value NUMERIC(15,2) DEFAULT 0.0;

-- Add payment metadata as JSONB
ALTER TABLE shipments
    ADD COLUMN payment_meta JSONB;
